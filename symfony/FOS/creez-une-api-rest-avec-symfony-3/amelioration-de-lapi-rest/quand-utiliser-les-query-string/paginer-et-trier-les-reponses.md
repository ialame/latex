# Paginer la liste de lieux

Commençons par mettre en place une pagination pour la liste des lieux. Pour obtenir cette pagination, nous allons utiliser un principe simple.

Deux query strings vont permettre de choisir l'index du premier résultat souhaité (`offset`) et le nombre de résultats souhaités (`limit`).

Ces deux paramètres sont facultatifs mais doivent obligatoirement être des entiers positifs.

Pour implémenter ce fonctionnement, il suffit de rajouter deux annotations `QueryParam` dans l'action qui liste les lieux.
```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...
use FOS\RestBundle\Controller\Annotations\QueryParam;
// ...

class PlaceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     * @QueryParam(name="offset", requirements="\d+", default="", description="Index de début de la pagination")
     * @QueryParam(name="limit", requirements="\d+", default="", description="Index de fin de la pagination")
     */
    public function getPlacesAction(Request $request)
    {
        $places = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->findAll();
        /* @var $places Place[] */

        return $places;
    }
// ...
}
```

Avec l'attribut `requirements`, nous utilisons une expression régulière pour valider les paramètres. Si les données ne sont pas valides alors le paramètre vaudra sa valeur par défaut (une chaîne vide pour notre cas). Si les paramètres ne sont pas renseignés, ils seront aussi vides.
Il faut aussi noter que nous pouvons utiliser n'importe quelle valeur par défaut. En effet, elle n'est pas validée par FOSRestBundle. C'est d'ailleurs pour cette raison que nous pouvons mettre dans notre exemple une chaîne vide comme valeur par défaut alors que notre expression régulière ne valide que les entiers.

[[erreur]]
| L'expression régulière utilisée dans `requirements` est traité en rajoutant automatiquement un pattern du type `#^notre_regex$#xsu`. En mettant, `\d+` nous validons donc avec `#^\d+$#xsu`.  Vous pouvez consulter [la documentation de PHP](http://php.net/manual/fr/reference.pcre.pattern.modifiers.php) pour voir l'utilité des options `x` (ignorer les caractères d'espacement), `s` (pour utiliser `.` comme métacaractère générique) et `u` (le masque et la chaîne d'entrée sont traitées comme des chaînes UTF-8.).

Pour les traiter, nous avons plusieurs choix. Nous pouvons utiliser un attribut de l'objet *Request* appelé `paramFetcher` que le *Param Fetcher Listener* crée automatiquement. Ou encore, nous pouvons ajouter un paramètre à notre action qui doit être du type `FOS\RestBundle\Request\ParamFetcher` .

Avec la cette dernière méthode, que nous allons utiliser, le *Param Fetcher Listener* injecte automatiquement le param fetcher à notre place.

L'objet ainsi obtenu permet d'accéder aux différents query strings que nous avons déclarés.

```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...
use FOS\RestBundle\Controller\Annotations\QueryParam;
use FOS\RestBundle\Request\ParamFetcher;
// ...

class PlaceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     * @QueryParam(name="offset", requirements="\d+", default="", description="Index de début de la pagination")
     * @QueryParam(name="limit", requirements="\d+", default="", description="Nombre d'éléments à afficher")
     */
    public function getPlacesAction(Request $request, ParamFetcher $paramFetcher)
    {
        $offset = $paramFetcher->get('offset');
        $limit = $paramFetcher->get('limit');

        $places = $this->get('doctrine.orm.entity_manager')
                ->getRepository('AppBundle:Place')
                ->findAll();
        /* @var $places Place[] */

        return $places;
    }
// ...
}
```
Avec le param fetcher, nous pouvons récupérer nos paramètres et les traiter à notre convenance. 
Pour gérer la pagination avec Doctrine, nous pouvons utiliser le query builder avec les paramètres `offset` et `limit`.
```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...
use FOS\RestBundle\Controller\Annotations\QueryParam;
use FOS\RestBundle\Request\ParamFetcher;
// ...

class PlaceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     * @QueryParam(name="offset", requirements="\d+", default="", description="Index de début de la pagination")
     * @QueryParam(name="limit", requirements="\d+", default="", description="Nombre d'éléments à afficher")
     */
    public function getPlacesAction(Request $request, ParamFetcher $paramFetcher)
    {
        $offset = $paramFetcher->get('offset');
        $limit = $paramFetcher->get('limit');

        $qb = $this->get('doctrine.orm.entity_manager')->createQueryBuilder();
        $qb->select('p')
           ->from('AppBundle:Place', 'p');

        if ($offset != "") {
            $qb->setFirstResult($offset);
        }

        if ($limit != "") {
            $qb->setMaxResults($limit);
        }
    
        $places = $qb->getQuery()->getResult();

        return $places;
    }
// ...
}
```

