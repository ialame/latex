
Le schéma que nous allons adopter doit maintenant être familier. Plus tôt dans ce cours, nous avions :

> Pour concevoir une bonne API RESTful, il faut donc toujours se poser ces questions :

>- Sur quelle ressource mon opération doit s'effectuer ?
- Quel verbe HTTP décrit le mieux cette opération ?
- Quelle URL permet d'identifier la ressource ?
- et quel code de statut doit décrire la réponse ?

Nous allons donc suivre ce conseil, et rajouter une action permettant de créer un lieu dans notre application.

# Quelle est la ressource cible ?
[[question]]
|La première question que nous devons nous poser est sur quelle ressource pouvons-nous faire un appel de création ? 
De point de vue sémantique, nous pouvons considérer qu'une entité dans une application est accessible en utilisant la collection (**places**) ou en utilisant directement la ressource à travers son identifiant (**places/1**). Mais comme vous vous en doutez, une ressource que nous n'avons pas encore créé ne peut pas avoir d'identifiant.

Il faut donc voire la *création* d'une ressource comme étant *l'ajout* de celle-ci dans une *collection*.

Créer un lieu revient donc à rajouter un lieu à notre liste déjà existante. Pour créer une ressource, il faudra donc utiliser la collection associée.

# Quel verbe HTTP ?

Pour identifier notre collection, nous utiliserons l'URL `rest-api.local/places`. Mais quel appel doit-on faire ?
Les verbes HTTP ont chacun une signification et une utilisation bien définie. Pour la création, la méthode `POST` est bien appropriée. Pour s'en convaincre, il suffit de consulter la [RFC 7231](http://tools.ietf.org/html/rfc7231#section-4.3.3) qui dit :

