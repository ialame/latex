# Notre première ressource

> Une interface REST gravite autour de ressources. À partir du moment où vous devez interagir avec une entité de votre application, créer une entité, la modifier, la consulter ou encore l'identifier de manière unique, vous avez pour la plupart des cas une ressource.

L'application que nous devons développer enregistre plusieurs **lieux** (monuments, centres de loisirs, châteaux, etc.) et fait des suggestions de sorties/visites à des **utilisateurs**.

Dans notre application, nous aurons donc un lieu avec éventuellement les informations permettant de le décrire (nom, adresse, thème, réputation, etc.).

Nous serons surement appelés à le consulter ou à l'éditer. Voici donc notre première ressource : un lieu.

Le choix des ressources dans une API REST est très important mais leur nommage l'est autant car c'est cela qui permettra d'avoir une API cohérente.

# Les collections dans REST

A ce stade du cours, la notion de ressource doit être bien comprise. Mais il existe aussi une autre notion qui sera utile dans la conception d'une API REST : les collections.

Une collection désigne simplement un ensemble de ressources d'un même type. Dans notre cas, la liste de tous les lieux référencés dans l'application représente une collection. Et c'est idem pour la liste des utilisateurs.

# Le nommage d'une ressource

Une règle d'or à respecter, c'est la cohérence. Il faut choisir des noms de ressources simples et suivre une même logique de nommage. 
Si par exemple, une ressource est nommée au pluriel alors elle doit l'être sur toute l'API et toutes les ressources doivent être aussi au pluriel. La casse est également très importante pour la cohérence. Il faudra ainsi respecter la même casse pour toutes les ressources.

Pour le cas de notre exemple, toutes nos ressources seront en minuscule, au pluriel et en anglais. C'est un choix assez répandu dans les différentes API publiques à notre disposition.

Donc pour une collection représentant les lieux à visiter, nous aurons **places**. Dans notre URL, nous aurons alors `rest-api.local/places`.

# Accéder aux lieux déclarés dans l'application

Pour commencer, nous considérons qu'un lieu a un nom et une adresse. L'objectif est d'avoir un appel de notre API permettant d'afficher tous les lieux connus par notre application.

## La sémantique HTTP

La ressource avec laquelle nous souhaitons interagir est **places**. Notre requête HTTP doit donc se faire sur l'URL `rest-api.local/places`.
 
[[question]]
|Quelle méthode (ou verbe) HTTP utiliser : *GET*, *POST*, ou *DELETE* ?

Comme expliqué dans le modèle de maturité de Ridcharson, une API qui se veut RESTful doit utiliser les méthodes HTTP à bon escient pour interagir avec les ressources. 
Dans notre cas, nous voulons lire des données disponibles sur le serveur. Le protocole HTTP nous propose la méthode `GET` qui, selon la [RFC 7231](http://tools.ietf.org/html/rfc7231#section-4.3.1), est la méthode de base pour récupérer des informations.

![Cinématique de récupération des lieux](http://zestedesavoir.com/media/galleries/3183/ee574ab6-52b9-46fa-bf42-8d8cf7c5483b.png)

## Implémentation

Nous allons commencer par mettre en place notre appel API avec de fausses données, ensuite nous mettrons en place la persistance de celles-ci avec Doctrine.

Tout d'abord, il faut créer une entité nommée ***Place*** contenant un nom et une adresse :

```php
# src/AppBundle/Entity/Place.php
<?php
namespace AppBundle\Entity;

class Place
{
    public $name;

    public $address;

    public function __construct($name, $address)
    {
        $this->name = $name;
        $this->address = $address;
    }
}
```

Créons maintenant un nouveau contrôleur nommé ***PlaceController*** qui s'occupera de la gestions des lieux avec, pour l'instant, une seule méthode permettant de les lister.

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
    /**
     * @Route("/places", name="places_list")
     * @Method({"GET"})
     */
    public function getPlacesAction(Request $request)
    {
        return new JsonResponse([
            new Place("Tour Eiffel", "5 Avenue Anatole France, 75007 Paris"),
            new Place("Mont-Saint-Michel", "50170 Le Mont-Saint-Michel"),
            new Place("Château de Versailles", "Place d'Armes, 78000 Versailles"),
        ]);
    }
}
```

Un appel de type `GET` sur l'URL [rest-api.local/places](http://rest-api.local/places) permet d'obtenir notre liste de lieux.
```json
[
  {
    "name": "Tour Eiffel",
    "address": "5 Avenue Anatole France, 75007 Paris"
  },
  {
    "name": "Mont-Saint-Michel",
    "address": "50170 Le Mont-Saint-Michel"
  },
  {
    "name": "Château de Versailles",
    "address": "Place d'Armes, 78000 Versailles"
  }
]
```

Avec Postman :

![Récupération des lieux avec Postman](http://zestedesavoir.com/media/galleries/3183/19a74c07-7f93-45e4-a2ff-40fd61b7edea.png)

Nous allons maintenant récupérer nos lieux depuis la base de données avec Doctrine. Rajoutons un identifiant aux lieux et mettons en place les annotations sur l'entité ***Place***.
```php
# src/AppBundle/Entity/Place.php
<?php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="places")
 */
