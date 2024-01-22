# Accéder à un seul lieu

## Un peu de conception

[[question]]
| Maintenant que nous savons comment accéder à un ensemble de ressource (une collection), comment faire pour récupérer un seul lieu ?

D'un point de vue sémantique HTTP, nous savons que pour lire du contenu, il faut utiliser la méthode `GET`. Le problème maintenant est de savoir comment identifier la ressource parmi toutes celles dans la collection.

Le point de départ est, en général, le nom de la collection (**places** pour notre cas). Nous devons donc trouver un moyen permettant d'identifier de manière unique un élément de cette collection. Il a une relation entre la collection et chacune de ses ressources.

Pour le cas des lieux, nous pouvons choisir l'identifiant auto-incrémenté pour désigner de manière unique un lieu. Nous pourrons dire alors que l'identifiant `1` désigne la ressource `Tour Eiffel`.

Pour la représenter dans une URL, nous avons deux choix :

- rest-api.local/places?id=1
- rest-api.local/places/1

On pourrait être tenté par la première méthode utilisant le query string `id`. Mais la [RFC 3986](https://tools.ietf.org/html/rfc3986#section-3.4) spécifie clairement les query strings comme étant des composants qui contiennent des données *non-hiérarchiques*.
Pour notre cas, il y a une relation hiérarchique claire entre une collection et une de *ses* ressources. Donc cette méthode est à proscrire.

Notre URL pour désigner un seul lieu sera alors `rest-api.local/places/1`. Et pour généraliser, pour accéder à un lieu, on aura `rest-api.local/places/{place_id}` où `{place_id}` désigne l'identifiant de notre lieu.

## Implémentation

Mettons maintenant en œuvre un nouvel appel permettant de récupérer un lieu. Nous allons utiliser le contrôleur ***PlaceController***.

```php
# src/AppBundle/Controller/PlaceController.php
<?php
namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use AppBundle\Entity\Place;

class PlaceController extends Controller
{

    // code de getPlacesAction

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
        
        $formatted = [
           'id' => $place->getId(),
           'name' => $place->getName(),
           'address' => $place->getAddress(),
        ];
         
        return new JsonResponse($formatted);
    }
}
```

Cette action est particulièrement simple et se passe de commentaires. Ce qu'il faut retenir c'est que la méthode renvoie une seule entité et pas une liste.

En testant, nous avons comme réponse :

```json
{
  "id": 1,
  "name": "Tour Eiffel",
  "address": "5 Avenue Anatole France, 75007 Paris"
}
```

![Récupération d'un lieu avec Postman](http://zestedesavoir.com/media/galleries/3183/735983f9-9fb7-4275-9e58-e4d533ed195d.png)

Nous pouvons rendre la configuration de la route plus stricte en utilisant l'attribut `requirements` de l'annotation `Route`. Puisque les identifiants des lieux sont des entiers, la déclaration de la route pourrait être `@Route("/places/{place_id}", requirements={"place_id" = "\d+"}, name="places_one")`.
 
## Pratiquons avec les utilisateurs

*Bis repetita*, nous allons mettre en place une méthode permettant de récupérer les informations d'un seul utilisateur.

Comme pour les lieux, pour récupérer un utilisateur, il suffit de créer un nouvel appel `GET` sur l'URL `rest-api.local/users/{id}` où `{id}` désigne l'identifiant de l'utilisateur.

Pour cela, éditons le contrôleur ***UserController*** pour rajouter cette méthode.

```php
# src/AppBundle/Controller/UserController.php
<?php
namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use AppBundle\Entity\User;

class UserController extends Controller
{
    
     // code de getUsersAction

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

Nous obtenons une belle réponse JSON :
```json
{
  "id": 1,
  "firstname": "Ab",
  "lastname": "Cde",
  "email": "ab.cde@test.local"
}
```

![Récupération d'un utilisateur](http://zestedesavoir.com/media/galleries/3183/30720646-d698-4b89-95c7-45d00ffec28b.png)