Nous pouvons maintenant tester plusieurs appels API :

- `GET rest-api.local/places?limit=5` permet de lister cinq lieux ;
- `GET rest-api.local/places?offset=3` permet de lister tous les lieux en omettant les trois premiers lieux ;
- `GET rest-api.local/places?offset=1&limit=2` permet de lister deux lieux en omettant le premier lieu dans l'application.

En testant le dernier exemple avec Postman, nous avons :

![Récupération des lieux avec une pagination](http://zestedesavoir.com/media/galleries/3183/7d9c831d-e3e7-41cc-a94a-ba606920a065.png)

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
  },
  {
    "id": 4,
    "name": "Disneyland Paris",
    "address": "77777 Marne-la-Vallée",
    "prices": [],
    "themes": []
  }
]
```

# Trier la liste des lieux

Pour pratiquer, nous allons rajouter un paramètre pour trier les lieux selon leur nom.

Le paramètre s'appellera `sort` et pourra avoir deux valeurs: *asc* pour l'ordre croissant et *desc* pour l'ordre décroissant.
La valeur par défaut sera `null`.

```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...
use FOS\RestBundle\Controller\Annotations\QueryParam;
// ...

class PlaceController extends Controller
{

    /**
     * @Rest\View(serializerGroups={"place"})
     * @Rest\Get("/places")
     * @QueryParam(name="offset", requirements="\d+", default="", description="Index de début de la pagination")
     * @QueryParam(name="limit", requirements="\d+", default="", description="Nombre d'éléments à afficher")
     * @QueryParam(name="sort", requirements="(asc|desc)", nullable=true, description="Ordre de tri (basé sur le nom)")
     */
    public function getPlacesAction(Request $request, ParamFetcher $paramFetcher)
    {
        $offset = $paramFetcher->get('offset');
        $limit = $paramFetcher->get('limit');
        $sort = $paramFetcher->get('sort');

        $qb = $this->get('doctrine.orm.entity_manager')->createQueryBuilder();
        $qb->select('p')
           ->from('AppBundle:Place', 'p');

        if ($offset != "") {
            $qb->setFirstResult($offset);
        }

        if ($limit != "") {
            $qb->setMaxResults($limit);
        }

        if (in_array($sort, ['asc', 'desc'])) {
            $qb->orderBy('p.name', $sort);
        }
       
    
        $places = $qb->getQuery()->getResult();

        return $places;
    }
// ...
}
```

La seule différence avec les deux autres query strings est que pour avoir une valeur par défaut à `null`, nous utilisons l'attribut `nullable`.


En testant l'appel précédant avec en plus un tri des noms par ordre décroissant :

![Récupération des lieux avec une pagination et un tri par ordre décroissant de nom](http://zestedesavoir.com/media/galleries/3183/5ce13b47-e433-4ae5-9c6b-811b7b3d56cf.png)

La réponse change en :
```json
[
  {
    "id": 6,
    "name": "test",
    "address": "test",
    "prices": [],
    "themes": []
  },
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
]
```


Il est aussi possible de configuer *FOSRestBundle* pour injecter directement les query strings dans l'objet `Request`. Pour plus d'informations,vous pouvez consulter [la documentation du bundle](http://symfony.com/doc/current/bundles/FOSRestBundle/param_fetcher_listener.html).