Dans chacune des routes générées, nous avons un attribut `_format` qui apparaît. *FOSRestBundle* introduit automatiquement ce paramètre afin de gérer le format des réponses. Vu que pour notre cas nous forçons toujours une réponse JSON, les URL [rest-api.local/places](http://rest-api.local/places), [rest-api.local/places.json](http://rest-api.local/places.json), [rest-api.local/places.nimportequoi](rest-api.local/places.nimportequoi) correspondent toutes à la même route et renvoient du JSON.

Pour gérer plusieurs formats de réponse, HTTP propose une solution plus élégante avec l'entête `Accept` que nous aborderons plus tard. Nous allons donc désactiver l'ajout automatique de cet attribut en reconfigurant *FOSRestBundle*.

Il faut rajouter une entrée dans le fichier de configuration :
```yaml
# app/config/config.yml

# ...

fos_rest:
    routing_loader:
        include_format: false
```

Si nous relançons `php bin/console debug:config fos_rest`, le format n'est plus présent dans les routes :

- get_places GET /places
- get_place  GET /places/{id}

Pratiquons en redéfinissant les routes du contrôleur ***UserController*** avec les annotations de *FOSRestBundle*.

```php
# src/AppBunble/UserController.php
<?php
namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use FOS\RestBundle\Controller\Annotations\Get;
use AppBundle\Entity\User;

class UserController extends Controller
{
    /**
     * @Get("/users")
     */
    public function getUsersAction(Request $request)
    {
        // ...
    }

    /**
     * @Get("/users/{user_id}")
     */
    public function getUserAction(Request $request)
    {
        // ...
    }
}

```

Et n'oublions pas de déclarer dans notre fichier de routage :

```yaml
# app/config/routing.yml
app:
    resource: "@AppBundle/Controller/DefaultController.php"
    type:     annotation

places:
    type:     rest
    resource: AppBundle\Controller\PlaceController

users:
    type:     rest
    resource: AppBundle\Controller\UserController
```

Voyons maintenant les outils que ce bundle nous propose pour la gestion des vues.