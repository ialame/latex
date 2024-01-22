Revenons maintenant à notre système d'authentification avec des tokens.
Un token aura les caractéristiques suivantes :

- une valeur : une suite de chaînes de caractères générées aléatoirement et *unique* ; 
- une date de création : la date à la quelle le token a été créé. Cette date nous permettra plus tard de vérifier l'âge du token et donc sa validité du token ;
- un utilisateur : une référence vers l'utilisateur qui a demandé la création de ce token. 
Comme pour toute ressource, nous avons besoin d'une URL pour l'identifier. Nous utiliserons `rest-api.local/auth-tokens`, `auth-tokens` étant juste le dimunitif de *authentication tokens* i.e les tokens d'authentification.

Contrairement aux autres ressources, la requête de création d'un token est légérement différente. Le payload contiendra le login et le mot de passe de l'utilisateur et les informations qui décrivent le token seront générées par le serveur.

La réponse contiendra donc les informations ainsi créées.

![Cinématique de création de token](http://zestedesavoir.com/media/galleries/3183/56ec754f-6588-48bf-9654-2e4b99810135.png)

L'implémentation va donc ressembler à tout ce que nous avons déjà fait.

Commençons par l'entité `AuthToken` :
```php
# src/AppBundle/Entity/AuthToken.php
<?php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="auth_tokens",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="auth_tokens_value_unique", columns={"value"})}
 * )
 */
class AuthToken
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
    protected $value;
    
    /**
     * @ORM\Column(type="datetime")
     * @var \DateTime
     */
    protected $createdAt;

    /**
     * @ORM\ManyToOne(targetEntity="User")
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
     
    public function getValue()
    {
        return $this->value;
    }
     
    public function setValue($value)
    {
        $this->value = $value;
    }
     
    public function getCreatedAt()
    {
        return $this->createdAt;
    }
     
    public function setCreatedAt(\DateTime $createdAt)
    {
        $this->createdAt = $createdAt;
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

La mise à jour de la base de données avec Doctrine :
```bash
php bin/console doctrine:schema:update --dump-sql --force
#> CREATE TABLE auth_tokens (id INT AUTO_INCREMENT NOT NULL, user_id INT DEFAULT NULL, value VARCHAR(255) NOT NULL, created_at DATETIME NOT NULL, INDEX IDX_8AF9B66CA76ED395 (user_id), UNIQUE INDEX auth_tokens_value_unique (value), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ENGINE = InnoDB;
#> ALTER TABLE auth_tokens ADD CONSTRAINT FK_8AF9B66CA76ED395 FOREIGN KEY (user_id) REFERENCES users (id);

#> Updating database schema...
#> Database schema updated successfully! "2" queries were executed
```

Pour la gestion du login et du mot de passe de l'utilisateur, nous allons créer :

- une entité nommée ***Credentials*** avec deux attributs : `login` et `password`. Cette entité n'aura aucune annotation Doctrine, elle pemettra juste de transporter ces informations ;
- un formulaire nommé ***CredentialsType*** pour valider que les champs de l'entité `Credentials` ne sont pas vides (`Not-Blank`).

L'entité ressemble donc à :
```php
# src/AppBundle/Entity/Credentials.php
<?php
namespace AppBundle\Entity;

class Credentials
{
    protected $login;

    protected $password;
 
    public function getLogin()
    {
        return $this->login;
    }
     
    public function setLogin($login)
    {
        $this->login = $login;
    }
     
    public function getPassword()
    {
        return $this->password;
    }
     
    public function setPassword($password)
    {
        $this->password = $password;
    }
}
```

Le formulaire et les règles de validation associées :
```php
# src/AppBundle/Form/Type/CredentialsType.php
<?php
namespace AppBundle\Form\Type;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class CredentialsType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder->add('login');
        $builder->add('password');
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => 'AppBundle\Entity\Credentials',
            'csrf_protection' => false
        ]);
    }
}

```

```yaml
# src/AppBundle/Resources/config/validation.yml

# ...

