
Le bundle *FOSRestBundle* met à notre disposition un ensemble de composants pour gérer différents aspects d'une API. Et vous vous en doutez donc, il existe un listener pour gérer de manière simple et efficace les exceptions.

À l'état actuel, les exceptions sont gérées par le listener du bundle *Twig*. La première configuration à effectuer est de le remplacer par l'exception listener de *FOSRestBundle*. Pour cela, il suffit de rajouter une ligne dans la configuration du bundle.

```yaml
# app/config/config.yml

# ...
fos_rest:
    routing_loader:
        include_format: false
    # ...
    exception:
        enabled: true

```

En activant ce composant, la gestion des exceptions avec *Twig* est automatiquement désactivée.
Rien qu'avec cette configuration, nous pouvons voir un changement dans la réponse lorsque l'entête `X-Auth-Token` n'est pas renseignée.

```json
{
  "code": 500,
  "message": "X-Auth-Token header is required"
}
```

![Code de statut de l'erreur interne](http://zestedesavoir.com/media/galleries/3183/4661e9d5-85b0-48fa-9844-dad0cf4b7198.png)

Lorsque le token renseigné n'est pas valide, nous obtenons comme réponse :
```json
{
  "code": 500,
  "message": "Invalid authentication token"
}
```

Les messages correspondent à ceux que nous avons défini dans les exceptions parce que l'application est en mode développement. En production, ces messages sont remplacés par `Internal Server Error`.
Pour s'en rendre compte, il suffit de lancer la même requête avec comme URL `rest-api.local/app.php/places` pour forcer la configuration en production.

```json
{
  "code": 500,
  "message": "Internal Server Error"
}
```

Il peut arriver que nous voulions conserver les messages des exceptions même en production. Pour ce faire, il suffit de rajouter dans la configuration un système d'autorisation des exceptions concernées.

```yaml
# app/config/config.yml

# ...
fos_rest:
    routing_loader:
        include_format: false
    # ...
    exception:
        enabled: true
        messages:
            'Symfony\Component\Security\Core\Exception\BadCredentialsException': true
```

Le tableau message contient comme clés les noms des exceptions à autoriser et la valeur vaut `true`.

En re-testant, la requête sur l'URL `rest-api.local/app.php/places` (n'oubliez pas de vider le cache avant de tester), le message est bien affiché :
```json
{
  "code": 500,
  "message": "Invalid authentication token"
}
```

Nous venons de franchir un premier pas.

Mais comme nous l'avons déjà vu, le code `500` ne doit être utilisé que pour les erreurs internes du serveur.
Pour le cas d'une authentification qui a échoué, le protocole HTTP propose un code bien spécifique - `401  Unauthorized` - qui dit qu'une authentification est nécessaire pour accéder à notre ressource.

Encore une fois, *FOSRestBundle* propose un système très simple. À l'instar du tableau `messages`, nous pouvons rajouter un tableau `codes` avec comme clés les exceptions et comme valeur les codes de statut associés.

Nous aurons donc comme configuration finale :

```yaml
# app/config/config.yml

# ...
fos_rest:
    routing_loader:
        include_format: false
    # ...
    exception:
        enabled: true
        messages:
            'Symfony\Component\Security\Core\Exception\BadCredentialsException': true
        codes:
            'Symfony\Component\Security\Core\Exception\BadCredentialsException': 401
```


Encore une fois ce bundle, nous facilite grandement le travail et réduit considérablement le temps de développement.

Lorsque nous re-exécutons notre requête :

![Requête Postman sans token d'autorisation](http://zestedesavoir.com/media/galleries/3183/1bd072af-cfe5-4bdb-9044-374fee5efb04.png)

La réponse contient le bon message et le code de statut est aussi correct :

![Code de statut de la réponse non autorisée](http://zestedesavoir.com/media/galleries/3183/5f64db92-7400-4f75-b8af-5891042b782a.png)

```json
{
  "code": 401,
  "message": "X-Auth-Token header is required"
}
```

[[information]]
|L'attribut code dans la réponse est créé par *FOSRestBundle* par soucis de clarté. La contrainte REST, elle, exige juste que le code HTTP de la réponse soit conforme.

Vu que le bundle est conçu pour interagir avec Symfony, toutes les exceptions du framework qui implémentent l'interface ``Symfony\Component\HttpKernel\Exception\HttpExceptionInterface` peuvent être traitées automatiquement.

Si par exemple, nous utilisons l'exception `NotFoundHttpException`, le code de statut devient automatiquement `404`. En général, il est aussi utile d'autoriser tous les messages des exceptions de type `HttpException` pour faciliter la gestion des cas d'erreurs.

La configuration du bundle devient maintenant :
```yaml
# app/config/config.yml
# ...

fos_rest:
    routing_loader:
        include_format: false
    # ...
    exception:
        enabled: true
        messages:
            'Symfony\Component\HttpKernel\Exception\HttpException' : true
            'Symfony\Component\Security\Core\Exception\BadCredentialsException': true
        codes:
            'Symfony\Component\Security\Core\Exception\BadCredentialsException': 401

```

Toutes les occurrences de `return \FOS\RestBundle\View\View::create(['message' => 'Place not found'], Response::HTTP_NOT_FOUND);` peuvent être remplacées par `throw new \Symfony\Component\HttpKernel\Exception\NotFoundHttpException('Place not found');`.

Et de même `return \FOS\RestBundle\View\View::create(['message' => 'User not found'], Response::HTTP_NOT_FOUND);` peut être remplacé par `throw new \Symfony\Component\HttpKernel\Exception\NotFoundHttpException('User not found');`.

Les réponses restent identiques mais les efforts fournis pour les produire sont réduits.

![Récupération d'un utilisateur inexistant avec Postman](http://zestedesavoir.com/media/galleries/3183/d241580b-6617-4146-aa82-59e8a2e17c39.png)

![Code de statut de la réponse](http://zestedesavoir.com/media/galleries/3183/691e124d-53d6-425c-bd22-a96ca3c6f483.png)