# Gestion des thèmes pour les lieux

Nous allons commencer notre implémentation en mettant en place la gestion des thèmes.

L'entité contiendra les champs cités plus haut avec en plus une contrainte d'unicité sur le nom d'un thème et l'identifiant du lieu.

```php
<?php
# src/AppBundle/Entity/Theme.php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="themes",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="themes_name_place_unique", columns={"name", "place_id"})}
 * )
 */
class Theme
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
     * @ORM\Column(type="integer")
     */
    protected $value;

    /**
     * @ORM\ManyToOne(targetEntity="Place", inversedBy="themes")
     * @var Place
     */
    protected $place;

    public function getId()
    {
        return $this->id;
    }
     
    public function setId($id)
    {
        $this->id = $id;
    }
     
    public function getName()
    {
        return $this->name;
    }
     
    public function setName($name)
    {
        $this->name = $name;
    }
     
    public function getValue()
    {
        return $this->value;
    }
     
    public function setValue($value)
    {
        $this->value = $value;
    }
     
    public function getPlace()
    {
        return $this->place;
    }
     
    public function setPlace(Place $place)
    {
        $this->place = $place;
    }
}
```

L'entité `Place` doit aussi être modifiée pour avoir une relation bidirectionnelle.
```php
<?php
# src/AppBundle/Entity/Place.php

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
    // ...

    /**
     * @ORM\OneToMany(targetEntity="Theme", mappedBy="place")
     * @var Theme[]
     */
    protected $themes;

    public function __construct()
    {
        $this->prices = new ArrayCollection();
        $this->themes = new ArrayCollection();
    }
    // ...

    public function getThemes()
    {
        return $this->themes;
    }
     
    public function setThemes($themes)
    {
        $this->themes = $themes;
    }
}
```

Pour supporter la création de thèmes pour les lieux, nous allons créer un formulaire Symfony et les régles de validation associées.

```php
<?php
# src/AppBundle/Form/Type/ThemeType.php

namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ThemeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('name');
        $builder->add('value');
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\Theme',
            'csrf_protection' => false
        ]);
    }
}

```

[[information]]
|La liste des thèmes prédéfinis est utilisée pour valider le formulaire Symfony. 

```yaml
# src/AppBundle/Resources/config/validation.yml

AppBundle\Entity\Theme:
    properties:
        name:
            - NotNull: ~
            - Choice:
                choices: [art, architecture, history, science-fiction, sport]
        value:
            - NotNull: ~
            - Type: numeric
            - GreaterThan:
                value: 0
            - LessThanOrEqual:
                value: 10

```

Pour ajouter un thème, nous allons créer un nouveau contrôleur qui ressemble à quelques lignes près à ce que nous avons déjà fait jusqu'ici.
Nous allons en profiter pour ajouter une méthode pour lister les thèmes d'un lieu donné.

```php
<?php
# src/AppBundle/Controller/Place/ThemeController.php

namespace AppBundle\Controller\Place;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\ThemeType;
use AppBundle\Entity\Theme;

class ThemeController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"theme"})
     * @Rest\Get("/places/{id}/themes")
     */
    public function getThemesAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id'));
        /* @var $place Place */

        if (empty($place)) {
            return $this->placeNotFound();
        }

        return $place->getThemes();
    }


     /**
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"theme"})
     * @Rest\Post("/places/{id}/themes")
     */
    public function postThemesAction(Request $request)
    {
        $place = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->find($request->get('id'));
        /* @var $place Place */

        if (empty($place)) {
            return $this->placeNotFound();
        }

        $theme = new Theme();
        $theme->setPlace($place);
        $form = $this->createForm(ThemeType::class, $theme);

        $form->submit($request->request->all());

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            $em->persist($theme);
            $em->flush();
            return $theme;
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
Le fichier de routage de l'application doit être modifié en conséquence pour charger ce nouveau contrôleur.

```yaml
# app/config/routing.yml

# ...

themes:
    type:     rest
    resource: AppBundle\Controller\Place\ThemeController

# ...
```

Il ne faut pas oublier de rajouter un nouveau groupe de sérialisation pour la gestion de ces thèmes.

```yaml
# src/AppBundle/Resources/config/serialization.yml
AppBundle\Entity\Place:
    attributes:
        id:
            groups: ['place', 'price', 'theme']
        name:
            groups: ['place', 'price', 'theme']
        address:
            groups: ['place', 'price', 'theme']
        prices:
            groups: ['place']
        themes:
            groups: ['place']

# ...

AppBundle\Entity\Theme:
    attributes:
        id:
            groups: ['place', 'theme']
        name:
            groups: ['place', 'theme']
        value:
            groups: ['place', 'theme']
        place:
            groups: ['theme']
