Pour profiter des différents outils disponibles dans l’écosystème de *Swagger*, *NelmioApiDocBundle* propose d'exporter la configuration au format *OpenAPI*.

Pour ce faire, il faut rajouter un attribut `resource` à nos annotations `ApiDoc`. Ensuite, il suffit d'utiliser la commande `php bin/console api:swagger:dump dossier_de_destination`.
Voici un exemple de configuration qui remplit ce contrat :
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
     *    resource=true,
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
        // ...
    }

   // ...
}

```

Les métadonnées concernant la documentation peuvent être modifiées en configurant le bundle.

```yaml
# app/config/config.yml
# ...
nelmio_api_doc:
    # ...
    swagger:
        api_base_path:        /
        swagger_version:      '1.2'
        api_version:          '1.0'
        info:
            title:                Rest API
            description:          'Proposition de sortie aux utilisateurs'

```

[[attention]]
| Avec la version 2.13.0, ce bundle génère un fichier **swagger.json** en utilisant [la version 1.2 des spécifications d'*OpenAPI*](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/1.2.md) alors qu'il existe une version 2.0. Le fichier généré ne sera donc pas à jour même si dans la configuration nous mettons 2.0 comme valeur de l'attribut `swagger_version`.

En exécutant la commande :

```bash
php bin/console api:swagger:dump --pretty web/swagger
Dumping resource list to web/swagger/api-docs.json:  OK
Dump API declaration to web/swagger/auth-tokens.json:  OK
```

Les fichiers ainsi générés dans le dossier ***web/swagger*** peuvent être exploités par tous les outils compatibles avec *OpenAPI*.

Pour les tester, il suffit d'éditer le fichier ***web/swagger-ui/dist/indext.html***  et de remplacer la ligne `var url ="/swagger.json";` par `var url ="/swagger/auth-tokens.json";`.

En accédant à l'URL [http://rest-api.local/swagger-ui/dist/index.html](http://rest-api.local/swagger-ui/dist/index.html), la documentation générée s'affiche.

![Documentation OpenAPI générée par NelmioApiDocBundle](https://zestedesavoir.com/media/galleries/3183/552bdd3f-f80d-4566-84c5-1ef0046a2888.png)