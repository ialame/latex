
Pour en finir avec la partie sécurisation, il ne reste plus qu'à rajouter une méthode pour se déconnecter de notre API.
La déconnexion consiste juste à la suppression du token d'authentification.
Par contre, une petite précaution va s'imposer. Pour traiter la suppression d'un token, il faudra juste nous assurer que l'utilisateur veut supprimer son propre token et pas celui d'un autre.
À part cette modification, tous les autres mécanismes déjà vus rentrent en jeu.

Pour supprimer la ressource, il faudra donc une requête `DELETE` sur la ressource `rest-api.local/auth-tokens/{id}`. La réponse en cas de succès doit être vide avec comme code de statut: `204 No Content`.

```php
# src/AppBunle/Controller/AuthTokenController.php
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
    //...

    /**
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT)
     * @Rest\Delete("/auth-tokens/{id}")
     */
    public function removeAuthTokenAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $authToken = $em->getRepository('AppBundle:AuthToken')
                    ->find($request->get('id'));
        /* @var $authToken AuthToken */

        $connectedUser = $this->get('security.token_storage')->getToken()->getUser();

        if ($authToken && $authToken->getUser()->getId() === $connectedUser->getId()) {
            $em->remove($authToken);
            $em->flush();
        } else {
            throw new \Symfony\Component\HttpKernel\Exception\BadRequestHttpException();
        }
    }

    // ...
}
```

![Requête de suppression d'un token avec Postman](http://zestedesavoir.com/media/galleries/3183/923f1b55-33bd-4aec-b0fc-bbd57b87bd5c.png)

Si tout se passe bien, la réponse lors d'une suppression est vide avec comme statut `204`. En cas d'erreur une réponse `400` est renvoyée au client.

```json
{
  "code": 400,
  "message": "Bad Request"
}
```