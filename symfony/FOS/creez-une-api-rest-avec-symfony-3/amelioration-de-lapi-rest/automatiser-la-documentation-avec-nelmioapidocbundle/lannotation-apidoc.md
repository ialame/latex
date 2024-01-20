# Configuration

Pour générer de la documentation, le bunble *NelmioApiDocBundle* se base sur une fonctionnalité principale : l'annotation `ApiDoc`.

À son installation, ce bundle met à notre disposition cette annotation qui va nous permettre de **rédiger** notre documentation.

[[information]]
|Il faut garder en tête que la documentation avec *NelmioApiDocBundle* est grandement liée au code.

Sans plus attendre, nous allons l'utiliser pour documenter l'appel qui liste les lieux de notre application.
```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
// ...

class PlaceController extends Controller
{

    /**
     * @ApiDoc(
     *    description="Récupère la liste des lieux de l'application"
     * )
     *
     *
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     * @QueryParam(name="offset", requirements="\d+", default="", description="Index de début de la pagination")
     * @QueryParam(name="limit", requirements="\d+", default="", description="Nombre d'éléments à afficher")
     * @QueryParam(name="sort", requirements="(asc|desc)", nullable=true, description="Ordre de tri (basé sur le nom)")
     */
    public function getPlacesAction(Request $request, ParamFetcher $paramFetcher)
    {
        // ...

        return $places;
    }
// ...
}
```

Avec juste cette annotation, il est possible de consulter la documentation de notre API. Mais avant d'y accéder, nous devons avoir une URL dédiée. Et pour ce faire, le bundle propose un fichier de routage qui permet de configurer cette URL.

```yaml
# app/config/routing.yml

# ...
nelmio-api-doc:
    resource: "@NelmioApiDocBundle/Resources/config/routing.yml"
    prefix:   /documentation

```

Nous allons aussi rajouter une règle dans le pare-feu de Symfony afin d'autoriser l'accès à la documentation sans authentification.
```yaml
# app/config/secrity.yml
security:

# ...
    firewalls:
        # disables authentication for assets and the profiler, adapt it according to your needs
        dev:
            pattern: ^/(_(profiler|wdt)|css|images|js)/
            security: false

        doc:
            pattern: ^/documentation
            security: false
# ...
```
Notre documentation est maintenant accessible depuis l'URL [http://rest-api.local/documentation](http://rest-api.local/documentation).

En y accédant depuis un navigateur, nous obtenons une page générée automatiquement :

