
Qui dit système d'authentification dit des opérations comme *se connecter* et *se déconnecter*.

[[question]]
|Comment mettre en place un tel système en se basant sur des concepts REST ?

Pour bien adapter ses opérations, il faut d'abord bien les comprendre.

En général, lorsque nous nous connectons à un site web, nous fournissons un login et un mot de passe via un formulaire de connexion. Si les informations fournies sont valides, le serveur *crée* _un cookie_ qui permettra d'assurer la gestion de la session. Une fois que nous avons fini de naviguer sur le site, il suffit de nous déconnecter pour que _le cookie_ de session soit *supprimé*.

![Cycle d'authentification](http://zestedesavoir.com/media/galleries/3183/4caa0a4f-a12c-44e5-9ec0-2bb6fef9f341.png)

Nous avons donc 3 éléments essentiels pour un tel fonctionnement :

- une méthode pour se connecter ;
- une méthode pour se déconnecter ;
- et une entité pour suivre l'utilisateur pendant sa navigation (le cookie).

En REST toutes nos opérations doivent se faire sur des *ressources*.

Pour rappel, 

> Du moment où vous devez interagir avec une entité de votre application, créer une entité, la modifier, la consulter ou que vous devez l'identifier de manière unique alors vous avez pour la plupart des cas une ressource.

Les opérations se font sur le cookie, nous pouvons donc dire qu'il représente notre ressource. Pour le cas d'un site web, l'utilisation d'un cookie est pratique vue que les navigateurs le gèrent nativement (envoie à chaque requête, limitation à un seul domaine pour la sécurité, durée de validité, etc.).

Pour le cas d'une API, il est certes possible d'utiliser un cookie mais il existe une solution équivalente mais plus simple et plus courante: les tokens.

[[information]]
| Donc *se connecter* ou encore *se déconnecter* se traduisent respectivement par créer un token d'authentification et supprimer son token d'authentification. 
 Pour chaque requête, le token ainsi crée est rajouté en utilisant une entête HTTP comme pour les cookies.

Commençons d'abord par gérer la création des tokens.