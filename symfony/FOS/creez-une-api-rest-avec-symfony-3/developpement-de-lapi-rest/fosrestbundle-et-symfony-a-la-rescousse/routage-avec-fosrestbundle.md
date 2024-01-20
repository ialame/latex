Le système de [routage](http://symfony.com/doc/1.7/bundles/FOSRestBundle/5-automatic-route-generation_single-restful-controller.html) avec ce bundle est assez complet et facile à prendre en main. Il existe un système basé sur des conventions de nommages des méthodes et un autre basé sur des annotations.

# Routage automatique

Afin de bien voir les effets de nos modifications, nous allons d'abord afficher les routes existantes avec la commande `php bin/console debug:router`.

```text
php bin/console debug:router
 -------------------------- -------- -------- ------ -----------------------------------
  Name                       Method   Scheme   Host   Path
 -------------------------- -------- -------- ------ -----------------------------------
  _wdt                       ANY      ANY      ANY    /_wdt/{token}
  _profiler_home             ANY      ANY      ANY    /_profiler/
  ...
  _twig_error_test           ANY      ANY      ANY    /_error/{code}.{_format}
  homepage                   ANY      ANY      ANY    /
  tests_list                 GET      ANY      ANY    /tests
  places_list                GET      ANY      ANY    /places
  places_one                 GET      ANY      ANY    /places/{place_id}
  users_list                 GET      ANY      ANY    /users
  users_one                  GET      ANY      ANY    /users/{user_id}
 -------------------------- -------- -------- ------ -----------------------------------
```

Les routes qui nous intéressent ici sont au nombre de 4 :

 - GET /places
 - GET /places/{place_id}
 - GET /users
 - GET /users/{user_id}

*FOSRestBundle* nous permet d'obtenir le même résultat avec beaucoup moins de code. Nous allons donc commencer par supprimer toutes les annotations dans notre contrôleur ***PlaceController***.

```php
# src/AppBundle/Controller/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    public function getPlacesAction(Request $request)
    {
        $places = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->findAll();
        /* @var $places Place[] */
        
        $formatted = [];
        foreach ($places as $place) {
            $formatted[] = [
               'id' => $place->getId(),
               'name' => $place->getName(),
               'address' => $place->getAddress(),
            ];
        }
        
        return new JsonResponse($formatted);
    }

    public function getPlaceAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('place_id'));
        /* @var $place Place */

        if (empty($place)) {
            return new JsonResponse(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }
        
        $formatted = [
           'id' => $place->getId(),
           'name' => $place->getName(),
           'address' => $place->getAddress(),
        ];
         
        return new JsonResponse($formatted);
    }
}
```

En relançant la commande `php bin/console debug:router`, nous voyons maintenant qu'il n'existe aucune route pour les lieux. 
Nous allons donc configurer Symfony pour que *FOSRestBundle* s'occupe du routage. Les routes seront directement déclarées dans ***app/config/routing.yml***.
*FOSRestBundle* introduit un RouteLoader qui supporte les routes de type `rest`. C'est donc la seule nouveauté dans la configuration des routes dans Symfony.

```yaml
# app/config/routing.yml
app:
    resource: "@AppBundle/Controller/DefaultController.php"
    type:     annotation

places:
    type:     rest
    resource: AppBundle\Controller\PlaceController
```

[[attention]]
|Dans la clé `app`, la déclaration a été changée pour dire à Symfony de ne plus charger nos contrôleurs REST, la clé `app.resource` passe ainsi de `@AppBundle/Controller` à `@AppBundle/Controller/DefaultController.php`.

Nous pouvons constater avec la commande `php bin/console debug:router` que deux routes ont été générées pour les lieux :

- get_places /places.{_format}
- get_place  /place.{_format}

Nous reviendrons plus tard sur la présence de l'attribut `_format` dans la route.

Il suffit de tester les nouvelles routes générées pour nous rendre compte que le fonctionnement de l'application reste entièrement le même. 
 
[[question]]
| Mais comment *FOSRestBundle* génère-t-il nos routes ?

Tout le secret réside dans des conventions de nommage. Les noms que nous avons utilisé pour le contrôleur et les actions permettent de générer des routes RESTful sans efforts de notre part.

Ainsi, le nom du contrôleur sans le suffixe ***Controller*** permet d'identifier le nom de notre ressource. ***PlaceController*** permet de désigner la ressource **places**. Il faut noter aussi que si le contrôleur s'appelait ***PlacesController*** (avec un « s »), la ressource serait aussi **places**. Ce nom constitue donc le début de notre URL. 

Ensuite, pour le reste de l'URL et surtout le verbe HTTP, *FOSRestBundle* se base sur le nom de la méthode. La méthode *getPlacesAction* peut être vu en deux parties : *get* qui désigne le verbe HTTP à utiliser `GET`, et *Places* au pluriel qui correspond exactement au même nom que notre ressource.

Cette méthode dit donc à *FOSRestBundle* que nous voulons récupérer la collection de lieux de notre application qui le traduit en REST par *GET /places*.

[[information]]
|Le paramètre `Request $request` est propre à Symfony et donc est ignoré par *FOSRestBundle*.

De la même façon, la méthode *getPlaceAction* (sans un ­­­« s » à « Place ») dit à *FOSRestBundle* que nous voulons récupérer un seul lieu.

Mais la différence ici réside dans le fait que nous avons besoin d'un paramètre pour identifier le lieu que nous voulons récupérer. Pour que la route générée soit correcte, il est obligatoire de rajouter un paramètre identifiant la ressource.

La signature de la méthode devient alors :
```php
# src/AppBunble/PlaceController.php
<?php
public function getPlaceAction($id, Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($id); // L'identifiant est utilisé directement
        /* @var $place Place */
        // ...
         
        return new JsonResponse($formatted);
    }
```

Les nouvelles routes deviennent :

- *get_places GET /places.{_format}* qui permet de récupérer tous les lieux de l'application (*get_places* est le nom de la route générée) ;
- *get_place GET /places/{id}.{_format}* qui permet de récupérer un seul lieu de l'application (*get_place* est le nom de la route générée).

Nous retrouvons deux routes totalement opérationnelles. En suivant cet ensemble de normes, les routes sont alors générées automatiquement avec les bonnes URL, les bons paramètres et les bons verbes HTTP.

# Routage manuel

Bien que très pratique, le routage automatique peut rapidement montrer ses limites. 
D'abord, il nous impose des règles de nommage pour nos méthodes. Si nous voulons nommer autrement nos actions dans le contrôleur, nous faisons face à une limitation vu que les URL et les verbes HTTP peuvent être impactés. 
Ensuite, pour avoir des routes correctes, il faudra connaitre l'ensemble des règles de nommage qu'utilise *FOSTRestBundle*, ce qui est loin d'être évident.

Heureusement, nous avons à disposition une méthode manuelle permettant de définir nos routes facilement.

L'avantage du routage manuel réside dans le fait qu'il se rapproche au plus du système de routage natif de Symfony avec *SensioFrameworkExtraBundle* et permet donc de moins se perdre en tant que débutant. En plus, les annotations permettant de déclarer les routes sont plus lisibles.

*FOSRestBundle* propose donc plusieurs annotations de routage :

- FOS\\RestBundle\\Controller\\Annotations\\Get;
- FOS\\RestBundle\\Controller\\Annotations\\Head;
- FOS\\RestBundle\\Controller\\Annotations\\Put;
- FOS\\RestBundle\\Controller\\Annotations\\Delete;
- FOS\\RestBundle\\Controller\\Annotations\\Post;
- FOS\\RestBundle\\Controller\\Annotations\\Patch;

Chacune de ces annotations désigne une méthode HTTP et prend exactement les mêmes paramètres que l'annotation [Route](http://symfony.com/doc/current/bundles/SensioFrameworkExtraBundle/annotations/routing.html) que nous avions déjà utilisée.

Pour l'appliquer dans le cas du contrôleur ***PlaceController***, nous aurons :

```php
# src/AppBunble/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations\Get; // N'oublons pas d'inclure Get
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    /**
     * @Get("/places")
     */
    public function getPlacesAction(Request $request)
    {
        // ...
    }

    /**
     * @Get("/places/{id}")
     */
    public function getPlaceAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id')); // L'identifiant en tant que paramétre n'est plus nécessaire
        // ...
    }
}
```

Les nouvelles routes restent inchangées :

- get_places GET /places.{_format}
- get_place GET /places/{id}.{_format}

[[erreur]]
|Si une de ces annotations est utilisée sur une action du contrôleur, le système de routage automatique abordé précédemment n'est plus utilisable sur cette même action.