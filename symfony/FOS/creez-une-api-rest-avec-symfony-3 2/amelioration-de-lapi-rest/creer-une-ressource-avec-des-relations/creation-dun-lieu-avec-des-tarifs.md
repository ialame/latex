# Un peu de conception

Vu que nous avons déjà une méthode pour créer des tarifs, nous allons utiliser le même payload pour la création d'un lieu pour garder une API cohérente. Le payload existant doit être maintenant :

```json
{
  "name": "Disneyland Paris",
  "address": "77777 Marne-la-Vallée",
  "prices": [
    {
      "type": "for_all",
      "value": 10.0
    },
    {
      "type": "less_than_12",
      "value": 5.75
    }
  ]
}
```

L'attribut `prices` est un tableau qui contiendra la liste des prix que nous voulons rajouter à la création du lieu.

Les tarifs resteront optionnels ce qui nous permettra de créer des lieux avec ou sans. Nous allons sans plus attendre appliquer ces modifications à l'appel existant.

# Implémentation

## Mise à jour du formulaire

La méthode pour créer un lieu reste inchangée. Nous devons juste changer le formulaire des lieux et le traitement associé. 

```php
<?php
# src/AppBundle/Form/Type/PlaceType.php

namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CollectionType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PlaceType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('name');
        $builder->add('address');
        $builder->add('prices', CollectionType::class, [
            'entry_type' => PriceType::class,
            'allow_add' => true,
            'error_bubbling' => false,
        ]);
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

La configuration du formulaire est typique des formulaires Symfony avec une collection. [La documentation officielle](http://symfony.com/doc/current/cookbook/form/form_collections.html) aborde le sujet d'une manière plus complète.

Les règles de validation pour les thèmes existent déjà. Pour les utiliser, nous devons modifier la validation de l'entité ***Place*** en rajoutant la règle `Valid`. Avec cette annotation, nous disons à Symfony de valider l'attribut `prices` en utilisant les contraintes de validation de l'entité ***Price***.

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
        prices:
            - Valid: ~
```


Notez qu'il n'y a pas d'assertions de type `NotBlank` puisque l'attribut `prices` est optionnel.

## Traitement du formulaire

Avec les modifications que nous venons d'apporter, nous pouvons déjà tester la création d'un lieu avec des prix. Mais avant de le faire, nous allons rapidement adapter le contrôleur pour gérer la sauvegarde des prix.

```php
<?php
# src/AppBundle/Controller/PlaceController.php

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
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"place"})
     * @Rest\Post("/places")
     */
    public function postPlacesAction(Request $request)
    {
        $place = new Place();
        $form = $this->createForm(PlaceType::class, $place);

        $form->submit($request->request->all());

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            foreach ($place->getPrices() as $price) {
                $price->setPlace($place);
                $em->persist($price);
            }
            $em->persist($place);
            $em->flush();
            return $place;
        } else {
            return $form;
        }
    }

    // ...

```

[[attention]]
| Comme vous l'avez sûrement remarqué, toute la logique de notre API est regroupée dans les contrôleurs. Ceci n'est pas une bonne pratique et l'utilisation d'un service dédié est vivement conseillée pour une application destinée à une mise en production.


L'entité ***Place*** a été légèrement modifiée. L'attribut `prices` est maintenant initialisé avec une collection vide.
```php
<?php
# src/AppBundle/Entity/Place.php

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

    /**
     * @ORM\OneToMany(targetEntity="Price", mappedBy="place")
     * @var Price[]
     */
    protected $prices;

    public function __construct()
    {
        $this->prices = new ArrayCollection();
        // ...
    }

   // ...
```

En testant une création d'un lieu avec des prix :

```json
{
  "name": "Musée du Louvre",
  "address": "799, rue de Rivoli, 75001 Paris",
  "prices": [
    {
      "type": "less_than_12",
      "value": 6
    },
    {
      "type": "for_all",
      "value": 15
    }
  ]
}
```

![Requête de création d'un lieu avec des prix](https://zestedesavoir.com/media/galleries/3183/10010a73-68e8-47ef-b23a-64f519477e58.png)

La réponse  est identique à ce que nous avons déjà eu mais les prix sont enregistrés en même temps.

```json
{
  "id": 9,
  "name": "Musée du Louvre",
  "address": "799, rue de Rivoli, 75001 Paris",
  "prices": [
    {
      "id": 6,
      "type": "less_than_12",
      "value": 6
    },
    {
      "id": 7,
      "type": "for_all",
      "value": 15
    }
  ],
  "themes": []
}
```

Nous pouvons maintenant créer un lieu tout en rajoutant des prix et le principe peut même être élargi pour les thèmes des lieux et les préférences des utilisateurs.