![Documentation générée par NelmioApiDocBundle](https://zestedesavoir.com/media/galleries/3183/64675bd3-1609-46a2-adbe-7e0bea07f8c9.png)

Pour avoir une vue complète (comme sur l'image), il faut cliquer sur la méthode `GET /places` pour dérouler les détails concernant les filtres. La mise en page de la documentation est grandement inspiré de *Swagger UI*.

# Intégration avec FOSRestBundle

Le premier point qui devrait vous interpeller est la présence des filtres de *FOSRestBundle* dans la documentation.
*NelmioApiDocBundle* a été conçu pour interagir avec la plupart des bundles utilisés dans le cadre d'une API. 
Ainsi, les annotations de *FOSRestBundle* sont utilisées pour compléter la documentation.

Bien sûr, si nous n'utilisons pas *FOSRestBundle*, nous pouvons rajouter manuellement des filtres en utilisant l'attribut `filters` de l'annotation `ApiDoc`.

De la même façon, le verbe HTTP utilisé est `GET` avec une URL `/places`. Là aussi, les routes générées par Symfony sont utilisées par *NelmioApiDocBundle*.

# Définir le type des réponses de l'API

Notre documentation n'est pas encore complète. Le type des réponses renvoyées par notre API n'est pas encore documenté.

Pour ce faire, il existe un attribut nommé `output` qui prend comme paramètre le nom d'une classe ou encore une collection. Cet attribut supporte aussi les groupes de sérialisation que nous avons déjà définis.

Pour le cas des lieux, nous devons renvoyer une collection de lieux. La documentation s'écrit donc :

```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;
// ...
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
// ...

class PlaceController extends Controller
{

    /**
     * @ApiDoc(
     *    description="Récupère la liste des lieux de l'application",
     *    output= { "class"=Place::class, "collection"=true, "groups"={"place"} }
     * )
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     * @QueryParam(name="offset", requirements="\d+", default="", description="Index de début de la pagination")
     * @QueryParam(name="limit", requirements="\d+", default="", description="Nombre d'éléments à afficher")
     * @QueryParam(name="sort", requirements="(asc|desc)", nullable=true, description="Ordre de tri (basé sur le nom)")
     */
    public function getPlacesAction(Request $request, ParamFetcher $paramFetcher)
    {
    // ...
    }
// ...
}
```
La documentation devient :

![Type de réponse pour la liste des lieux](https://zestedesavoir.com/media/galleries/3183/ff0af4f2-ce3a-434b-a399-a2f96e63dea5.png)

La documentation est complétée et les attributs ont exactement les bon types définis dans les annotations *Doctrine*. Pour obtenir de telles informations, *NelmioApiDocBundle* utilise le sérialiseur de *JMSSerializerBundle*.

[[erreur]]
|Par contre, si nous étions restés sur le sérialiseur natif de Symfony qui n'est pas encore supporté, nous n'aurions pas pu obtenir ces informations.

Les descriptions de tous les attributs sont vides. Pour les renseigner, il suffit de rajouter dans les entités une description dans le bloc de *PHPDoc*.

Pour l'entité `Place`, nous pouvons rajouter :
```php
<?php
    /**
     * Identifiant unique du lieu
     *
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue
     */
    protected $id;
```

La documentation générée devient alors :

![Description de l'identifiant du lieu dans la documentation](https://zestedesavoir.com/media/galleries/3183/6bf39eb0-4736-42af-b051-45bebdc93f2e.png)

# Définir le type des payloads des requêtes

De la même façon, pour définir la structure des payloads des requêtes, nous pouvons utiliser un attribut nommé `input` qui peut prendre en paramètre, entre autres, une classe qui implémente l'interface PHP `JsonSerializable` mais aussi un formulaire Symfony. Et cela tombe bien puisse que tous nos payloads se basent sur ces formulaires.

Pour tester le bon fonctionnement de cet attribut, nous allons rajouter de la documentation pour la méthode de création d'un lieu.

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
     *    input={"class"=PlaceType::class, "name"=""}
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

![Documentation générée par NelmioApiDocBundle](https://zestedesavoir.com/media/galleries/3183/89489495-7416-44ad-9e9f-4e0c6cdc9641.png)

Pour rajouter des descriptions pour les différents attributs des formulaires, nous pouvons utiliser une option nommée `description` rajoutée aux formulaires Symfony par *NelmioApiDocBundle*.

```php
<?php
# src/AppBundle/Form/Type/PlaceType.php

namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CollectionType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PlaceType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('name', TextType::class, [
            'description' => "Nom du lieu"
        ]);
        $builder->add('address', TextType::class, [
            'description' => "Adresse complète du lieu"
        ]);
        $builder->add('prices', CollectionType::class, [
            'entry_type' => PriceType::class,
            'allow_add' => true,
            'error_bubbling' => false,
            'description' => "Liste des prix pratiqués"
        ]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\Place',
            'csrf_protection' => false
        ]);
    }
}
```

![Documentation complétée avec les descriptions des attributs](https://zestedesavoir.com/media/galleries/3183/27e39081-c95f-4fb4-a032-2364f95c7654.png)

# Gérer plusieurs codes de statut

En définissant l'attribut `output`, le code de statut associé par défaut est 200. Mais pour la création d'un lieu, nous devons avoir un code 201. Et de la même façon si le formulaire est invalide, nous voulons renvoyer une erreur 400 avec les messages de validation. Pour obtenir un tel résultat, *NelmioApiDocBundle* met à notre disposition un attribut  `responseMap`.

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
     *         400 = { "class"=PlaceType::class, "form_errors"=true, "name" = ""}
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

Le paramètre `form_errors` permet de spécifier le type de retour que nous voulons à savoir les erreurs de validation.

![Documentation de la création avec succès](https://zestedesavoir.com/media/galleries/3183/ea3f64a8-f885-4dc4-9044-9257e93132bd.png)

![Documentation de la création avec des erreurs de validation](https://zestedesavoir.com/media/galleries/3183/84bd04f2-e12f-4257-a4b9-abfb89cd6eb3.png)

Ici, nous avons bien deux réponses selon le code de statut mais pour la réponse lors d'un requête invalide, le format n'est pas correct (pas d'attribut `children`, l'attribut `status_code` s'appelle `code`, etc.).

