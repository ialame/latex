*Swagger UI* est un logiciel basé sur les technologies du web (HTML, Javascript, CSS) permettant de générer une documentation en utilisant les spécifications d'*OpenAPI*. Il fournit aussi un bac à sable permettant de tester les appels API directement depuis la documentation générée.

# Installation de Swagger UI

Pour installer Swagger UI, il suffit de [le télécharger depuis GitHub](https://github.com/swagger-api/swagger-ui/tree/v2.1.4). Ensuite, nous allons le décompresser dans un dossier nommé ***swagger-ui*** dans le répertoire ***web***. Nous utiliserons la version v2.1.4.

[[information]]
| Si vous utilisez *git*, il suffit de se placer dans le dossier ***web*** et de lancer :
|```bash
|git clone https://github.com/swagger-api/swagger-ui.git
|git checkout v2.1.4
|```

![Arborescence après l'installation de Swagger UI](https://zestedesavoir.com/media/galleries/3183/329a64d9-b260-417f-a17d-b371f1093b0d.png)

Si l'installation s'est bien déroulée, en accédant à l'URL [http://rest-api.local/swagger-ui/dist/index.html](http://rest-api.local/swagger-ui/dist/index.html), la page d'accueil de Swagger UI s'affiche.

![Page d'accueil de Swagger UI](https://zestedesavoir.com/media/galleries/3183/76d8eba8-3e82-499d-b829-0ce4a36941e7.png)

# Utiliser notre documentation

Depuis l'interface de Swagger Editor, il est possible d'exporter notre documentation au format JSON. Le fichier ***swagger.json*** ainsi obtenu ressemble à :

```json
{
    "swagger": "2.0",
    "info": {
        "title": "Proposition de suggestions API",
        "description": "Proposer des idées de sortie à des utilisateurs en utilisant leurs préférences",
        "version": "1.0.0"
    },
    "host": "rest-api.local",
    "schemes": [
        "http"
    ],
    "produces": [
        "application/json",
        "application/xml"
    ],
    "consumes": [
        "application/json",
        "application/xml"
    ],
    "paths": {
        // ...
    },
    "parameters": {
         // ...
    },
    "definitions": {
         // ...
    }
}
```

Pour utiliser ce fichier ***swagger.json***, il faut commencer par l'enregistrer dans le dossier ***web***. Le fichier doit être disponible depuis un navigateur.
Ensuite, il faut éditer le fichier ***web/swagger-ui/dist/indext.html*** et éditer les lignes 34 à 39.

```javascript
 /*var url = window.location.search.match(/url=([^&]+)/);
  if (url && url.length > 1) {
    url = decodeURIComponent(url[1]);
  } else {
    url = "http://petstore.swagger.io/v2/swagger.json";
  }*/
var url ="/swagger.json";
```

En consultant l'URL, nous pouvons maintenant voir notre documentation et même tester les appels API depuis celui-ci.

![Documentation de notre API avec Swagger UI](https://zestedesavoir.com/media/galleries/3183/21c68674-1e9f-48aa-84a9-e5193939e977.png)