>For example, POST is used for the following functions (among others

>+ Providing a block of data, such as the fields entered into an HTML form, to a data-handling process;
 +  Posting a message to a bulletin board, newsgroup, mailing list, blog, or similar group of articles;
 + **Creating a new resource that has yet to be identified by the origin server;**

-

> POST est utilisé pour les fonctions suivantes (entre autres) :

>+ ...
 + ...
 + **Création d'une nouvelle ressource qui n'a pas encore été identifiée par le serveur d'origine;**

# Le corps de notre requête 

Maintenant que nous savons qu'il faudra une requête du type `POST rest-api.local/places`, nous allons nous intéresser au corps de notre requête : **le payload** (dans le jargon API).

Lorsque nous soumettons un formulaire sur une page web avec la méthode `POST`, le contenu est encodé en utilisant les encodages _application/x-www-form-urlencoded_ ou encore _multipart/form-data_ que vous avez sûrement déjà rencontrés.

Pour le cas d'une API, nous pouvons utiliser le format que nous voulons dans le corps de nos requêtes tant que le serveur supporte ce format. Nous allons donc choisir le JSON comme format. 
[[information]]
|Ce choix n'est en aucun cas lié au format de sortie de nos réponses. Le JSON reste un format textuel largement utilisé et supporté et représente souvent le minimum à supporter par une API REST. Ceci étant dit, supporter le format JSON n'est pas une contrainte REST. 

# Quel code de statut HTTP ?

Pour rappels, les codes de statut HTTP peuvent être regroupés par *famille*. Le premier chiffre permet d'identifier la famille de chaque code. Ainsi les codes de la famille *2XX* (200, 201, 204, etc.) décrivent une requête qui s'est effectué avec succès, la famille *4XX* (400, 404, etc.) pour une erreur côté client et enfin la famille *5XX* (500, etc.) pour une erreur serveur.
La liste complète des codes de statut et leur signification est disponible dans la [section 6 de la RFC 7231](http://tools.ietf.org/html/rfc7231#section-6).
Mais pour notre cas, une seule nous intéresse: `201 Created`. Le message associé à ce code parle de lui-même, si une ressource a été créée avec succès, nous utiliserons donc le code `201`.

![Cinématique de création d'un lieu](http://zestedesavoir.com/media/galleries/3183/4501d22f-8e63-4fd7-945f-e5f010e2200e.png)

# Créer un nouveau lieu

Mettons en pratique tout cela en donnant la possibilité aux utilisateurs de notre API de créer un lieu.
Un utilisateur devra faire une requête `POST` sur l'URL `rest-api.local/places` avec comme payload :
```json
{
    "name": "ici un nom",
    "address": "ici une adresse"
}
```

[[attention]]
|Le corps de la requête ne contient pas l'identifiant vu que nous allons le créer côté serveur.

Pour des soucis de clarté, les méthodes déjà existantes dans le contrôleur ***PlaceController*** ne seront pas visibles dans les extraits de code. 
Commençons donc par créer une route et en configurant le routage comme il faut:
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
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
        
    }
}
```

Pour tester la méthode, nous allons tout d'abord simplement renvoyer les informations qui seront dans le payload.

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
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
         return [
            'payload' => [
                $request->get('name'),
                $request->get('address')
             ]
        ];
    }
}
```

Pour tester cette méthode, nous allons utiliser Postman.

![Payload pour la création d'un lieu](http://zestedesavoir.com/media/galleries/3183/58e5ce2a-a985-48d9-adec-8805c5c4eea1.png)

Il faut choisir comme contenu JSON, Postman rajoutera automatiquement l'entête `Content-Type` qu'il faut à la requête. Nous explorerons plus en détails ces entêtes plus tard dans ce cours.

![Entête rajoutée par Postman](http://zestedesavoir.com/media/galleries/3183/c9d13097-2262-44a6-b513-a01af246dd34.png)

La réponse obtenue est :

![Réponse temporaire pour la création d'un lieu](http://zestedesavoir.com/media/galleries/3183/362c4618-3f04-40f5-ab94-f203e8645c30.png)

Nous avons maintenant un système opérationnel pour récupérer les informations pour créer notre lieu. Mais avant de continuer, un petit aparté sur *FOSRestBundle* s'impose.

## Le body listener de FOSRestBundle 

Il faut savoir que de base, Symfony ne peut pas peupler les paramétres de l'objet `Request` avec le payload `JSON`. Dans une application n'utilisant pas *FOSRestBundle*, il faudrait parser manuellement le contenu en faisant `json_decode($request->getContent(), true)` pour accéder au nom et à l'adresse du lieu.

Pour s'en convaincre, nous allons désactiver le body listener qui est activé par défaut.
```yaml
# app/config/config.yml
fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
    format_listener:
        rules:
            - { path: '^/', priorities: ['json'], fallback_format: 'json', prefer_extension: false }
    # configuration à rajouter pour désactiver le body listener
    body_listener:
        enabled: false
```

La réponse que nous obtenons est tout autre :

![Réponse sans le body listener de FOSRestBundle](http://zestedesavoir.com/media/galleries/3183/f9011b00-8695-4edd-86f7-a347692b81ed.png)

En remplaçant, le code actuel par :

```php
<?php
return [
    'payload' => json_decode($request->getContent(), true)
];
```

Nous retrouvons la première réponse.

Là aussi *FOSRestBundle* nous facilite le travail et tout parait transparent pour nous. Il faut juste garder en tête que ce listener existe et fait la transformation nécessaire pour nous.

Avant de continuer, nous allons le réactiver :
```yaml
    body_listener:
        enabled: true
```

## Sauvegarde en base
Maintenant que nous avons les informations nécessaires pour créer un lieu, nous allons juste l'insérer en base avec Doctrine. Pour définir le bon code de statut, il suffit de mettre un paramètre `statusCode=Response::HTTP_CREATED` dans l'annotation `View`.

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
     * @Rest\View(statusCode=Response::HTTP_CREATED)
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
        $place = new Place();
        $place->setName($request->get('name'))
            ->setAddress($request->get('address'));

        $em = $this->get('doctrine.orm.entity_manager');
        $em->persist($place);
        $em->flush();

        return $place;
    }
}
```

Ici, en renvoyant la ressource qui vient d'être créée, nous suivons la [RFC 7231](http://tools.ietf.org/html/rfc7231#section-6.3.2).

> The 201 response payload typically describes and links to the resource(s) created.

Pour les tester notre implémentation, nous allons utiliser :
```json
{
    "name": "Disneyland Paris",
    "address": "77777 Marne-la-Vallée"
}
```

La réponse renvoyée avec le bon code de statut.

![Code de statut de la réponse](http://zestedesavoir.com/media/galleries/3183/fbc7a46d-e668-4372-8d4c-6d9ca5748534.png)

![Réponse de la création d'un lieu dans Postman](http://zestedesavoir.com/media/galleries/3183/f566322a-64b8-4093-a9f8-0529360c4ade.png)

## Validation des données
Bien que nous puissions créer avec succès un lieu, nous n'effectuons aucune validation. Dans cette partie, nous allons voir comment valider les informations en utilisant les formulaires de Symfony.

Nous allons commencer par créer un formulaire pour les lieux :
```php
# src/AppBundle/Form/Type/PlaceType.php
<?php
namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PlaceType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('name');
        $builder->add('address');
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\Place',
            'csrf_protection' => false
        ]);
    }
}
```

Dans une API, il faut obligatoirement désactiver la protection CSRF (Cross-Site Request Forgery). Nous n'utilisons pas de session et l'utilisateur de l'API peut appeler cette méthode sans se soucier de l'état de l'application : l'API doit rester sans état : stateless.

Nous allons maintenant rajouter des contraintes simples pour notre lieu.
Le nom et l'adresse ne doivent pas être nulles et en plus, le nom doit être unique.
Nous utiliserons le format YAML pour les règles de validations.
```yaml
# src/AppBundle/Resources/config/validation.yml
AppBundle\Entity\Place:
    constraints:
        - Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity: name
    properties:
        name:
            - NotBlank: ~
            - Type: string
        address:
            - NotBlank: ~
            - Type: string