AppBundle\Entity\Credentials:
    properties:
        login:
            - NotBlank: ~
            - Type: string
        password:
            - NotBlank: ~
            - Type: string
```


Il ne faut pas oublier de configurer le sérialiseur pour afficher le token en utilisant un groupe prédéfini.
```yaml
# src/AppBundle/Resources/config/serialization.yml
# ...

AppBundle\Entity\User:
    attributes:
        id:
            groups: ['user', 'preference', 'auth-token']
        firstname:
            groups: ['user', 'preference', 'auth-token']
        lastname:
            groups: ['user', 'preference', 'auth-token']
        email:
            groups: ['user', 'preference', 'auth-token']
        preferences:
            groups: ['user']

AppBundle\Entity\AuthToken:
    attributes:
        id:
            groups: ['auth-token']
        value:
            groups: ['auth-token']
        createdAt:
            groups: ['auth-token']
        user:
            groups: ['auth-token']

```


Maintenant, il ne reste plus qu'à créer le contrôleur qui assure la gestion des tokens d'authentification.
```php
# src/AppBundle/Controller/AuthTokenController.php
<?php
namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations as Rest; // alias pour toutes les annotations
use AppBundle\Form\Type\CredentialsType;
use AppBundle\Entity\AuthToken;
use AppBundle\Entity\Credentials;

class AuthTokenController extends Controller
{
    /**
     * @Rest\View(statusCode=Response::HTTP_CREATED, serializerGroups={"auth-token"})
     * @Rest\Post("/auth-tokens")
     */
    public function postAuthTokensAction(Request $request)
    {
        $credentials = new Credentials();
        $form = $this->createForm(CredentialsType::class, $credentials);

        $form->submit($request->request->all());

        if (!$form->isValid()) {
            return $form;
        }

        $em = $this->get('doctrine.orm.entity_manager');

        $user = $em->getRepository('AppBundle:User')
            ->findOneByEmail($credentials->getLogin());

        if (!$user) { // L'utilisateur n'existe pas
            return $this->invalidCredentials();
        }

        $encoder = $this->get('security.password_encoder');
        $isPasswordValid = $encoder->isPasswordValid($user, $credentials->getPassword());
        
        if (!$isPasswordValid) { // Le mot de passe n'est pas correct
            return $this->invalidCredentials();
        }

        $authToken = new AuthToken();
        $authToken->setValue(base64_encode(random_bytes(50)));
        $authToken->setCreatedAt(new \DateTime('now'));
        $authToken->setUser($user);

        $em->persist($authToken);
        $em->flush();

        return $authToken;
    }

    private function invalidCredentials()
    {
        return \FOS\RestBundle\View\View::create(['message' => 'Invalid credentials'], Response::HTTP_BAD_REQUEST);
    }
}

```

N'oublions pas de déclarer le nouveau contrôleur.
```yaml
# app/config/routing.yml
# ...

auth-tokens:
    type:     rest
    resource: AppBundle\Controller\AuthTokenController

```

[[information]]
|Pour des raisons de sécurité, nous évitons de donner des  détails sur les comptes existants, un même message - `Invalid Credentials` - est renvoyé lorsque le login n'existe pas ou lorsque le mot de passe n'est pas correct.

Nous pouvons maintenant créer un token en utilisant le compte `test@pass.fr` créé plus tôt.

```json
{
    "login": "test@pass.fr",
    "password": "test"
}
```

![Requête de création d'un token d'authentification avec Postman](http://zestedesavoir.com/media/galleries/3183/655ceeba-e0cc-4155-8624-aa9e5b82b86a.png)

La réponse contient un token que nous pourrons exploiter plus tard pour décliner notre identité.
```json
{
  "id": 3,
  "value": "MVgq3dT8QyWv3t+s7DLyvsquVbu+mOSPMdYX7VUQOEQcJGwaGD8ETa+zi9ReHPWYFKI=",
  "createdAt": "2016-04-08T17:49:00+00:00",
  "user": {
    "id": 5,
    "firstname": "test",
    "lastname": "Pass",
    "email": "test@pass.fr"
  }
}
```