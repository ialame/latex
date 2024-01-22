# Quel code de statut utilisé ?

Que se passe-t-il si nous essayons de récupérer un lieu inexistant ?

Vous remarquerez qu'avec le code actuel si le lieu recherché n'existe pas (par exemple [rest-api.local/places/42](http://rest-api.local/places/42)), nous avons une belle erreur nous signifiant que la méthode getId ne peut être appelée sur l'objet `null` (`Fatal error: Call to a member function getId() on null`) et le code de statut de la réponse est une erreur `500`.

![Récupération d'un lieu inexistant](http://zestedesavoir.com/media/galleries/3183/f9dfd919-5f11-4104-a11c-a2493afa58f0.png)

Ce comportement ne respecte pas la sémantique HTTP. En effet dans n'importe quel site, si vous essayez d'accéder à une page inexistante, vous recevez la fameuse erreur `404 Not Found` qui signifie que la ressource n'existe pas. Pour que notre API soit le plus RESTful possible, nous devons implémenter un comportement similaire.

Nous ne devons avoir une erreur `500` que dans le cas d'une erreur interne du serveur. Par exemple, s'il est impossible de se connecter à la base de données, il est légitime de renvoyer une erreur `500`.

De la même façon, lorsque la ressource est trouvée, nous devons renvoyer un code `200` pour signifier que tout s'est bien passé. Par chance, ce code est le code par défaut lorsqu'on utilise l'objet ***JsonResponse*** de Symfony. Nous avons donc déjà ce comportement en place.

![Cinématique de récupération des lieux avec le code de statut](http://zestedesavoir.com/media/galleries/3183/483910ed-0da7-4ad9-a00d-3e2f9360855e.png)

# Gérer une erreur 404

Pour notre cas, il est facile de gérer ce type d'erreurs. Nous devons juste vérifier que la réponse du repository n'est pas nulle. Au cas contraire, il faudra renvoyer une erreur `404` avec éventuellement un message détaillant le problème.

Pour un lieu, nous aurons donc :
```php
# src/AppBundle/Controller/PlaceController.php
<?php
namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use AppBundle\Entity\Place;

class PlaceController extends Controller
{
    // ...

    /**
     * @Route("/places/{place_id}", name="places_one")
     * @Method({"GET"})
     */
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


Maintenant, une requête `GET` sur l'URL [rest-api.local/places/42](rest-api.local/places/42) nous renvoie une erreur `404` avec un message bien formaté en JSON. 
La constante `Response::HTTP_NOT_FOUND` vaut `404` et est une constante propre à Symfony.

La réponse contient un message en JSON :
```json
{
  "message": "Place not found"
}
```

![Récupération d'un lieu inexistant avec Postman](http://zestedesavoir.com/media/galleries/3183/dc186ce3-69c6-4ba6-bafc-592745420efe.png)

Pour un utilisateur, les modifications à effectuer restent identiques :
```php
# src/AppBundle/Controller/UserController.php
<?php
namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use AppBundle\Entity\User;

class UserController extends Controller
{
    // ...

    /**
     * @Route("/users/{id}", name="users_one")
     * @Method({"GET"})
     */
    public function getUserAction(Request $request)
    {
        $user = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->find($request->get('id'));
        /* @var $user User */

        if (empty($user)) {
            return new JsonResponse(['message' => 'User not found'], Response::HTTP_NOT_FOUND);
        }
        
        $formatted = [
           'id' => $user->getId(),
           'firstname' => $user->getFirstname(),
           'lastname' => $user->getLastname(),
           'email' => $user->getEmail(),
        ];
        
        return new JsonResponse($formatted);
    }
}
```

Avec ces modifications, nous avons maintenant une gestion des erreurs propres et l'API respecte au mieux la sémantique HTTP.