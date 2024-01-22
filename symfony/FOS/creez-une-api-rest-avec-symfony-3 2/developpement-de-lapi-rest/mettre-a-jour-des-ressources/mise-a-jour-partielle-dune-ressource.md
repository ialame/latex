[[question]]
|Que faire alors si nous voulons modifier par exemple que le nom d'un lieu ?

Jusqu'à présent, nos appels API pour modifier une ressource se contente de la remplacer par une nouvelle (en gardant l'identifiant). Mais dans une API plus complète avec des ressources avec beaucoup d'attributs, nous pouvons rapidement sentir le besoin de modifier juste quelques-uns de ces attributs.

Pour cela, la seule chose que nous devons changer dans notre API c'est le verbe HTTP utilisé.

# À la rencontre de PATCH

Parmi toutes les méthodes que nous avons déjà pu utiliser, `PATCH` est la seule qui n'est pas spécifiée dans la RFC 7231 mais plutôt dans la [RFC 5789](https://tools.ietf.org/html/rfc5789).

Ce standard n'est pas encore validé - `PROPOSED STANDARD` (au moment où ces lignes sont écrites) - mais est déjà largement utilisé.

Cette méthode doit être utilisée pour décrire *un ensemble de changements* à appliquer à la ressource identifiée par son URI.

[[question]]
|Comment décrire les changements à appliquer ?

Vu que nous utilisons du JSON dans nos payload. Il existe une [RFC 6902](http://tools.ietf.org/html/rfc6902), elle aussi pas encore adoptée, qui essaye de formaliser le payload d'une requête `PATCH` utilisant du JSON.

Par exemple, dans la [section 4.3](http://tools.ietf.org/html/rfc6902#section-4.3), nous pouvons lire la description d'une opération consistant à remplacer un champ d'une ressource :
```json
{ "op": "replace", "path": "/a/b/c", "value": 42 }
```

Pour le cas de notre lieu, si nous voulions un correctif (patch) pour ne changer que l'adresse, il faudrait :
```json
{ "op": "replace", "path": "/address", "value": "Ma nouvelle adresse" }
```

Outre le fait que cette méthode n'est pas beaucoup utilisée, sa mise en œuvre par un client est complexe et son traitement coté serveur l'est autant.

Donc par *pragmatisme*, nous n'allons pas utiliser `PATCH` de cette façon.

Dans notre API, une requête `PATCH` aura comme payload le même que celui d'une requête `POST` à une grande différence près : Si un attribut n'existe pas dans le corps de la requête, nous devons conserver son ancienne valeur.

Notre requête avec comme payload :
```json
{
    "name": "Autre-Mont-Saint-Michel"
}
```
... ne devra pas renvoyer une erreur mais juste modifier le nom de notre lieu.

![Cinématique de la mise à jour partielle d'une ressource](http://zestedesavoir.com/media/galleries/3183/1f16cb61-82f3-4e08-adf8-c4496f052c9a.png)

# Mise à jour partielle d'un lieu

L'implémentation de la mise à jour partielle avec Symfony est très proche de la mise à jour complète. Il suffit de rajouter un paramètre dans la méthode `submit` (`clearMissing = false`) et le tour est joué.
Comme son nom l'indique, avec `clearMissing` à `false`, Symfony conservera tous les attributs de l'entité `Place` qui ne sont pas présents dans le payload de la requête.
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
     * @Rest\View()
     * @Rest\Patch("/places/{id}")
     */
    public function patchPlaceAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id')); // L'identifiant en tant que paramètre n'est plus nécessaire
        /* @var $place Place */

        if (empty($place)) {
            return new JsonResponse(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }

        $form = $this->createForm(PlaceType::class, $place);

         // Le paramètre false dit à Symfony de garder les valeurs dans notre 
         // entité si l'utilisateur n'en fournit pas une dans sa requête
        $form->submit($request->request->all(), false);

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            // l'entité vient de la base, donc le merge n'est pas nécessaire.
            // il est utilisé juste par soucis de clarté
            $em->merge($place);
            $em->flush();
            return $place;
        } else {
            return $form;
        }
    }
}
```

[[erreur]]
|Nous avons ici un gros copier-coller de la méthode `updatePlace`, un peu de refactoring ne sera pas de mal.

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
     * @Rest\View()
     * @Rest\Put("/places/{id}")
     */
    public function updatePlaceAction(Request $request)
    {
        return $this->updatePlace($request, true);
    }

    /**
     * @Rest\View()
     * @Rest\Patch("/places/{id}")
     */
    public function patchPlaceAction(Request $request)
    {
        return $this->updatePlace($request, false);
    }

    private function updatePlace(Request $request, $clearMissing)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id')); // L'identifiant en tant que paramètre n'est plus nécessaire
        /* @var $place Place */

        if (empty($place)) {
            return new JsonResponse(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }

        $form = $this->createForm(PlaceType::class, $place);

        // Le paramètre false dit à Symfony de garder les valeurs dans notre
        // entité si l'utilisateur n'en fournit pas une dans sa requête
        $form->submit($request->request->all(), $clearMissing);

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            $em->persist($place);
            $em->flush();
            return $place;
        } else {
            return $form;
        }
    }
}
```


En relançant notre requête, la réponse est bien celle attendue :

![Requête de mise à jour partielle avec Postman](http://zestedesavoir.com/media/galleries/3183/12a79295-03b5-4554-a3b2-952651aa5301.png)

![Réponse de la mise à jour dans Postman](http://zestedesavoir.com/media/galleries/3183/d8b54e56-0727-4582-aa87-8e6c70903d2f.png)

# Pratiquons avec les utilisateurs

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
     * @Rest\View()
     * @Rest\Patch("/users/{id}")
     */
    public function patchUserAction(Request $request)
    {
        return $this->updateUser($request, false);
    }

    private function updateUser(Request $request, $clearMissing)
    {
        $user = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->find($request->get('id')); // L'identifiant en tant que paramètre n'est plus nécessaire
        /* @var $user User */

        if (empty($user)) {
            return new JsonResponse(['message' => 'User not found'], Response::HTTP_NOT_FOUND);
        }

        $form = $this->createForm(UserType::class, $user);

        $form->submit($request->request->all(), $clearMissing);

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            $em->persist($user);
            $em->flush();
            return $user;
        } else {
            return $form;
        }
    }
}
```

Au fur et à mesure de nos développements, nous nous rendons compte que notre API est très uniforme, donc facile à utiliser pour un client. Mais aussi l'implémentation serveur l'est autant. Cette uniformité facilite grandement le développement d'une API RESTful et notre productivité est décuplée !

# Gestion des erreurs avec FOSRestBundle

Jusque-là, nous utilisons un objet `JsonResponse` lorsque la ressource recherchée n'existe pas. Cela fonctionne bien mais nous n'utilisons pas FOSRestBundle de manière appropriée.
Au lieu de renvoyer une réponse JSON, nous allons juste renvoyer une vue FOSRestBundle et laisser le view handler le formater en JSON.
En procédant ainsi, nous pourrons plus tard exploiter toutes les fonctionnalités de ce bundle comme par exemple changer le format des réponses (par exemple renvoyer du XML) sans modifier notre code.
Pour ce faire, il suffit de remplacer toutes les lignes :
```php
return new JsonResponse(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
```
Par
```php
return \FOS\RestBundle\View\View::create(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
```

Il faudra aussi faire de même avec le contrôleur ***UserController***.