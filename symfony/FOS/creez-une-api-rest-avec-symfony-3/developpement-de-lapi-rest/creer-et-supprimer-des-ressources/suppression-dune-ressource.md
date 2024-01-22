La suppression d'une ressource reste une action très facile à appréhender.
Le protocole HTTP dispose d'une méthode appelée `DELETE` qui comme son nom l'indique permet de supprimer une ressource.
Quant à l'URL de la ressource, il suffit de se poser une seule question :

[[question]]
|Que voulons-nous supprimer ?

La méthode `DELETE` s'appliquera sur la ressource à supprimer.
Si par exemple nous voulons supprimer le lieu avec comme identifiant `3`, il suffira de faire une requête sur l'URL `rest-api.local/places/3`.

Une fois n'est pas de coutume, nous allons consulter la [RFC 7312](http://tools.ietf.org/html/rfc7231#section-4.3.5)

> If a DELETE method is successfully applied, the origin server SHOULD send a 202 (Accepted) status code if the action will likely succeed but has not yet been enacted, a 204 (No Content) status code if the action has been enacted and no further information is to be supplied, or a 200 (OK) status code if the action has been enacted and the response message includes a representation describing the status.

Cette citation est bien longue mais ce qui nous intéresse ici se limite à `a 204 (No Content) status code if the action has been enacted and no further information is to be supplied`. Pour notre cas, lorsque la ressource sera supprimée, nous allons renvoyer aucune information. Le code de statut à utiliser est donc : `204`.

![Cinématique de suppression d'une ressource](http://zestedesavoir.com/media/galleries/3183/55ce3035-f686-488f-96ef-00034ff32f64.png)

# Suppression d'un lieu

Nous allons, sans plus attendre, créer une méthode pour supprimer un lieu de notre application.

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
    // ...

     /**
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT)
     * @Rest\Delete("/places/{id}")
     */
    public function removePlaceAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $place = $em->getRepository('AppBundle:Place')
                    ->find($request->get('id'));
        /* @var $place Place */

        $em->remove($place);
        $em->flush();
    }
}
```

Un petit test rapide avec Postman nous donne :

![Suppression d'un lieu avec Postman](http://zestedesavoir.com/media/galleries/3183/6da11766-ae50-49e7-ad63-80f8ede4e64c.png)

Le code de statut est aussi correct :

![Code de statut pour la suppression d'une ressource](http://zestedesavoir.com/media/galleries/3183/5618dab6-6251-4e93-b3c8-ba9af743de92.png)

[[question]]
|Que faire si nous essayons de supprimer une ressource qui n'existe pas ou plus ?

Si nous essayons de supprimer à nouveau la même ressource, nous obtenons une erreur interne. Mais, il se trouve que dans [les spécifications](http://tools.ietf.org/html/rfc7231#section-8.1.3) de la méthode `DELETE`, il est dit que cette méthode doit être idempotente.

[[information]]
|Une action idempotente est une action qui produit le même résultat et ce, peu importe le nombre de fois qu'elle est exécutée.

Pour suivre ces spécifications HTTP, nous allons modifier notre code pour gérer le cas où le lieu à supprimer n'existe pas ou plus. En plus, l'objectif d'un client qui fait un appel de suppression est de supprimer une ressource, donc si elle l'est déjà, nous pouvons considérer que tout c'est bien passé.

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
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT)
     * @Rest\Delete("/places/{id}")
     */
    public function removePlaceAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $place = $em->getRepository('AppBundle:Place')
                    ->find($request->get('id'));
        /* @var $place Place */

        if ($place) {
            $em->remove($place);
            $em->flush();
        }
    }
}
```

# Pratiquons avec les utilisateurs
Rajoutons une méthode pour supprimer un utilisateur.

```php
# src/AppBundle/Controller/UserController.php
<?php
namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\UserType;
use AppBundle\Entity\User;

class UserController extends Controller
{
    // ...

     /**
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT)
     * @Rest\Delete("/users/{id}")
     */
    public function removeUserAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $user = $em->getRepository('AppBundle:User')
                    ->find($request->get('id'));
        /* @var $user User */

        if ($user) {
            $em->remove($user);
            $em->flush();
        }
    }
}
```

