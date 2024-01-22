
La dernière contrainte du REST que nous n'avons pas encore implémentée est 
l'hypermédia en tant que moteur de l'état de l'application *HATEOAS*. 
Pour rappel, le contrôle hypermédia désigne l'état d'une application ou API avec un seul point d'entrée mais qui propose des éléments permettant de l'explorer et d'interagir avec elle.

Avec un humain qui surfe sur le web, il est facile de suivre cette contrainte. En général, nous utilisons tous des sites web en tapant sur notre navigateur l'URL de la page d'accueil. Ensuite, avec les différents liens et formulaires, nous interagissons avec ledit site. Un site web est l'exemple parfait du concept HATEAOS.

Pour une API, nous avons des outils comme [*BazingaHateoasBundle*](https://github.com/willdurand/BazingaHateoasBundle) qui permettent d'avoir un *semblant* de HATEOS.

Une fois configuré, voici un exemple de réponse lorsqu'on récupère un utilisateur (exemple issu de [la documentation du bundle](https://github.com/willdurand/Hateoas#introduction)).

```json
{
    "id": 42,
    "first_name": "Adrien",
    "last_name": "Brault",
    "_links": {
        "self": {
            "href": "/api/users/42"
        },
        "manager": {
            "href": "/api/users/23"
        }
    },
    "_embedded": {
        "manager": {
            "id": 23,
            "first_name": "Will",
            "last_name": "Durand",
            "_links": {
                "self": {
                    "href": "/api/users/23"
                }
            }
        }
    }
}
```

Les attributs `_links` et `_embedded` sont issus des spécifications [Hypertext Application Language (HAL)](https://tools.ietf.org/html/draft-kelly-json-hal-07). Ils permettent de décrire notre ressource en suivant les spécifications HAL encore à l'état de brouillon.

Des initiatives identiques comme [JSON for Linking Data (json-ld)](http://json-ld.org/) tentent de traiter le problème mais se heurtent tous face à un même obstacle.

La contrainte HATEOAS de REST nécessite un client très intelligent qui puisse :

- comprendre les relations déclarées entre ressource ;
- auto-découvrir notre API à partir d'une seule URL.

Malheureusement, il n'existe pas encore de client d'API en mesure de comprendre et d'exploiter une API RESTFul niveau 3 (selon le modèle de Richardson).

Nous n'implémenterons donc pas cette contrainte et c'est le cas pour beaucoup d'API REST. Dans les faits, cela ne pose aucun problème et notre API est pleinement fonctionnelle.