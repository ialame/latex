Afin de gérer la mise à jour des ressources, nous devons différencier la mise à jour complète (le client de l'API veut changer toute la ressource) de la mise à jour partielle (le client de l'API veut changer juste quelques attributs de la ressource).

Déroulons notre schéma classique pour trouver les mécanismes qu'offre HTTP pour gérer la mise à jour complète d'une ressource.

# Quelle est la ressource cible ?

Lorsque nous voulons modifier une ressource, la question ne se pose pas. La cible de notre requête est la ressource à mettre à jour.
Donc pour mettre à jour un lieu, nous devrons faire une requête sur l'URL de celle-ci (par exemple rest-api.local/places/1).

# Quel verbe HTTP ?

La différenciation entre la mise à jour complète ou partielle d'une ressource se fait avec le choix du verbe HTTP utilisé. Donc le verbe est ici d'**une importance capitale**.

Notre fameuse [RFC 7231](http://tools.ietf.org/html/rfc7231#section-4.3.3) décrit la méthode `PUT` comme :

> The PUT method requests that the state of the target resource be created or replaced with the state defined by the representation enclosed in the request message payload. 

La méthode `PUT` permet de créer ou de remplacer une ressource.

Le cas d'utilisation de `PUT` pour créer une ressource est très rare et nous ne l'aborderons pas. Il faut juste retenir que pour que cet verbe soit utilisé pour créer une ressource, il faudrait laisser au client de l'API le choix des URL de nos ressources.
Nous l'utiliserons donc juste afin de remplacer le contenu d'une ressource par le payload de la requête, bref pour la mettre à jour en entier.

# Le corps de notre requête 

Le corps de la requête sera donc la nouvelle valeur que nous voulons affecter à notre ressource (toujours au format JSON comme pour la création).

# Quel code de statut HTTP ?

Dans la description même de la requête `PUT`, le code de statut à utiliser est explicité: `200`.

> A successful PUT of a given representation would suggest that a subsequent GET on that same target resource will result in an equivalent representation being sent in a 200 (OK) response.

![Cinématique de la mise à jour complète](http://zestedesavoir.com/media/galleries/3183/1773ddb0-eaed-4427-a3ed-c01c51cb56a9.png)

[[information]]
|Juste pour rappel, comme pour la récupération d'une ressource, si le client essaye de mettre à jour une ressource inexistante, nous aurons un `404`.

# Mise à jour d'un lieu

Pour une mise à jour complète, un utilisateur devra faire une requête `PUT` sur l'URL `rest-api.local/places/{id}` où `{id}` représente l'identifiant du lieu avec comme payload, le même qu'à la création :
```json
{
    "name": "ici un nom",
    "address": "ici une adresse"
}
```
[[erreur]]
|Le corps de la requête ne contient pas l'identifiant de la ressource vu qu'elle sera disponible dans l'URL.

Le routage dans notre contrôleur se rapproche beaucoup de celle pour récupérer un lieu :
```php
# src/AppBundle/Controller/PlaceController.php
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
     * @Rest\Put("/places/{id}")
     */
    public function putPlaceAction(Request $request)
    {
    
    }
}
```

Les règles de validation des informations sont exactement les mêmes qu'à la création d'un lieu. Nous allons donc exploiter le même formulaire Symfony.
La seule différence ici réside dans le fait que nous devons d'abord récupérer une instance du lieu dans la base de données avant d'appliquer les mises à jour.

```php
# src/AppController/PlaceController.php
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
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id')); // L'identifiant en tant que paramètre n'est plus nécessaire
        /* @var $place Place */

        if (empty($place)) {
            return new JsonResponse(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
        }

        $form = $this->createForm(PlaceType::class, $place);

        $form->submit($request->request->all());

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

Pour tester cette méthode, nous allons utiliser Postman.

![Requête de mise à jour complète avec Postman](http://zestedesavoir.com/media/galleries/3183/0fe63dd0-4c38-4458-aacc-520d61f007c9.png)

La réponse est :
```json
  {
    "id": 2,
    "name": "Mont-Saint-Michel",
    "address": "Autre adresse Le Mont-Saint-Michel"
  }
```

![Réponse à la requête de mise à jour dans Postman](http://zestedesavoir.com/media/galleries/3183/f643d594-d20e-4c71-8a1c-b4cd4bc071ae.png)

# Pratiquons avec les utilisateurs

La mise à jour complète d'un utilisateur suit exactement le même modèle que celle d'un lieu. Les contraintes de validation sont identiques à celles de la création d'un utilisateur.

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
     * @Rest\Put("/users/{id}")
     */
    public function updateUserAction(Request $request)
    {
        $user = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->find($request->get('id')); // L'identifiant en tant que paramètre n'est plus nécessaire
        /* @var $user User */

        if (empty($user)) {
            return new JsonResponse(['message' => 'User not found'], Response::HTTP_NOT_FOUND);
        }

        $form = $this->createForm(UserType::class, $user);

        $form->submit($request->request->all());

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            // l'entité vient de la base, donc le merge n'est pas nécessaire.
            // il est utilisé juste par soucis de clarté
            $em->merge($user);
            $em->flush();
            return $user;
        } else {
            return $form;
        }
    }
}
```

[[question]]
|Que se passe-t-il si nous faisons une requête en omettant le champ `address` ?

Modifions notre requête en supprimant le champ `address` :
```json
{
    "name": "Autre-Mont-Saint-Michel"
}
```

La réponse est une belle erreur 400: 
```json
{
  "code": 400,
  "message": "Validation Failed",
  "errors": {
    "children": {
      "name": [],
      "address": {
        "errors": [
          "This value should not be blank."
        ]
      }
    }
  }
}
```

Cela nous permet de bien valider les données envoyées par le client mais avec cette méthode, il est dans l'obligation de connaitre tous les champs afin d'effectuer sa mise à jour.
