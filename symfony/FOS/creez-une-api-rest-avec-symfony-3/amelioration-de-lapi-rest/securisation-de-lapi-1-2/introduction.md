Jusque-là, les actions disponibles dans notre API sont accessibles pour n'importe quel client. Nous ne disposons d'aucun moyen pour gérer l'identité de ces derniers.

Pour être bref, n'importe qui peut faire n'importe quoi avec notre API.

[[information]]
| La sécurité n'est pas un sujet adressé par les concepts REST mais nous pouvons adapter les méthodes d'autorisation et d'authentification classiques aux principes REST.

Il existe beaucoup de techniques et d'outils comme [OAuth](http://oauth.net/) ou [JSON Web Tokens](https://jwt.io/) permettant de mettre en place un système d'authentification.

Cependant nous ne nous baserons sur aucun de ces outils et nous allons mettre en place un système d'authentification totalement personnalisé.