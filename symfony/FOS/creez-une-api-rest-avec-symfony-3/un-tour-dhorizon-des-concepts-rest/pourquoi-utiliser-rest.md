[[question]]
|Pourquoi utiliser REST plutôt d'une autre technologie ou architecture ?

Il existe plusieurs moyens permettant de communiquer entre des composants dans le cadre d'une architecture de type SOA (Service oriented Archictecture). On peut citer le protocole SOAP (Simple Object Access Protocol) ou encore XML-RPC.

Ces technologies sont largement utilisées surtout dans un cadre d'entreprise, mais avec l'essor du web, elles ont commencé à montrer leurs limites.

REST étant conçu pour répondre à ce besoin spécifique - le web - ce style d'architecture théorisé par Roy Fielding présente intrinsèquement beaucoup d'avantages pour ce cas d'usage.

Dans cette partie de ce cours, nous allons donc voir les facilités et l'intérêt que REST pourrait nous apporter dans le cadre du développement d'une API web.

# Pourquoi REST

## Les avantages de l'architecture REST

Comme les différentes règles et design pattern appliqués en génie logiciel, les différentes contraintes qu'impose l'architecture REST permettent d'obtenir des applications de meilleure qualité.

On peut citer entre autres :

- un couplage plus faible entre le client et le serveur comparé aux méthodes du type RPC Remote Procedure Call comme SOAP;
- une uniformisation des APIs (Application Programming Interface) pour une facilité d'utilisation;
- une plus grande tolérance à la panne;
- ou encore une application facilement portable et extensible.

## Popularisation

Bien que la publication de la thèse de Roy Fielding date des années 2000, un livre de Leonard Richardson et Sam Ruby *RESTful Web Services*, sorti le 22 mai 2007, a popularisé le style d’architecture REST en proposant une méthodologie pour l'implémenter en utilisant le protocole HTTP Hypertext Transfert Protocol.

Comme vous l'aurez déjà remarqué, plus nous avançons dans les principes REST, plus le modèle devient contraignant. Dès lors, une application peut suivre ces principes sans pour autant remplir toutes les contraintes du REST.

