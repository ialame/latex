# Configuration du gestionnaire de vue
Avec *FOSRestBundle*, nous disposons d'un service appelé `fos_rest.view_handler` qui nous permet de gérer nos réponses.
Pour l'utiliser, il suffit d'instancier une vue *FOSRestBundle*, la configurer et laisser le gestionnaire de vue (le view handler) s'occuper du reste.
Voyez par vous-même :
```php
# src/AppBunble/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations\Get;
use FOS\RestBundle\View\ViewHandler;
use FOS\RestBundle\View\View; // Utilisation de la vue de FOSRestBundle
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    /**
     * @Get("/places")
     */
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

         // Récupération du view handler
        $viewHandler = $this->get('fos_rest.view_handler');

        // Création d'une vue FOSRestBundle
        $view = View::create($formatted);
        $view->setFormat('json');
        
        // Gestion de la réponse
        return $viewHandler->handle($view);
    }
}
```

L'intérêt d'utiliser un bundle réside aussi dans le fait de réduire les lignes de codes que nous avons à écrire (et par la même occasion, les sources de bogues). N'hésitez pas à retester notre appel afin de vérifier que la réponse est toujours la même.

FOSRestBundle introduit aussi un listener (ViewResponseListener) qui nous permet, à l'instar de Symfony via l'annotation `Template` du [SensioFrameworkExtraBundle](http://symfony.com/doc/current/bundles/SensioFrameworkExtraBundle/annotations/view.html), de renvoyer juste une instance de View et laisser le bundle appelait le gestionnaire de vue lui-même.

[[erreur]]
|Pour utiliser l'annotation `View`, il faut que le SensioFrameworkExtraBundle soit activé. Mais si vous avez utilisé l'installateur de Symfony pour créer ce projet, c'est déjà le cas.

Nous allons donc activer le listener en modifiant notre configuration :

```yaml
# app/config/config.yml
fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
```

Ensuite, il ne reste plus qu'à adapter le code (toutes les annotations de FOSRestBundle seront aliasées par `Rest`):

```php
# src/AppBunble/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use FOS\RestBundle\View\ViewHandler;
use FOS\RestBundle\View\View; // Utilisation de la vue de FOSRestBundle
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    /**
     * @Rest\View()
     * @Rest\Get("/places")
     */
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

        // Création d'une vue FOSRestBundle
        $view = View::create($formatted);
        $view->setFormat('json');

        return $view;
    }
}
```

La simplicité qu'apporte ce bundle ne s'arrête pas là. Les données assignées à la vue sont sérialisées au bon format en utilisant le sérialiseur que nous avions configuré au début.
Ce sérialiseur supporte aussi bien les tableaux que les objets. Si vous voulez approfondir le sujet, il est préférable de consulter [la documentation complète](http://symfony.com/doc/current/components/serializer.html).

Ce qu'il faut retenir dans notre cas, c'est qu'avec nos objets actuels (accesseurs en visibilité public), le sérialiseur de Symfony peut les transformer pour nous. 
Au lieu de passer un tableau formaté par nos soins, nous allons passer directement une liste d'objets au view handler. Notre code peut être réduit à :
```php
# src/AppBunble/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use FOS\RestBundle\View\ViewHandler;
use FOS\RestBundle\View\View; // Utilisation de la vue de FOSRestBundle
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    /**
     * @Rest\View()
     * @Rest\Get("/places")
     */
    public function getPlacesAction(Request $request)
    {
        $places = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->findAll();
        /* @var $places Place[] */
        
        // Création d'une vue FOSRestBundle
        $view = View::create($places);
        $view->setFormat('json');

        return $view;
    }
}
```

Et là, nous voyons vraiment l’intérêt d'utiliser les composants que nous propose le framework. L'objectif est d'être le plus concis et productif possible.

# La cerise sur le gâteau : Format automatique et réponse sans l'objet View

Pour l'instant, notre API ne supporte qu'un seul format : le JSON. Donc au lieu de le mettre dans tous les contrôleurs, *FOSRestBundle* propose un mécanisme permettant de gérer les formats et la [négociation  de contenu](https://fr.wikipedia.org/wiki/N%C3%A9gociation_de_contenu) : [le format listener](http://symfony.com/doc/1.7/bundles/FOSRestBundle/format_listener.html).

Il y aura un chapitre dédié à la gestion de plusieurs formats et la négociation  de contenu.

Pour l'instant, nous allons juste configurer le format listener de *FOSRestBundle* pour que toutes les URL renvoient du JSON.
```yaml
# src/app/config/config.yml
fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
    format_listener:
        rules:
            - { path: '^/', priorities: ['json'], fallback_format: 'json' }
```

La seule règle déclarée dit que pour toutes les URL (`path: ^/`), le format prioritaire est le JSON (`priorities: ['json']`) et si aucun format n'est demandé par le client, il faudra utiliser le JSON quand même (`fallback_format: 'json'`).

Vu que maintenant nous n'avons plus à définir le format dans les actions de nos contrôleurs, nous avons même la possibilité de renvoyer directement nos objets sans utiliser l'objet *View* de *FOSRestBundle*.
```php
# src/AppBunble/PlaceController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    /**
     * @Rest\View()
     * @Rest\Get("/places")
     */
    public function getPlacesAction(Request $request)
    {
        $places = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->findAll();
        /* @var $places Place[] */

        return $places;
    }
}
```

Un dernier test juste pour la forme :

![Récupération des lieux avec Postman](http://zestedesavoir.com/media/galleries/3183/e31b47b5-ceaa-4b08-bacf-242339e6c700.png)