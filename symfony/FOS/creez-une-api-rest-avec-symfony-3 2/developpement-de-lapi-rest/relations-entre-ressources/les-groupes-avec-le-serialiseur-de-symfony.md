
Nous venons de faire face à une erreur assez commune lorsque nous travaillons avec un sérialiseur sur des entités avec des relations.

Le problème que nous avons ici est simple à expliquer. Lorsque le sérialiseur affiche un prix, il doit sérialiser le type, la valeur mais aussi le lieu associé.

Nous aurons donc :
```json
{
    "id": 1,
    "type": "less_than_12",
    "value": 5.75,
    "place": {
        "..." : "..."
    }
}
```

Les choses se gâtent lorsque le sérialiseur va essayer de transformer le lieu contenu dans notre objet. Ce lieu contient lui-même l'objet prix qui devra être sérialisé à nouveau. Et la boucle se répète à l'infini.

![Référence circulaire](http://zestedesavoir.com/media/galleries/3183/8490e0e9-26d3-4f09-947b-7f44be413d3d.png)

Pour prévenir ce genre de problème, le sérialiseur de Symfony utilise [la notion de groupe](http://symfony.com/doc/current/components/serializer.html#attributes-groups). L'objectif des groupes est de définir les attributs qui seront sérialisés selon la vue que nous voulons afficher.

Reprenons le cas de notre prix pour mieux comprendre. Lorsque nous affichons les informations sur un prix, ce qui nous intéresse c'est :

- son identifiant ;
- son type ;
- sa valeur ;
- et son lieu associé.

Jusque-là notre problème reste entier. Mais lorsque nous allons afficher ce fameux lieu, nous devons limiter les informations affichées. Ainsi, nous pouvons décider que le lieu embarqué dans la réponse ne doit contenir que :

- son identifiant ;
- son nom ;
- et son adresse.

Le champ `prices` doit être ignoré.

Tous ces attributs peuvent représenter un groupe : `price`. À chaque fois que le sérialiseur est utilisé en spécifiant le groupe `price` alors seul ces attributs seront sérialisés. 

De la même façon, lorsque nous voudrons afficher un lieu, tous les attributs seront affichés en excluant un seul attribut : le champ `place` de l'objet `Price`.

La configuration Symfony pour obtenir un tel comportement est assez simple :
```yaml
# src/AppBundle/Resources/config/serialization.yml
AppBundle\Entity\Place:
    attributes:
        id:
            groups: ['place', 'price']
        name:
            groups: ['place', 'price']
        address:
            groups: ['place', 'price']
        prices:
            groups: ['place']


AppBundle\Entity\Price:
    attributes:
        id:
            groups: ['place', 'price']
        type:
            groups: ['place', 'price']
        value:
            groups: ['place', 'price']
        place:
            groups: ['price']
```

Ce fichier de configuration définit deux choses essentielles:

- Si nous utilisons le groupe `price` avec le sérialiseur, seuls les attributs dans ce groupe seront affichés ;
- et de la même façon, seuls les attributs dans le groupe `place` seront affichés si celui-ci est utilisé avec notre sérialiseur.

[[information]]
|Il est aussi possible de déclarer les règles de sérialisations avec des annotations sur nos entités. Pour en savoir plus, il est préférable de consulter [la documentation officielle](http://symfony.com/doc/current/cookbook/serializer.html). Les fichiers de configuration peuvent aussi être placés dans un dossier ***src/AppBundle/Resources/config/serialization/*** afin de mieux les isoler.


Pour l'utiliser dans notre contrôleur avec *FOSRestBundle*, la modification à faire est très simple. Il suffit d'utiliser l'attribut `serializerGroups` de l'annotation `View`. 
```php
# src/AppBundle/Controller/Place/PriceController.php
<?php
namespace AppBundle\Controller\Place;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\PriceType;
use AppBundle\Entity\Price;

class PriceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"price"})
     * @Rest\Get("/places/{id}/prices")
     */
    public function getPricesAction(Request $request)
    {
         // ...
    }


     /**
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"price"})
     * @Rest\Post("/places/{id}/prices")
     */
    public function postPricesAction(Request $request)
    {
        // ...
    }

    private function placeNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
    }
}
```

Pour tester cette configuration, récupérons la liste des prix du lieu `1`.

![Requête Postman pour récupérer les prix d'un lieu](http://zestedesavoir.com/media/galleries/3183/06230df9-2232-4a84-9b5b-e8110b8da881.png)

La réponse ne contient que les attributs que nous avons affectés au groupe `price`.
```json
[
  {
    "id": 1,
    "type": "less_than_12",
    "value": 5.75,
    "place": {
      "id": 1,
      "name": "Tour Eiffel",
      "address": "5 Avenue Anatole France, 75007 Paris"
    }
  }
]
```

De la même façon, nous devons modifier le contrôleur des lieux pour définir le ou les groupes à utiliser pour la sérialisation des réponses.

```php
# src/AppBundle/Controller/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\PlaceType;
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     */
    public function getPlacesAction(Request $request)
    {
        // ...
    }

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places/{id}")
     */
    public function getPlaceAction(Request $request)
    {
        // ...
    }

     /**
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"place"})
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
        // ...
    }

     /**
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT, serializerGroups={"place"})
     * @Rest\Delete("/places/{id}")
     */
    public function removePlaceAction(Request $request)
    {
        // ...
    }

     /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Put("/places/{id}")
     */
    public function updatePlaceAction(Request $request)
    {
        // ...
    }

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Patch("/places/{id}")
     */
    public function patchPlaceAction(Request $request)
    {
        // ...
    }

    // ...
}
```

Et récupérons le lieu `1` pour voir la réponse :

![Requête Postman pour récupérer un lieu](http://zestedesavoir.com/media/galleries/3183/cb94598a-6535-470e-ae42-8b818d2bd00a.png)

```json
{
  "id": 1,
  "name": "Tour Eiffel",
  "address": "5 Avenue Anatole France, 75007 Paris",
  "prices": [
    {
      "id": 1,
      "type": "less_than_12",
      "value": 5.75
    }
  ]
}
```

Grâce aux groupes, les références circulaires ne sont plus qu'un mauvais souvenir.

[[attention]]
|Les groupes du sérialiseur de Symfony ne sont supportés que depuis la version 2.0 de *FOSRestBundle*. Dans le cas où vous utilisez une version de *FOSRestBundle* inférieure à la 2.0, il faudra alors utiliser le [JMSSerializerBundle](http://jmsyst.com/bundles/JMSSerializerBundle) à la place du sérialiseur de base de Symfony.