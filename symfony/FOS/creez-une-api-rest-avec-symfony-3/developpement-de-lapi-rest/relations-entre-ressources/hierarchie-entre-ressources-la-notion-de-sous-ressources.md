# Un peu de conception

Supposons qu'un lieu ait un ou plusieurs tarifs par exemple moins de 12 ans et tout public.
En termes de conception de la base de données, une relation `oneToMany` permet de gérer facilement cette situation et donc d'interagir avec les tarifs d'un lieu donné. 

[[question]]
|Comment matérialiser une telle relation avec une API qui suit les contraintes REST ?

Si nous créons une URI nommée `rest-api.local/prices`, nous pouvons effectivement accéder à nos prix comme pour les lieux ou les utilisateurs. Mais nous aurons accès à **l'ensemble des tarifs** appliqués pour **tous les lieux** de notre application. 

Pour accéder aux prix d'un lieu `1`, il serait tentant de rajouter un paramètre du style `rest-api.local/prices?place_id=1` mais, la répétition étant pédagogique, nous allons regarder à nouveau le deuxième chapitre "Premières interactions avec les ressources" :

> la [RFC 3986](https://tools.ietf.org/html/rfc3986#section-3.4) spécifie clairement les query strings comme étant des composants qui contiennent des données *non-hiérarchiques*.

Nous avons une notion d'hièrarchie entre un lieu et **ses** tarifs et donc cette relation doit apparaitre dans notre URI.

`rest-api.local/prices/1` ferait-il l'affaire ? Sûrement pas, cette URL désigne le tarif ayant comme identifiant `1`.

Pour trouver la bonne URL, nous devons commencer par le possesseur dans la relation ici c'est un lieu qui a des tarifs, donc `rest-api.local/places/{id}` doit être le début de notre URL. Ensuite, il suffit de rajouter l'identifiant de la collection de prix que nous appelerons `prices`.

En définitif, `rest-api.local/places/{id}/prices` permet de désigner clairement les tarifs pour le lieu ayant comme identifiant `{id}`.

![Hiérarchie entre les ressources](http://zestedesavoir.com/media/galleries/3183/1f03ca59-2b22-4589-8fee-01941d171561.png)

Une fois que nous avons identifié notre ressource, tous les principes déjà abordés pour interagir avec une ressource s'appliquent.

->

|Opération souhaitée                 | Verbe HTTP | URI                | 
|------------------------------------|------------|--------------------|
|Récupérer tous les prix d'un lieu   |    GET     | /places{id}/prices |
|Récupérer un seul prix d'un lieu    |    GET     | /places/{id}/prices/{price_id}|
|Créer un nouveau prix pour un lieu   | POST      | /places{id}/prices  |
|Suppression d'un prix pour un lieu   | DELETE    |/places/{id}/prices/{price_id}|
|Mise à jour complète d'un prix d'un lieu  | PUT |/places/{id}/prices/{price_id}|
|Mise à jour partielle d'un prix d'un lieu  | PATCH|/places/{id}/prices/{price_id}|

<-

# Pratiquons avec les lieux
Pour mettre en pratique toutes ces informations, nous allons ajouter deux nouveaux appels à notre API :

- un pour créer un nouveau prix ;
- un pour lister tous les prix d'un lieu.

Nous considérons qu'un prix a deux caractéristiques :

- un type (tout public, moins de 12 ans, etc.) ;
- une valeur (10, 15.5, 22.75, 29.99, etc.) qui désigne le tarif en euros.

Pour l'instant, seul deux types de prix sont supportés :

- *less_than_12* pour moins de 12 ans ;
- *for_all* pour tout public.

Commençons par la base de données en créant une nouvelle entité ***Price***, nous rajouterons une contrainte d'unicité sur le type et le lieu afin de nous assurer qu'un lieu ne pourra pas avoir deux prix du même type :
```php
# src/AppBundle/Entity/Price.php
<?php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="prices",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="prices_type_place_unique", columns={"type", "place_id"})}
 * )
 */
class Price
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
    protected $type;
    
    /**
     * @ORM\Column(type="float")
     */
    protected $value;

    /**
     * @ORM\ManyToOne(targetEntity="Place", inversedBy="prices")
     * @var Place
     */
    protected $place;
 
    // tous les getters et setters
}
```

Nous utilisons une relation bidirectionnelle car nous voulons afficher les prix d'un lieu en plus des informations de base lorsqu'un client de l'API consulte les informations de ce lieu.

```php
# src/AppBundle/Entity/Place.php
<?php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Doctrine\Common\Collections\ArrayCollection;

/**
 * @ORM\Entity()
 * @ORM\Table(name="places",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="places_name_unique",columns={"name"})}
 * )
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

    /**
     * @ORM\OneToMany(targetEntity="Price", mappedBy="place")
     * @var Price[]
     */
    protected $prices;

    public function __construct()
    {
        $this->prices = new ArrayCollection();
    }
    // tous les getters et setters
}
```

N'oublions pas de mettre à jour la base de données avec : 
```bash
php bin/console doctrine:schema:update --dump-sql --force
```

La création d'un prix nécessite quelques règles de validation que nous devons implémenter.
```yaml
# src/AppBundle/Resources/config/validation.yml

# ...

AppBundle\Entity\Price:
    properties:
        type:
            - NotNull: ~
            - Choice:
                choices: [less_than_12, for_all]
        value:
            - NotNull: ~
            - Type: numeric
            - GreaterThanOrEqual:
                value: 0
```

Il ne reste plus qu'à créer le formulaire associé :
```php
# src/AppBundle/Form/Type/PriceType.php
<?php
namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PriceType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        // Pas besoin de rajouter les options avec ChoiceType vu que nous allons l'utiliser via API.
        // Le formulaire ne sera jamais affiché
        $builder->add('type');
        $builder->add('value');
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\Price',
            'csrf_protection' => false
        ]);
    }
}
```

Les deux appels seront mis en place dans un nouveau contrôleur pour des raisons de clarté. Mais il est parfaitement possible de le mettre dans le contrôleur déjà existant. Nous aurons un nouveau dossier nommé `src/AppBundle/Controller/Place` qui contiendra un contrôleur `PriceController`.

Avec ce découpage des fichiers, nous mettons en évidence la relation hiérarchique entre *Place* et *Price*.

```php
# src/AppBundle/Controller/Place/PriceController.php
<?php
namespace AppBundle\Controller\Place;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations

class PriceController extends Controller
{

    /**
     * @Rest\View()
     * @Rest\Get("/places/{id}/prices")
     */
    public function getPricesAction(Request $request)
    {
       
    }

     /**
     * @Rest\View(statusCode=Response::HTTP_CREATED)
     * @Rest\Post("/places/{id}/prices")
     */
    public function postPricesAction(Request $request)
    {
       
    }
}
```

```yaml
# app/config/routing.yml
app:
    resource: "@AppBundle/Controller/DefaultController.php"
    type:     annotation

places:
    type:     rest
    resource: AppBundle\Controller\PlaceController

prices:
    type:     rest
    resource: AppBundle\Controller\Place\PriceController

users:
    type:     rest
    resource: AppBundle\Controller\UserController
```

Au niveau des URL utilisées dans le routage, il suffit de se référer au tableau plus haut. 
Finissons notre implémentation en ajoutant de la logique aux actions du contrôleur :
```php
# src/AppBundle/Controller/Place/PriceController.php
<?php
namespace AppBundle\Controller\Place;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\PriceType;
use AppBundle\Entity\Price;

class PriceController extends Controller
{

    /**
     * @Rest\View()
     * @Rest\Get("/places/{id}/prices")
     */
    public function getPricesAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id')); // L'identifiant en tant que paramétre n'est plus nécessaire
        /* @var $place Place */

        if (empty($place)) {
            return $this->placeNotFound();
        }

        return $place->getPrices();
    }


     /**
     * @Rest\View(statusCode=Response::HTTP_CREATED)
     * @Rest\Post("/places/{id}/prices")
     */
    public function postPricesAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id'));
        /* @var $place Place */

        if (empty($place)) {
            return $this->placeNotFound();
        }

        $price = new Price();
        $price->setPlace($place); // Ici, le lieu est associé au prix
        $form = $this->createForm(PriceType::class, $price);

        // Le paramétre false dit à Symfony de garder les valeurs dans notre
        // entité si l'utilisateur n'en fournit pas une dans sa requête
        $form->submit($request->request->all());

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            $em->persist($price);
            $em->flush();
            return $price;
        } else {
            return $form;
        }
    }

    private function placeNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);
    }
}
```

Le principe reste le même qu'avec les différentes actions que nous avons déjà implémentées.
Il faut juste noter que lorsque nous créons un prix, nous pouvons lui associer un lieu en récupérant l'identifiant du lieu qui est dans l'URL de la requête.

Pour tester nos nouveaux appels, nous allons créer un nouveau prix pour le lieu. Voici le payload JSON utilisé :

```json
{
    "type": "less_than_12",
    "value":  5.75
}
```

Requête :

![Corps de la requête de création d'un lieu avec Postman](http://zestedesavoir.com/media/galleries/3183/d9e353f5-61e9-4d03-ac7c-221d73368155.png)

Réponse :

```json
{
  "error": {
    "code": 500,
    "message": "Internal Server Error",
    "exception": [
      {
        "message": "A circular reference has been detected (configured limit: 1).",
        "class": "Symfony\\Component\\Serializer\\Exception\\CircularReferenceException",
        "trace": [ "..." ]
      }
    ]
  }
}
```

[[erreur]]
|Nous obtenons une belle erreur interne ! Pourquoi une exception avec comme message `A circular reference has been detected (configured limit: 1).` ?