```

Jusque-là rien de nouveau avec les formulaires Symfony. Si ce code ne vous parait pas assez claire. Il est préférable de consulter [la documentation officielle](http://symfony.com/doc/current/book/validation.html) avant de continuer ce cours.

[[information]]
|Vu que nous avons une contrainte d'unicité sur le champ `name`. Il est plus logique de rajouter cela dans les annotations Doctrine. 
```php
# src/AppBundle/Entity/Place.php
<?php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="places",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="places_name_unique",columns={"name"})}
 * )
 */
class Place
{
    // ...
}
```

Il ne reste plus qu'à exploiter le formulaire dans notre contrôleur.
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
     * @Rest\View(statusCode=Response::HTTP_CREATED)
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
        $place = new Place();
        $form = $this->createForm(PlaceType::class, $place);

        $form->submit($request->request->all()); // Validation des données

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

Le format des données attendu lorsqu'on utilise la méthode `handleRequest` des formulaires Symfony est un peu différent de celui que nous utilisons pour créer un lieu.
Avec `handleRequest`, nous aurions dû utiliser :
```json
{
    "place":
        {
            "name": "ici un nom",
            "address": "ici une adresse"
        }
}
```

Où l'attribut `place` est le nom de notre formulaire Symfony.

Donc pour mieux répondre aux contraintes REST, au lieu d'utiliser la méthode `handleRequest` pour soumettre le formulaire, nous avons opté pour [la soumission manuelle](http://symfony.com/doc/current/cookbook/form/direct_submit.html) avec `submit`. Nous adaptons Symfony à REST et pas l'inverse.

## Gestion des erreurs avec FOSRestBundle

Lorsque le formulaire n'est pas valide, nous nous contentons juste de renvoyer le formulaire. Le ViewHandler de FOSRestBundle est conçu pour gérer nativement les formulaires invalides.

Non seulement, il est en mesure de formater les erreurs dans le formulaire mais en plus, il renvoie le bon code de statut lorsque les données soumises sont invalide: `400`.
Le code de statut `400` permet de signaler au client de l'API que sa requête est invalide. 

Pour s'en assurer, essayons de recréer un autre lieu avec les mêmes informations que le précédent.

![Code de statut pour un formulaire invalide](http://zestedesavoir.com/media/galleries/3183/03a6892f-a3b1-46b4-904b-a6f6f5d15400.png)

```json
{
  "code": 400,
  "message": "Validation Failed",
  "errors": {
    "children": {
      "name": {
        "errors": [
          "This value is already used."
        ]
      },
      "address": []
    }
  }
}
```
Si la clé `errors` d'un attribut existe, alors il y a des erreurs de validation sur cet attribut.

# Pratiquons avec les utilisateurs

Comme pour les lieux, nous allons créer une action permettant de rajouter un utilisateur à notre application. Nous aurons comme contraintes :

- le prénom, le nom et l'adresse mail de l'utilisateur ne doivent pas être nuls ;
- et l'adresse mail doit être unique.

Pour créer un utilisateur, un client de l'API devra envoyer une requête au format : 
```json
{
    "firstname": "",
    "lastname": "",
    "email": ""
}
```

Comme pour les lieux, pour créer un utilisateur il faudra une requête `POST` sur l'URL `rest-api.local/users` qui désigne notre collection d'utilisateurs.

Allons-y !

Configuration du formulaire et des contraintes de validation :
```php
# src/AppBundle/Form/Type/UserType.php
<?php
namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\EmailType;

class UserType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('firstname');
        $builder->add('lastname');
        $builder->add('email', EmailType::class);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\User',
            'csrf_protection' => false
        ]);
    }
}
```

```yaml
# src/AppBundle/Resources/config/validation.yml

# ...

AppBundle\Entity\User:
    constraints:
        - Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity: email
    properties:
        firstname:
            - NotBlank: ~
            - Type: string
        lastname:
            - NotBlank: ~
            - Type: string
        email:
            - NotBlank: ~
            - Email: ~
```

Rajout d'une contrainte d'unicité dans Doctrine :
```php
# src/AppBundle/Entity/User.php
<?php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
* @ORM\Entity()
* @ORM\Table(name="users",
*      uniqueConstraints={@ORM\UniqueConstraint(name="users_email_unique",columns={"email"})}
* )
*/
class User
{
    // ...
}
```

Utilisation de notre formulaire dans le contrôleur :
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
     * @Rest\View(statusCode=Response::HTTP_CREATED)
     * @Rest\Post("/users")
     */
    public function postUsersAction(Request $request)
    {
        $user = new User();
        $form = $this->createForm(UserType::class, $user);

        $form->submit($request->request->all());

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
Nous pouvons maintenant créer des utilisateurs grâce à l'API.