```

[[information]]
|Le nouveau groupe est aussi utilisé pour configurer la sérialisation de l'entité `Place`afin  d'éviter les références circulaires.


# Gestions des préférences

Pour la gestion des utilisateurs, nous allons suivre exactement le même schéma d'implémentation. Les extraits de code fournis se passeront donc de commentaires.

Commençons par l'entité pour la gestion des préférences et le formulaire permettant de le gérer.
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
     * @ORM\Column(type="integer")
     */
    protected $value;

    /**
     * @ORM\ManyToOne(targetEntity="User", inversedBy="preferences")
     * @var User
     */
    protected $user;

    public function getId()
    {
        return $this->id;
    }
     
    public function setId($id)
    {
        $this->id = $id;
    }
     
    public function getName()
    {
        return $this->name;
    }
     
    public function setName($name)
    {
        $this->name = $name;
    }
     
    public function getValue()
    {
        return $this->value;
    }
     
    public function setValue($value)
    {
        $this->value = $value;
    }
     
    public function getUser()
    {
        return $this->user;
    }
     
    public function setUser(User $user)
    {
        $this->user = $user;
    }
}
```

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
    // ...

     /**
     * @ORM\OneToMany(targetEntity="Preference", mappedBy="user")
     * @var Preference[]
     */
    protected $preferences;

    public function __construct()
    {
        $this->preferences = new ArrayCollection();
    }

    // ...

    public function getPreferences()
    {
        return $this->preferences;
    }
     
    public function setPreferences($preferences)
    {
        $this->preferences = $preferences;
    }
}
```

Le formulaire associé et les règles de validation sont proches de celui des thèmes.

```php
<?php
# src/AppBundle/Form/Type/PreferenceType.php

namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PreferenceType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('name');
        $builder->add('value');
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\Preference',
            'csrf_protection' => false
        ]);
    }
}

```

```yaml
# src/AppBundle/Resources/config/validation.yml

# ...

AppBundle\Entity\Preference:
    properties:
        name:
            - NotNull: ~
            - Choice:
                choices: [art, architecture, history, science-fiction, sport]
        value:
            - NotNull: ~
            - Type: numeric
            - GreaterThan:
                value: 0
            - LessThanOrEqual:
                value: 10

```

Un nouveau contrôleur sera aussi créé pour assurer la gestion des préférences utilisateurs via notre API.

```php
<?php
# src/AppBundle/Controller/User/PreferenceController.php

namespace AppBundle\Controller\User;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\PreferenceType;
use AppBundle\Entity\Preference;

class PreferenceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"preference"})
     * @Rest\Get("/users/{id}/preferences")
     */
    public function getPreferencesAction(Request $request)
    {
        $user = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->find($request->get('id'));
        /* @var $user User */

        if (empty($user)) {
            return $this->userNotFound();
        }

        return $user->getPreferences();
    }


     /**
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"preference"})
     * @Rest\Post("/users/{id}/preferences")
     */
    public function postPreferencesAction(Request $request)
    {
        $user = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:User')
                ->find($request->get('id'));
        /* @var $user User */

        if (empty($user)) {
            return $this->userNotFound();
        }

        $preference = new Preference();
        $preference->setUser($user);
        $form = $this->createForm(PreferenceType::class, $preference);

        $form->submit($request->request->all());

        if ($form->isValid()) {
            $em = $this->get('doctrine.orm.entity_manager');
            $em->persist($preference);
            $em->flush();
            return $preference;
        } else {
            return $form;
        }
    }

    private function userNotFound()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'User not found'], Response::HTTP_NOT_FOUND);
    }
}
```

```yaml
# app/config/routing.yml

# ...

preferences:
    type:     rest
    resource: AppBundle\Controller\User\PreferenceController

```
Les groupes de sérialisation doivent aussi être mis à jour afin d'éviter les fameuses références circulaires.

```yam
# src/AppBundle/Resources/config/serialization.yml

# ...

AppBundle\Entity\User:
    attributes:
        id:
            groups: ['user', 'preference']
        firstname:
            groups: ['user', 'preference']
        lastname:
            groups: ['user', 'preference']
        email:
            groups: ['user', 'preference']
        preferences:
            groups: ['user']

AppBundle\Entity\Preference:
    attributes:
        id:
            groups: ['user', 'preference']
        name:
            groups: ['user', 'preference']
        value:
            groups: ['user', 'preference']
        user:
            groups: ['preference']

```

Avec ces modifications que nous venons d'apporter, nous pouvons maintenant associer des thèmes et des préférences respectivement aux lieux et aux utilisateurs.
Nous allons donc finaliser ce chapitre en rajoutant enfin les suggestions.

