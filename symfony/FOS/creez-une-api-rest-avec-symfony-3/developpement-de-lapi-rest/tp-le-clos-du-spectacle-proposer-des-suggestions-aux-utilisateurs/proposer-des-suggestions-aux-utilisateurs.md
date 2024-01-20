# Calcul du niveau de correspondance

[[information]]
|La technique utilisée pour trouver les suggestions n'est pas optimale. L'objectif ici est juste de présenter une méthode fonctionnelle et avoir une API complète.

L'algorithme pour calculer le niveau de correspondance va être implémenté dans l'entité `User`.
À partir des thèmes d'un lieu, nous allons créer une méthode permettant de déterminer le niveau de correspondance (défini plus haut).

```php
<?php
# src/AppBundle/Entity/User.php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Doctrine\Common\Collections\ArrayCollection;

/**
* @ORM\Entity()
* @ORM\Table(name="users",
*      uniqueConstraints={@ORM\UniqueConstraint(name="users_email_unique",columns={"email"})}
* )
*/
class User
{
    const MATCH_VALUE_THRESHOLD = 25;

    // ...

    public function preferencesMatch($themes)
    {
        $matchValue = 0;
        foreach ($this->preferences as $preference) {
            foreach ($themes as $theme) {
                if ($preference->match($theme)) {
                    $matchValue += $preference->getValue() * $theme->getValue();
                }
            }
        }

        return $matchValue >= self::MATCH_VALUE_THRESHOLD;
    }
}

```

La méthode `match` de l'objet `Preference` permet juste de vérifier si le nom du thème est le même que celui de la préférence de l'utilisateur.

```php
<?php
# src/AppBundle/Entity/Preference.php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="preferences",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="preferences_name_user_unique", columns={"name", "user_id"})}
 * )
 */
class Preference
{
    // ...

    public function match(Theme $theme)
    {
        return $this->name === $theme->getName();
    }
}
```

# Appel API pour récupérer les suggestions

Pour récupérer les suggestions, il nous suffit maintenant de créer un appel dans le contrôleur ***UserController***.

```php
<?php
# src/AppBundle/Controller/UserController.php

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
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/users/{id}/suggestions")
     */
    public function getUserSuggestionsAction(Request $request)
    {
        $user = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->find($request->get('id'));
        /* @var $user User */

        if (empty($user)) {
            return $this->userNotFound();
        }

        $suggestions = [];

        $places = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->findAll();

        foreach ($places as $place) {
            if ($user->preferencesMatch($place->getThemes())) {
                $suggestions[] = $place;
            }
        }
        
        return $suggestions;
    }

    // ...

    private function userNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'User not found'], Response::HTTP_NOT_FOUND);
    }
}

```


[[attention]]
|Un fait important à relever ici est que la méthode, bien qu'étant dans le contrôleur des utilisateurs, renvoie des lieux. Le groupe de sérialisation utilisé est donc **place**.


Pour tester, nous avons un utilisateur défini comme suit :
```json
{
  "id": 1,
  "firstname": "My",
  "lastname": "Bis",
  "email": "my.last@test.local",
  "preferences": [
    {
      "id": 1,
      "name": "history",
      "value": 4
    },
    {
      "id": 2,
      "name": "art",
      "value": 4
    },
    {
      "id": 6,
      "name": "sport",
      "value": 3
    }
  ]
}
```

Et la liste de lieux dans l'application est la suivante :
```json
[
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
    ],
    "themes": [
      {
        "id": 1,
        "name": "architecture",
        "value": 7
      },
      {
        "id": 2,
        "name": "history",
        "value": 6
      }
    ]
  },
  {
    "id": 2,
    "name": "Mont-Saint-Michel",
    "address": "50170 Le Mont-Saint-Michel",
    "prices": [],
    "themes": [
      {
        "id": 3,
        "name": "history",
        "value": 3
      },
      {
        "id": 4,
        "name": "art",
        "value": 7
      }
    ]
  }
]
```

Quand nous récupérons les suggestions pour notre utilisateur, nous obtenons :

![Récupération des suggestions pour l'utilisateur avec Postman](http://zestedesavoir.com/media/galleries/3183/8e0c74db-049b-424c-b521-918a266cc765.png)

```json
[
  {
    "id": 2,
    "name": "Mont-Saint-Michel",
    "address": "50170 Le Mont-Saint-Michel",
    "prices": [],
    "themes": [
      {
        "id": 3,
        "name": "history",
        "value": 3
      },
      {
        "id": 4,
        "name": "art",
        "value": 7
      }
    ]
  }
]
```

Nous avons donc un lieu dans notre application qui correspondrait aux gouts de notre utilisateur. 