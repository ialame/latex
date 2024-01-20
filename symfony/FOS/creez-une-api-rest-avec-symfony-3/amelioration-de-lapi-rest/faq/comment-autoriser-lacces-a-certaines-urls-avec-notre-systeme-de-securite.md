Comme vous l'avez sans doute remarqué, une fois le système de sécurité est activé, seule la requête de création de token est autorisée. Mais dans les faits, il est assez courant d'avoir plusieurs appels d'API accessible sans authentification.

[[question]]
| Par exemple, comment autoriser les utilisateurs à s'inscrire ?

Actuellement cela est impossible mais nous pouvons corriger le tir très facilement.

Pour rappel, l'authentification est géré par la classe `AuthTokenAuthenticator` dont voici un extrait du code :

```php
<?php
# src/AppBundle/Security/AuthTokenAuthenticator.php

namespace AppBundle\Security;

// ...

class AuthTokenAuthenticator implements SimplePreAuthenticatorInterface, AuthenticationFailureHandlerInterface
{
    /**
    * Durée de validité du token en secondes, 12 heures
    */
    const TOKEN_VALIDITY_DURATION = 12 * 3600;

    protected $httpUtils;

    public function __construct(HttpUtils $httpUtils)
    {
        $this->httpUtils = $httpUtils;
    }

    public function createToken(Request $request, $providerKey)
    {
        $targetUrl = '/auth-tokens';
        // Si la requête est une création de token, aucune vérification n'est effectué
        if ($request->getMethod() === "POST" && $this->httpUtils->checkRequestPath($request, $targetUrl)) {
            return;
        }
      
        $authTokenHeader = $request->headers->get('X-Auth-Token');

        if (!$authTokenHeader) {
            throw new BadCredentialsException('X-Auth-Token header is required');
        }

        return new PreAuthenticatedToken(
            'anon.',
            $authTokenHeader,
            $providerKey
        );
    }
    // ...
}
```
Code: Le nouvel Authenticator

Pour vérifier si une requête a été faite sur une certaine URL, la méthode `checkRequestPath` peut utiliser une route comme nous l'avons spécifié dans l'extrait de code ci-dessus, mais aussi le nom d'une route.

Le code peut donc être simplifié en utilisant directement le nom pour la route `auth-tokens` : *post_auth_tokens*.

```php
<?php
# src/AppBundle/Security/AuthTokenAuthenticator.php

namespace AppBundle\Security;

// ...

class AuthTokenAuthenticator implements SimplePreAuthenticatorInterface, AuthenticationFailureHandlerInterface
{
    /**
    * Durée de validité du token en secondes, 12 heures
    */
    const TOKEN_VALIDITY_DURATION = 12 * 3600;

    protected $httpUtils;

    public function __construct(HttpUtils $httpUtils)
    {
        $this->httpUtils = $httpUtils;
    }

    public function createToken(Request $request, $providerKey)
    {
        $targetUrl = 'post_auth_tokens';
        // Si la requête est une création de token, aucune vérification n'est effectué
        if ($request->getMethod() === "POST" && $this->httpUtils->checkRequestPath($request, $targetUrl)) {
            return;
        }
      
        $authTokenHeader = $request->headers->get('X-Auth-Token');

        if (!$authTokenHeader) {
            throw new BadCredentialsException('X-Auth-Token header is required');
        }

        return new PreAuthenticatedToken(
            'anon.',
            $authTokenHeader,
            $providerKey
        );
    }
    // ...
}
```

Pour obtenir la liste des routes, nous pouvons utiliser la commande `php bin/console debug:router`.

```txt
 get_places                 GET      ANY      ANY    /places
 get_place                  GET      ANY      ANY    /places/{id}
 post_places                POST     ANY      ANY    /places
 remove_place               DELETE   ANY      ANY    /places/{id}
 update_place               PUT      ANY      ANY    /places/{id}
 patch_place                PATCH    ANY      ANY    /places/{id}
 get_prices                 GET      ANY      ANY    /places/{id}/prices
 post_prices                POST     ANY      ANY    /places/{id}/prices
 get_themes                 GET      ANY      ANY    /places/{id}/themes
 post_themes                POST     ANY      ANY    /places/{id}/themes
 get_users                  GET      ANY      ANY    /users
 get_user                   GET      ANY      ANY    /users/{id}
 post_users                 POST     ANY      ANY    /users
 remove_user                DELETE   ANY      ANY    /users/{id}
 update_user                PUT      ANY      ANY    /users/{id}
 patch_user                 PATCH    ANY      ANY    /users/{id}
 get_user_suggestions       GET      ANY      ANY    /users/{id}/suggestions
 post_auth_tokens           POST     ANY      ANY    /auth-tokens
 remove_auth_token          DELETE   ANY      ANY    /auth-tokens/{id}
 get_preferences            GET      ANY      ANY    /users/{id}/preferences
 post_preferences           POST     ANY      ANY    /users/{id}/preferences
```

Avec le système de nommage de *FOSRestBundle*, nous avons des noms simples et **surtout qui décrivent aussi le verbe HTTP associé** à la route. Dés lors pour autoriser une action, nous pouvons nous baser uniquement sur le nom de la route correspondante (le verbe HTTP est vérifiée indirectement). 

Ainsi pour autoriser la création d'utilisateurs et de tokens d'authentification, nous pouvons simplement utiliser respectivement les routes : *post_users* et *post_auth_tokens*.

Le code peut devenir :

```php
<?php
# src/AppBundle/Security/AuthTokenAuthenticator.php

namespace AppBundle\Security;

// ...

class AuthTokenAuthenticator implements SimplePreAuthenticatorInterface, AuthenticationFailureHandlerInterface
{
    /**
    * Durée de validité du token en secondes, 12 heures
    */
    const TOKEN_VALIDITY_DURATION = 12 * 3600;

    public function __construct()
    {
    }

    public function createToken(Request $request, $providerKey)
    {
        $autorisedPaths = [
            'post_users', // Création d'un utilisateur (inscription)
            'post_auth_tokens' // Création d'un token (connexion)
        ];

        $currentRoute = $request->attributes->get('_route');
        
        if (in_array($currentRoute, $autorisedPaths)) {
            return;
        }
      
        $authTokenHeader = $request->headers->get('X-Auth-Token');

        if (!$authTokenHeader) {
            throw new BadCredentialsException('X-Auth-Token header is required');
        }

        return new PreAuthenticatedToken(
            'anon.',
            $authTokenHeader,
            $providerKey
        );
    }
    // ...
}
```

Le service `HttpUtils` étant maintenant inutile, nous pouvons même le retirer de la configuration des services.

```yml
# app/config/services.yml

auth_token_authenticator:
    class:     AppBundle\Security\AuthTokenAuthenticator
    # arguments: ["@security.http_utils"] # à supprimer ou à commenter
    public:    false
```
Code: Désactivation du service HTTPUtils

Bien sur, libre à vous de gérer la liste de routes autorisées comme bon vous semble (en injectant un paramètre configurable dans le service, en ayant une liste dans une variable statique, etc.).