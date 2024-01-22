Avant de créer un token, nous devons mettre à jour notre modèle de données. Un utilisateur doit maintenant avoir un mot de passe et son *adresse mail* sera son *login*.
Pour la gestion de ce mot de passe, nous utiliserons [les outils](http://symfony.com/doc/current/cookbook/doctrine/registration_form.html) que nous propose Symfony.

Le nouveau modèle utilisateur :
```php
# src/AppBundle/Entity/User.php
<?php
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
    //...
    
    /**
     * @ORM\Column(type="string")
     */
    protected $password;

    protected $plainPassword;

    // ... tous les getters et setters
}

```

L'attribut `plainPassword` ne sera pas sauvegardé en base. Il nous permettra de
conserver le mot de passe de l'utilisateur en clair à sa création ou modification.

Comme toujours, n'oubliez pas de mettre à jour la base de données :
```bash
php bin/console doctrine:schema:update --dump-sql --force

ALTER TABLE users ADD password VARCHAR(255) NOT NULL;

Updating database schema...
Database schema updated successfully! "1" query was executed
```

La création d'un utilisateur nécessite maintenant un léger travail supplémentaire. À la création, il faudra fournir un mot de passe en claire que nous hasherons avant de le sauvegarder en base.
Rajoutons donc les configurations de sécurité de Symfony :
```yaml
# To get started with security, check out the documentation:
# http://symfony.com/doc/current/book/security.html
security:

    # Ajout d'un encoder pour notre entité USer
    encoders:
        AppBundle\Entity\User:
            algorithm: bcrypt
            cost: 12
```
Notre entité utilisateur doit implémenter l'interface `UserInterface` :
```php
# src/AppBundle/Entity/User.php
<?php
namespace AppBundle\Entity;

use Symfony\Component\Security\Core\User\UserInterface;
use Doctrine\ORM\Mapping as ORM;
use Doctrine\Common\Collections\ArrayCollection;

/**
* @ORM\Entity()
* @ORM\Table(name="users",
*      uniqueConstraints={@ORM\UniqueConstraint(name="users_email_unique",columns={"email"})}
* )
*/
class User implements UserInterface
{
    // ...

    public function getPassword()
    {
        return $this->password;
    }
     
    public function setPassword($password)
    {
        $this->password = $password;
    }


    public function getRoles()
    {
        return [];
    }

    public function getSalt()
    {
        return null;
    }

    public function getUsername()
    {
        return $this->email;
    }

    public function eraseCredentials()
    {
        // Suppression des données sensibles
        $this->plainPassword = null;
    }
}
```

Le formulaire de création d'utilisateur et l'action associée dans notre contrôleur vont être adaptés en conséquence :
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
        $builder->add('plainPassword'); // Rajout du mot de passe 
        $builder->add('email', EmailType::class);
    }

    // ...
}
```

Pour le mot de passe, nous aurons juste quelques règles de validation basiques :
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
        plainPassword:
            - NotBlank: { groups: [New, FullUpdate] }
            - Type: string
            - Length:
                min: 4
                max: 50
# ...
```

Le champ `plainPassword` est un champ un peu spécial. Les groupes nous permettrons d'activer sa contrainte `NotBlank` lorsque le client voudra créer ou mettre à jour tous les champs de l'utilisateur. Mais lors d'une mise à jour partielle (PATCH), si le champ est nul, il sera tout simplement ignoré.

[[erreur]]
|Le mot de passe ne doit en aucun cas être sérialisé. Il ne doit pas être associé à un groupe de sérialisation.

La création et la modification d'un utilisateur nécessite maintenant un hashage du mot de passe en clair, le service `password_encoder` de Symfony fait ce travail pour nous en utilisant toutes les configurations que nous venons de mettre en place.

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
    /**
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"user"})
     * @Rest\Post("/users")
     */
    public function postUsersAction(Request $request)
    {
        $user = new User();
        $form = $this->createForm(UserType::class, $user, ['validation_groups'=>['Default', 'New']]);

        $form->submit($request->request->all());

        if ($form->isValid()) {
            $encoder = $this->get('security.password_encoder');
            // le mot de passe en claire est encodé avant la sauvegarde
            $encoded = $encoder->encodePassword($user, $user->getPlainPassword());
            $user->setPassword($encoded);

            $em = $this->get('doctrine.orm.entity_manager');
            $em->persist($user);
            $em->flush();
            return $user;
        } else {
            return $form;
        }
    }

     /**
     * @Rest\View(serializerGroups={"user"})
     * @Rest\Put("/users/{id}")
     */
    public function updateUserAction(Request $request)
    {
        return $this->updateUser($request, true);
    }

    /**
     * @Rest\View(serializerGroups={"user"})
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
            return $this->userNotFound();
        }

        if ($clearMissing) { // Si une mise à jour complète, le mot de passe doit être validé
            $options = ['validation_groups'=>['Default', 'FullUpdate']];
        } else {
            $options = []; // Le groupe de validation par défaut de Symfony est Default
        }

        $form = $this->createForm(UserType::class, $user, $options);

        $form->submit($request->request->all(), $clearMissing);

        if ($form->isValid()) {
            // Si l'utilisateur veut changer son mot de passe
            if (!empty($user->getPlainPassword())) {
                $encoder = $this->get('security.password_encoder');
                $encoded = $encoder->encodePassword($user, $user->getPlainPassword());
                $user->setPassword($encoded);
            }
            $em = $this->get('doctrine.orm.entity_manager');
            $em->merge($user);
            $em->flush();
            return $user;
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

Le groupe de validation `Default` regroupe toutes les contraintes de validation qui ne sont dans aucun groupe. Il est créé automatiquement par Symfony. N'hésitez surtout pas à consulter [la documentation](http://symfony.com/doc/current/book/validation.html#validation-groups) pour des informations plus détaillées avant de continuer.

Nous pouvons maintenant tester la création d'un utilisateur en fournissant un mot de passe.

![Requête de création d'un utilisateur avec mot de passe](http://zestedesavoir.com/media/galleries/3183/024a94ff-cfe9-43d0-87f2-a51e9b0298c9.png)

L'utilisateur est créé et la réponse ne contient aucun mot de passe :

```json
{
  "id": 5,
  "firstname": "test",
  "lastname": "Pass",
  "email": "test@pass.fr",
  // ...
}
```

[[attention]]
|Toutes les modifications effectuées ici sont propres à Symfony. Si vous avez du mal à suivre, il est vivement (grandement) conseillé de consulter [la documentation officielle](http://symfony.com/doc/current/book/security.html) du framework.