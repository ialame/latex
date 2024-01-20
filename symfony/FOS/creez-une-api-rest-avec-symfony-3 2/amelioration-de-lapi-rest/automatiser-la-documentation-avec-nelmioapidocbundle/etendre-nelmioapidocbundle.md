# Pourquoi étendre le bundle ?

Pour corriger les petits manquements de *NelmioApiDocBundle*, nous allons étendre le code de celui-ci. L'objectif n'est pas d'apprendre le code source de ce bundle mais plutôt de maximiser son efficacité en l'adaptant à nos besoins.

[[information]]
|Il est possible d'obtenir de la documentation en redéfinissant manuellement toutes ces informations manquantes. Mais l'intérêt réel de ce bundle réside dans le fait d'utiliser les composants déjà existants pour générer la documentation automatiquement. N'hésitez donc pas à consulter [la documentation officielle](https://github.com/nelmio/NelmioApiDocBundle) de NelmioApiDocBundle pour plus d'informations.

# Correction du format de sortie des réponses en erreur

Il n'y a pas de documentation sur comment étendre *NelmioApiDocBundle*. Mais vu que ce bundle est open source, il suffit de relire avec attention son code pour comprendre son fonctionnement.

Il en ressort que pour traiter les informations disponibles dans les attributs `input` et `output` de l'annotation `ApiDoc`, le bundle utilise des parseurs.

Et [la documentation officielle](https://github.com/nelmio/NelmioApiDocBundle/blob/master/Resources/doc/configuration-in-depth.rst) nous explique comment en créer et comment l'utiliser.

Nous allons donc créer un parseur capable de générer les erreurs de validation au même format que *FOSRestBundle*.

Ce code est grandement inspiré du parseur déjà existant ([FormErrorsParser](https://github.com/nelmio/NelmioApiDocBundle/blob/2.12.0/Parser/FormErrorsParser.php)).

```php
<?php
# src/Component/ApiDoc/Parser/FOSRestFormErrorsParser.php

namespace Component\ApiDoc\Parser;

use Nelmio\ApiDocBundle\DataTypes;
use Nelmio\ApiDocBundle\Parser\ParserInterface;
use Nelmio\ApiDocBundle\Parser\PostParserInterface;

class FOSRestFormErrorsParser implements ParserInterface, PostParserInterface
{
    
    public function supports(array $item)
    {
        return isset($item['fos_rest_form_errors']) && $item['fos_rest_form_errors'] === true;
    }

    public function parse(array $item)
    {
        return array();
    }


    public function postParse(array $item, array $parameters)
    {
        $params = [];

        // Il faut d'abord désactiver tous les anciens paramètres créer par d'autres parseurs avant de reformater
        foreach ($parameters as $key => $parameter) {
            $params[$key] = null;
        }

        $params['code'] = [
            'dataType' => 'integer',
            'actualType' => DataTypes::INTEGER,
            'subType' => null,
            'required' => false,
            'description' => 'The status code',
            'readonly' => true
        ];

        $params['message'] = [
            'dataType' => 'string',
            'actualType' => DataTypes::STRING,
            'subType' => null,
            'required' => true,
            'description' => 'The error message',
            'default' => 'Validation failed.',
        ];

        $params['errors'] = [
            'dataType' => 'errors',
            'actualType' => DataTypes::MODEL,
            'subType' => sprintf('%s.FormErrors', $item['class']),
            'required' => true,
            'description' => 'List of errors',
            'readonly' => true,
            'children' => [
                'children' => [
                    'dataType' => 'List of form fields',
                    'actualType' => DataTypes::MODEL,
                    'subType' => sprintf('%s.Children', $item['class']),
                    'required' => true,
                    'description' => 'Errors',
                    'readonly' => true,
                    'children' => []
                ]
            ]
        ];

        foreach ($parameters as $name => $parameter) {
            $params['errors']['children']['children']['children'][$name] = $this->doPostParse($parameter, $name, [$name], $item['class']);
        }
       
        return $params;
    }

    protected function doPostParse($parameter, $name, array $propertyPath, $type)
    {
        $data = [
            'dataType' => 'Form field',
            'actualType' => DataTypes::MODEL,
            'subType' => sprintf('%s.FieldErrors[%s]', $type, implode('.', $propertyPath)),
            'required' => true,
            'description' => 'Field name',
            'readonly' => true,
            'children' => [
                'errors'=> [
                    'dataType' => 'errors',
                    'actualType' => DataTypes::COLLECTION,
                    'subType' => 'string',
                    'required' => false,
                    'description' => 'List of field error messages',
                    'readonly' => true
                ]
            ]
        ];

        if ($parameter['actualType'] == DataTypes::COLLECTION) {
            $data['children']['children'] = [
                'dataType' => 'List of embedded forms fields',
                'actualType' => DataTypes::COLLECTION,
                'subType' => sprintf('%s.FormErrors', $parameter['subType']),
                'required' => true,
                'description' => 'Validation error messages',
                'readonly' => true,
                'children' =>  [
                    'children' => [
                        'dataType' => 'Embedded form field',
                        'actualType' => DataTypes::MODEL,
                        'subType' => sprintf('%s.Children', $parameter['subType']),
                        'required' => true,
                        'description' => 'List of errors',
                        'readonly' => true,
                        'children' => []
                    ]
                ]
            ];

            foreach ($parameter['children'] as $cName => $cParameter) {
                $cPropertyPath = array_merge($propertyPath, [$cName]);

                $data['children']['children']['children']['children']['children'][$cName] =   $this->doPostParse($cParameter, $cName, $cPropertyPath, $parameter['subType']);
            }

        }

        return $data;
    }
}
```

Ce parseur doit toujours être utilisé avec `FormTypeParser` qui apporte l'ensemble des informations issues du formulaire Symfony. Pour l'activer, il faut utiliser l'attribut : `fos_rest_form_errors` (voir la méthode `supports`).

Pour le déclarer en tant parseur prêt à l'emploi, nous devons créer un service avec le tag `nelmio_api_doc.extractor.parser`.

```yaml
# app/config/services.yml
services:
    # ...

    app_bundle.api_doc.fos_rest_form_errors_parser:
        class: Component\ApiDoc\Parser\FOSRestFormErrorsParser
        tags:
            - { name: nelmio_api_doc.extractor.parser, priority: 1 }

```

[[information]]
| Tous les parseurs natifs du bundle sont déclarés avec une priorité de 0. En utilisant une priorité de 1, nous nous assurons que notre parseur est toujours appelé en dernier.

Pour utiliser notre parseur, nous allons ajuster l'annotation sur le contrôleur des lieux en utilisant l'attribut `fos_rest_form_errors`.

```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
// ...

class PlaceController extends Controller
{

    // ...

     /**
     * @ApiDoc(
     *    description="Crée un lieu dans l'application",
     *    input={"class"=PlaceType::class, "name"=""},
     *    statusCodes = {
     *        201 = "Création avec succès",
     *        400 = "Formulaire invalide"
     *    },
     *    responseMap={
     *         201 = {"class"=Place::class, "groups"={"place"}},
     *         400 = { "class"=PlaceType::class, "fos_rest_form_errors"=true, "name" = ""}
     *    }
     * )
     *
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"place"})
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
        // ...
    }
    // ...
}
```

La réponse pour un formulaire invalide est maintenant correctement formatée.

![Documentation de la réponse pour un formulaire invalide](https://zestedesavoir.com/media/galleries/3183/f0d112f3-cae1-4143-8bc0-5b2d2dc64a8d.png)