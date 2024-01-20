
# Origine de REST

REST (Representational State Transfer) est un style d'architecture pour les systèmes hypermédia distribués, créé par un dénommé Roy Thomas Fielding en 2000 et décrit dans le chapitre 5 de sa thèse de doctorat intitulée [Architectural Styles and the Design of Network-based Software Architectures](http://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm) (Les styles d'architecture et la conception de l'architecture des applications réseaux).

[Roy Fielding](https://fr.wikipedia.org/wiki/Roy_Fielding) est un ingénieur en informatique qui a notamment travaillé sur les spécifications du protocole HTTP Hypertext Transfert Protocol. Il est aussi connu comme l'un des membres fondateur de la fondation Apache.

# Présentation de REST

## Principes REST

Le style d'architecture REST représente un ensemble de contraintes qui régissent une application réseau. Chacune de ces contraintes décrit un concept qu'une application qui se veut RESTful doit implémenter.
[[information]]
|Le terme RESTful (anglicisme) est un adjectif qui qualifie une application qui suit les principes REST.

### Client-Serveur (Client-Server)

La première contrainte est la séparation des responsabilités entre le client et le serveur. Le serveur s'occupe de la gestion des règles métier et des données et le client se concentre sur l'interface utilisateur (une interface peut être une page web, une application mobile etc.).
En séparant le client et le serveur, la portabilité et la scalabilité de notre application sont grandement améliorées. Chaque composant pourra aussi évoluer séparément.
Nous pouvons imaginer un site web qui refait toute sa charte graphique sans que le code côté serveur ne soit modifié.

### Sans état (Stateless)

Une autre contrainte est la notion de "Sans état" ou Stateless en anglais.
La communication entre le client et le serveur doit se faire sans dépendre d'un contexte lié au serveur. Chaque requête du client contient toutes les informations nécessaires à son traitement.
Ainsi, plusieurs instances de serveurs peuvent traiter indifféremment les requêtes de chaque client.

### Cache

Afin d'améliorer les performances de l'application, le serveur doit ajouter *une étiquette de cache* à toutes les réponses.
Cette étiquette décrit les possibilités de mise en cache ou non des données renvoyées par le serveur.

### Interface uniforme (Uniform Interface)

Une des fonctionnalités clés qui permet de distinguer une architecture REST est la mise en valeur d'une interface uniforme entre les différents composants.

REST repose sur 4 contraintes d'interface :

- l'identification de manière unique des ressources; 
- l'interaction avec les ressources via des représentations, chaque ressource disposant de sa présentation; 
- les messages auto-descriptifs, une réponse ou une requête contient toutes les informations permettant de décrire la nature des données qu'elle contient et les interactions possibles;
- et, l'hypermédia en tant que moteur de l'état de l'application *HATEOAS*. L'état de l'application, les différentes interactions possibles entre client et le serveur doivent être décrites à travers les liens hypermédia dans les réponses du serveur.

Le terme lien hypermédia englobe des formulaires, les liens hypertextes ou plus généralement tout support numérique permettant une interaction.

En définissant une interface uniformisée, les différentes interactions avec le serveur sont facilement identifiables.


### Organisation en couches (Layered System)

Les couches dans une application consistent en l'isolation des différents composants de l'application pour bien organiser leurs responsabilités.
Chaque couche représente alors un système borné qui traite une problématique spécifique de notre application.
Nous pouvons prendre comme exemple une couche dédiée au stockage des données mais qui n'a pas conscience de leur origine. Son unique rôle consiste à stocker des informations qui lui sont passées.

### Code à la demande (Code-On-Demand)

Cette contrainte **optionnelle** permet l'extension des fonctionnalités du client en fournissant du code téléchargeable et exécutable.
Cela nécessite quand même une certaine connaissance des clients qui exploitent l'application REST.
Par exemple, une API pourrait fournir du code JavaScript que tous les clients web peuvent télécharger et exécuter pour effectuer des tâches complexes.
Cela permet de faire évoluer un client sans avoir à le redéployer vu que le code exécuté vient du serveur. Il suffira juste de mettre à jour le serveur et le tour est joué.

## Un peu de vocabulaire autour de REST

Ce style d'architecture introduit et utilise par la même occasion quelques notions qu'il faut impérativement comprendre.

### Ressources et identifiants

Une interface REST gravite autour de ressources. À partir du moment où vous devez interagir avec une entité de votre application, créer une entité, la modifier, la consulter ou encore l'identifier de manière unique, vous avez pour la plupart des cas une ressource.
Si par exemple, nous développons une application de commerce en ligne, un article disponible à la vente est une ressource. Une image décrivant cet article peut être une ressource. 
Pour référencer de manière unique cette ressource, REST utilise un identifiant. Cet identifiant sera alors utilisé par *tous* les différents composants de notre application afin d'interagir avec cette ressource.
Notre article pourra ainsi avoir un numéro unique que les composants du panier, de paiement ou autres utiliseront pour désigner cet article.

### Représentation d'une ressource

Une représentation désigne toutes les informations (données et métadonnées) utiles qui décrivent une ressource.

Notre article pourra donc être représenté par une page HTML (Hypertext Markup langage) contenant le nom de l'article, son prix, sa disponibilité etc. Et notre image décrivant un article, sa représentation désignera simplement les données en base64 et les métadonnées qui décrivent l'encodage utilisée pour l'image, le type de compression, etc.

[[information]]
|En résumé, REST est un style d'architecture défini par un ensemble de contraintes qui régissent l'organisation d'une application et les procédés de communication entre un fournisseur de services (le serveur) et le consommateur (le client).