class Place
{
    /**
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue
     */
    protected $id;
    
    /**
     * @ORM\Column(type="string")
     */
    protected $name;
    
    /**
     * @ORM\Column(type="string")
     */
    protected $address;

    public function getId()
    {
        return $this->id;
    }

    public function getName()
    {
        return $this->name;
    }

    public function getAddress()
    {
        return $this->address;
    }

    public function setId($id)
    {
        $this->id = $id;
        return $this;
    }

    public function setName($name)
    {
        $this->name = $name;
        return $this;
    }

    public function setAddress($address)
    {
        $this->address = $address;
        return $this;
    }
}
```

Pour des raisons de clarté, nous allons aussi modifier le nom de notre base de données.
```yaml
# app/config/parameters.yml
parameters:
    database_host: 127.0.0.1
    database_port: null
    database_name: rest_api
    database_user: root
    database_password: null
```

Il ne reste plus qu'à créer la base de données et la table pour stocker les lieux.
```bash
php bin/console doctrine:database:create
# Réponse
# Created database `rest_api` for connection named default

php bin/console doctrine:schema:update --dump-sql --force
# Réponse
CREATE TABLE places (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, address VARCHAR(255) NOT NULL, PRIMARY KEY(id)) DE

Updating database schema...
Database schema updated successfully! "1" query was executed
```

Le jeu de données de test :

```sql
INSERT INTO `places` (`id`, `name`, `address`) VALUES (NULL, 'Tour Eiffel', '5 Avenue Anatole France, 75007 Paris'), (NULL, 'Mont-Saint-Michel', '50170 Le Mont-Saint-Michel'), (NULL, 'Château de Versailles', 'Place d''Armes, 78000 Versailles')
```

Nous disposons maintenant d'une base de données pour gérer les informations de l'application. Il ne reste plus qu'à changer l'implémentation dans notre contrôleur pour charger les données avec Doctrine.

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
    /**
     * @Route("/places", name="places_list")
     * @Method({"GET"})
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
        
        return new JsonResponse($formatted);
    }
}
```
En testant à nouveau notre appel, nous obtenons :

```json
[
  {
    "id": 1,
    "name": "Tour Eiffel",
    "address": "5 Avenue Anatole France, 75007 Paris"
  },
  {
    "id": 2,
    "name": "Mont-Saint-Michel",
    "address": "50170 Le Mont-Saint-Michel"
  },
  {
    "id": 3,
    "name": "Château de Versailles",
    "address": "Place d'Armes, 78000 Versailles"
  }
]
```

Avec Postman : 