Ainsi, lors de la conférence [QCon du 20 novembre 2008](http://www.crummy.com/writing/speaking/2008-QCon/), Richardson a présenté un modèle qui permet d'évaluer son application selon les principes REST. Ce modèle est connu sous le nom de: Modèle de maturité de Richardson.

### Niveau 0 : RPC (Remote Procedure Call) via HTTP

Le protocole HTTP est utilisé pour appeler des méthodes du serveur. C'est le niveau des API Json RPC ou encore SOAP.

### Niveau 1 : Identification des ressources

Les entités avec lesquels les interactions ont lieu sont identifiées en tant que ressources.

### Niveau 2 : Utilisation des verbes HTTP

Les interactions avec le serveur se font avec plusieurs verbes HTTP différents en **respectant** leurs sémantiques. Les opérations avec une ressource se font via un même identifiant mais avec des verbes différents.

Par exemple, le verbe *GET* pour récupérer du contenu ou *DELETE* pour le supprimer. En l'occurrence, le Json RPC utilise le verbe *POST* pour toutes ces opérations et par conséquent ne respecte pas ce modèle.

[[information]]
|Les verbes HTTP appelés aussi méthodes permettent de décrire avec une sémantique claire l'opération que nous voulons effectuer. Nous pouvons citer les plus courantes qui sont *GET*, *POST*, *PUT* et *DELETE*.

Pour finir les codes de statut du protocole permettent d'avoir des réponses plus expressives. Une réponse avec un code 404 permettra au client d'identifier que la ressource demandée n'existe pas.
Nous verrons plus en détails quelles sont les méthodes et codes de statut que nous pouvons utiliser dans la suite de ce cours.

### Niveau 3 : Contrôles hypermédia.

Comme déjà décrit dans la partie *Présentation de REST > Interface uniforme*, le contrôle hypermédia désigne l'état d'une application ou API avec un seul point d'entrée mais qui propose des éléments permettant de l'explorer et d'interagir avec elle.
Un bon exemple est le site web. Si par exemple, nous accédons à YouTube, la page d'accueil nous propose des liens vers des vidéos ou encore un formulaire de recherche. Ces éléments hypermédia permettent ainsi de visualiser toutes sortes de contenus sans connaitre au préalable les liens directs les identifiants.

![Modèle de maturité de Richardson](http://zestedesavoir.com/media/galleries/3183/8d8d9732-3b64-4832-ac8e-e1ac642c10e6.png)

Notre objectif sera de se rapprocher le plus possible de l'architecture REST sans oublier les contraintes que le monde réel va nous imposer.

# REST appliqué au WEB avec le protocole HTTP

Comme l'a dit Roy Fielding dans le chapitre 6 de sa thèse, l'objectif de REST était de créer un model architectural décrivant comment le web devrait fonctionner, le permettant de devenir ainsi une référence pour les protocoles web. REST a été conçu en évitant de violer les contraintes principales qui régissent le web.

> As described in Chapter 4, the motivation for developing REST was to create an architectural model for how the Web should work, such that it could serve as the guiding framework for the Web protocol standards. REST has been applied to describe the desired Web architecture, help identify existing problems, compare alternative solutions, and ensure that protocol extensions would not violate the core constraints that make the Web successful.

Le protocole de transfert HTTP dispose de beaucoup de spécificités que nous pouvons donc mettre en oeuvre avec le style d'architecture REST. Nous verrons comment mettre à profit ces spécifications afin de remplir les exigences d'une application dite RESTful.

## La séparation Client-Serveur

L'essence même du HTTP - protocole de transfert hypertexte - comme son nom l'indique est de permettre le transfert de données entre un client et un serveur. Dès lors, les applications web remplissent de-facto cette contrainte d'architecture.

L'utilisation de HTTP dans le cadre de REST pour une bonne isolation client-serveur est donc un choix judicieux et très largement répandu.

## Sans état (Stateless)

Il suffit de consulter le résumé de la même [RFC 7231](http://tools.ietf.org/html/rfc7231) pour voir que :

> The Hypertext Transfer Protocol (HTTP) is a stateless application-level protocol for distributed, collaborative, hypertext information systems.

Le protocole de transfert hypertexte (HTTP) est un protocol **sans état** de la [couche application](https://fr.wikipedia.org/wiki/Couche_application) (se référer au modèle OSI) pour les systèmes d'informations hypermédia distribuées et collaboratifs. 

Le protocole HTTP est stateless (sans état) par définition. Même si nous pouvons retrouver des applications web qui dérogent à cette contrainte, il faut retenir que HTTP a été pensé pour fonctionner sans état.

## Le Cache

Là aussi, le protocole HTTP supporte nativement [la gestion du cache](http://tools.ietf.org/html/rfc7234) via les entêtes comme *Cache-Control*, *Expires*, etc.
Ces entêtes permettent de réutiliser une même réponse si le contenu est considéré comme étant à jour comme le préconise le style d'architecture REST afin d'améliorer les performances de notre application.

## La gestion des ressources

### Identification

Nous avons déjà défini une ressource dans le cadre de REST et pourquoi il fallait l'identifier de manière unique.
Le protocole HTTP utilise là aussi une notion bien connue: l'URI (Uniform Resource Identifier). En effet, lorsque nous consultons la [RFC 2731](http://tools.ietf.org/html/rfc7231) de HTTP 1.1, nous pouvons voir que une ressource est définie comme étant:

> The target of an HTTP request is called a "resource".  HTTP does not limit the nature of a resource; it merely defines an interface that might be used to interact with resources.  Each resource is identified by a Uniform Resource Identifier (URI), as described in Section 2.7 of [RFC7230].

La cible d'une requête HTTP est appelé une « ressource ». HTTP ne met pas de limitation sur la nature d'une ressource; il définit seulement une interface qui peut être utilisé pour interagir avec des ressources.  Chacune de ces ressources est identifiée par une URI (Uniform Resource Identifier), comme décrit dans la section 2.7 de la [RFC7230].

### Représentation

Une représentation est toute information destinée à refléter l'état passé, actuel ou voulu d'une ressource donnée.

> For the purposes of HTTP, a "representation" is information that is intended to reflect a past, current, or desired state of a given resource, ... 
Source: [RFC 7231](http://tools.ietf.org/html/rfc7231)

Ainsi avec les URI et les représentations des réponses HTTP (html, xml, json, etc.), nous pouvons satisfaire la contrainte 4 d'interface uniforme de REST pour mettre en place notre application.

# Ce cours

## Notre application Web

Durant ce cours, nous allons développer une API permettant de gérer des idées et suggestions de sorties récréatives en se basant sur les concepts REST. Cette application va nous servir de fil conducteur pour ce cours et toutes ses fonctionnalités seront détailllées plus tard.

Les prérequis pour suivre ce cours, il faut des connaissances minimum de Symfony 2.7 à 3.* :

- créer une application avec Symfony ;
- Utiliser Doctrine 2 avec Symfony ;
- Utiliser l'injection de dépendances de Symfony.

Les objectifs de ce cours sont entre autres de :

- Comprendre l'architecture REST ;
- Mettre en place une API RESTful (Créer une API uniforme et facile à utiliser) ;
- Apprendre comment sécuriser une API (REST en particulier) ;
- Savoir utiliser les avantages de Symfony dans ses développements (Composants et Bundles).﻿

## Description du besoin

Nous allons mettre en place une application permettant de gérer des idées et suggestions de sorties récréatives. 
L'application dispose de plusieurs lieux (restaurants, centre de loisirs, cinéma etc) connus et réputés et de plusieurs utilisateurs avec leurs centres d'intérêt.
L'objectif est de proposer un mécanisme permettant de proposer à chaque utilisateur une idée de sortie la plus pertinente en se basant sur ses préférences.

## Technologies utilisées

Les exemples présentés se baseront sur Symfony 3 avec *FOSRestBundle*. Les tests de l'API se feront avec cURL (utilitaire en ligne de commande) et le logiciel Postman (extension du navigateur Chrome).
