Comme pour *OpenAPI (Swagger RESTFul API)*, *NelmioApiDocBundle* propose un bac à sable permettant de tester la documentation. Avant d'utiliser ce bac à sable, nous allons rajouter quelques informations de configuration.

# Configuration du bac à sable 

[La documentation officielle](https://github.com/nelmio/NelmioApiDocBundle/blob/master/Resources/doc/sandbox.rst) sur le bac à sable est concise et simple. Les paramètres disponibles sont d'ailleurs assez proches de ceux d'*OpenAPI*.

Voyez donc par vous-même.
```yaml
# app/config/config.yml

nelmio_api_doc:
    sandbox:
        enabled:  true  # Juste pour la lisibilité car true est déjà la valeur par défaut
        endpoint: http://rest-api.local

        authentication:
            name: X-Auth-Token
            delivery: header

        accept_type: application/json   # valeur par défaut de l'entête Accept

        body_format:
            formats: [ json, xml ]                                 
            default_format: json                

        request_format:
            formats:                           
                json: application/json         
                xml: application/xml           

            method: accept_header     
            default_format: json    
```

Cette configuration est assez explicite et se passe donc de commentaires. En accédant à la documentation avec l'URL [http://rest-api.local/documentation](http://rest-api.local/documentation), nous obtenons : 

![Bac à sable de NelmioApiDocBundle](https://zestedesavoir.com/media/galleries/3183/2954e63c-90a9-4952-99e5-3ceee8a14849.png)

Le bac à sable est disponible en cliquant sur l'onglet ||Sandbox||.

# Documentation pour la création de token

Avant de tester ce bac à sable, nous allons rajouter de la documentation pour la création de token d'authentification. Cela facilitera grandement nos tests.

```php
<?php
# src/AppBundle/Controller/AuthTokenController.php

namespace AppBundle\Controller;

// ...
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
// ...

class AuthTokenController extends Controller
{
    /**
     * @ApiDoc(
     *    description="Crée un token d'authentification",
     *    input={ "class" = CredentialsType::class, "name"=""},
     *    statusCodes = {
     *        201 = "Création avec succès",
     *        400 = "Formulaire invalide"
     *    },
     *    responseMap={
     *         201 = {"class"=AuthToken::class, "groups"={"auth-token"}},
     *         400 = { "class"=CredentialsType::class, "fos_rest_form_errors"=true, "name" = ""}
     *    }
     * )
     *
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"auth-token"})
     * @Rest\Post("/auth-tokens")
     */
    public function postAuthTokensAction(Request $request)
    {
        //...
    }
//...
}
```
Nous pouvons maintenant créer un token depuis le bac à sable.

# Tester le bac à sable

Vu que toutes nos méthodes nécessites une authentification, il faut d'abord crée un token d'authentification. Ce token doit être renseigné dans le formulaire `api_key`.

![Token renseigné dans le formulaire](https://zestedesavoir.com/media/galleries/3183/3c407fa1-e5f4-4cc9-b6b2-ada15dc0a6d7.png)

Avec la configuration que nous avons mise en place, ce token sera envoyé automatiquement pour toutes nos requêtes.

Maintenant pour récupérer les lieux de l'application, il suffit de cliquer sur le bouton ||Try it!||.

![Récupération des lieux grâce au bac à sable](https://zestedesavoir.com//media/galleries/3183/a2158bd4-277e-4212-82f5-5db9507dd713.png)