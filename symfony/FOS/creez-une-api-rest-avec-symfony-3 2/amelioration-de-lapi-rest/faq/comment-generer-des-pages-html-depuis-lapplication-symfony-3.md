La configuration présentée durant ce cours implique que toute l'application ne génère que des réponses en JSON ou en XML. Cependant, il peut arriver qu'une même application puisse servir des réponses en JSON, en HTML voire en CSV.

Pour ce faire nous pouvons utiliser deux options que propose *FOSRestBundle*.

# Utiliser plusieurs règles dans le `format_listener`

Dans notre fichier de configuration, nous avions :

```yml
fos_rest:
    serializer:
        serialize_null:   true

    routing_loader:
        include_format: false
    view:
        view_response_listener: true
        formats:
            json: true
            xml: true
    format_listener:
        rules:
            - { path: '^/', priorities: ['json', 'xml'], fallback_format: 'json', prefer_extension: false }
```
Code: configuration du `format_listener`

Pour générer une page HTML, nous pouvons rajouter une nouvelle règle dans la clé `format_listener.rules`. L'ordre de déclaration étant important, il faut toujours déclarer les règles les plus spécifiques en premier.

```yml
    view:
        view_response_listener: true
        formats:
            json: true
            xml: true
        templating_formats:
            html: true
    format_listener:
        rules:
            - { path: '^/route/json', priorities: ['json'], fallback_format: 'json', prefer_extension: false }
            - { path: '^/route', priorities: ['html'], fallback_format: 'html', prefer_extension: false }
            - { path: '^/', priorities: ['json', 'xml'], fallback_format: 'json', prefer_extension: false }
```
Code: Une nouvelle route pour créer du HTML

Avec cette configuration, toutes les URLs commençant par `/route/json` renverront du JSON. Par contre, si l'URL commence par `/route` (mais sans la partie `/json`, `/route/other` par exemple) les réponses seront en HTML.

Si nous avions inversé ces deux règles, toutes les URLs `/route/json` renverraient aussi du HTML car l'expression régulière `^/route` englobe aussi `^/route/json`.


[[information]]
| Comme pour les formats JSON et XML, la génération de réponse au format HTML est déjà supporté par défaut. Mais en rajoutant la clé `templating_formats.html`, la configuration est plus lisible. De plus, nous utilisons `templating_formats` au lieu de `formats` car pour les pages HTML, nous aurons besoin d'un *template* pour les afficher.

Nous pouvons rajouter autant de règles que nous voulons mais cela peut rapidement montrer ses limites. Nous avons ainsi la possibilité d'utiliser un autre système plus efficace pour isoler la partie API et la partie IHM[^ihm] de son application.

# Configurer le `zone_listener` 

Il existe un *listener* de *FOSRestBundle* que nous n'avons pas abordé qui permet d'isoler la partie API d'une application de manière très simple : le `zone_listener`.

Le `zone_listener` est un *listener* qui nous permet de désactiver toutes les fonctionnalités de *FOSRestBundle* pour un ensemble d'URLs.

Ajoutons d'abord un préfixe `/api` à toutes les routes de notre API. La déclaration des routes pourrait ressembler à :

```yaml
places:
    type:     rest
    resource: AppBundle\Controller\PlaceController
    prefix: /api

prices:
    type:     rest
    resource: AppBundle\Controller\Place\PriceController
    prefix: /api
...
```
Code: Ajout d'un préfixe

Tous les appels d'API restent identiques mais sont maintenant préfixés. 

La configuration de *FOSRestBundle* devient maintenant :

```yaml
# src/app/config/config.yml
fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
    zone:
        - { path: ^/api }
    format_listener:
        rules:
            - { path: '^/api', priorities: ['json'], fallback_format: 'json' }
```
Code: Configuration du `zone_listener`

La partie `zone` permet d'activer le bundle que pour les routes commençants par `/api`. Ainsi, toute requête en dehors de cette *zone* sera gérée nativement par Symfony. Nous pouvons ainsi faire cohabiter notre API et une IHM complète sans soucis.

En utilisant ce système de zone, il ne faut pas oublier de reconfigurer toute la partie liée au pare-feu de Symfony et à notre système de sécurité pour prendre en compte le préfixe.

La configuration finale serait donc :
```yml
# app/config/security.yml
security:
    firewalls:
        main:
            pattern: ^/api
            stateless: true
            simple_preauth:
                authenticator: auth_token_authenticator
            provider: auth_token_user_provider
            anonymous: ~
```

La clé `pattern` prend en compte le préfixe.

```php
<?php
# src/AppBundle/Security/AuthTokenAuthenticator.php
public function createToken(Request $request, $providerKey)
{
    $targetUrl = '/api/auth-tokens';
    // Si la requête est une création de token, aucune vérification n'est effectué
    if ($request->getMethod() === "POST" && $this->httpUtils->checkRequestPath($request, $targetUrl)) {
        return;
    }
  // ...
}
// ...
}
```
Code: Prise en compte du pattern dans nos contrôleurs

L'URL dans `targetUrl` contient maintenant notre préfixe `/api`.

Il est quand même utile de souligner qu'il est préférable d'utiliser une application à part pour générer ses pages HTML et avoir une application dédiée pour son API. L’intérêt de REST est d'avoir une architecture orientée service et donc de séparer les différents composants.

[^ihm]: Interface Homme Machine, dans notre cas la page qui s'affiche dans le navigateur.