![Récupération des lieux avec Postman](http://zestedesavoir.com/media/galleries/3183/cbd47021-75d9-4a32-a9fa-bf487e010f55.png)

# Pratiquons avec les utilisateurs

## Objectif
Maintenant que le principe pour récupérer les informations d'une liste est expliqué, nous allons faire de même avec les utilisateurs. 
Nous considérerons que les utilisateurs ont un nom, un prénom et une adresse mail et que la ressource pour désigner une liste d'utilisateur est **users**.

L'objectif est de mettre en place un appel permettant de générer la liste des utilisateurs enregistrés en base.
Voici le format de la réponse attendue :
```json
[
  {
    "id": 1,
    "firstname": "Ab",
    "lastname": "Cde",
    "email": "ab.cde@test.local"
  },
  {
    "id": 2,
    "firstname": "Ef",
    "lastname": "Ghi",
    "email": "ef.ghi@test.local"
  }
]
```

## Implémentation

### Configuration de doctrine

Comme pour les lieux, nous allons commencer par créer l'entité ***User*** et la configuration doctrine qui va avec:
```php
# src/AppBundle/Entity/User.php
<?php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
* @ORM\Entity()
* @ORM\Table(name="users")
*/
class User
{
   /**
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue
     */
    protected $id;
    
    /**
     * @ORM\Column(type="string")
     */
    protected $firstname;
    
    /**
     * @ORM\Column(type="string")
     */
    protected $lastname;

    /**
     * @ORM\Column(type="string")
     */
    protected $email;

    public function getId()
    {
        return $this->id;
    }
     
    public function setId($id)
    {
        $this->id = $id;
    }
     
    public function getFirstname()
    {
        return $this->firstname;
    }
     
    public function setFirstname($firstname)
    {
        $this->firstname = $firstname;
    }
     
    public function getLastname()
    {
        return $this->lastname;
    }
     
    public function setLastname($lastname)
    {
        $this->lastname = $lastname;
    }
     
    public function getEmail()
    {
        return $this->email;
    }
     
    public function setEmail($email)
    {
        $this->email = $email;
    }
}
```

Mettons à jour la base de données :
```bash
php bin/console doctrine:schema:update --dump-sql --force
# Réponse
#> CREATE TABLE users (id INT AUTO_INCREMENT NOT NULL, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB;

#>Updating database schema...
#>Database schema updated successfully! "1" query was executed
```

N'oublions pas le jeu de données de test :

```sql
INSERT INTO `users` (`id`, `firstname`, `lastname`, `email`) VALUES (NULL, 'Ab', 'Cde', 'ab.cde@test.local'), (NULL, 'Ef', 'Ghi', 'ef.ghi@test.local');
```

### Création du contrôleur pour les utilisateurs

Nous allons créer un contrôleur dédié aux utilisateurs. Pour l'instant, nous aurons une seule méthode permettant de les lister.

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
    /**
     * @Route("/users", name="users_list")
     * @Method({"GET"})
     */
    public function getUsersAction(Request $request)
    {
        $users = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->findAll();
        /* @var $users User[] */
        
        $formatted = [];
        foreach ($users as $user) {
            $formatted[] = [
               'id' => $user->getId(),
               'firstname' => $user->getFirstname(),
               'lastname' => $user->getLastname(),
               'email' => $user->getEmail(),
            ];
        }
        
        return new JsonResponse($formatted);
    }
}
```

En regardant le code, nous pouvons déjà remarqué que le contrôleur ***UserController*** ressemble à quelques lignes prés au contrôleur ***PlaceController***. Vu qu'avec REST nous utilisons une interface uniforme pour interagir avec nos ressources, si l'opération que nous voulons effectuer est identique, il y a de forte chance que le code pour l'implémentation le soit aussi. Cela nous permettra donc de gagner du temps dans les développements.

En testant avec Postman :

![Récupération des utilisateurs avec Postman](http://zestedesavoir.com/media/galleries/3183/d2153521-5879-472a-b592-104f25f32a7a.png)

