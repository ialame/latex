# Cas des requêtes

Depuis que nous avons installé *FOSRestBundle*, notre API supporte déjà trois formats: le JSON, le format *x-www-form-urlencoded* (utilisé par les formulaires) et le XML.

Le body listener que nous avons activé utilise déjà par défaut ces trois formats. Pour déclarer le format utilisé dans la requête, il suffit d'utiliser l'entête HTTP `Content-Type` qui permet de décrire le type du contenu de la requête (et même de la réponse).

Avec Postman, nous pouvons tester la création d'un utilisateur en exploitant cette fonctionnalité. Au lieu d'avoir du JSON, nous devons juste formater la requête en XML.
Le corps de la requête doit être :
```xml
<user>
    <firstname>test</firstname>
    <lastname>XML</lastname>
    <email>test@xml.fr</email>
</user>
```

Chaque format a un type MIME qui permet de le décrire avec l'entête `Content-Type` :

- JSON: Application/json
- XML: application/xml

[[information]]
|C'est au client de définir dans sa requête le format utilisé pour que le serveur puisse la traiter correctement.

Avec Postman, il y a un onglet ||Headers|| qui permet de rajouter des entêtes HTTP. Pour faciliter le travail, nous pouvons aussi choisir dans l'onglet ||Body||, le contenu de la requête. Postman rajoutera automatiquement le bon type MIME de la requête à notre place.

![Choix du type de contenu avec Postman](http://zestedesavoir.com/media/galleries/3183/8fb3c712-d2fd-4eda-b63b-9a520bbe4647.png)

![Entête rajoutée par Postman](http://zestedesavoir.com/media/galleries/3183/a37ae749-7ad9-46dd-bb93-2a5d74e0ee25.png)

En envoyant la requête, l'utilisateur est créé et nous obtenons une réponse en ... JSON ! Nous allons donc voir dans la partie suivante comment autoriser plusieurs formats de réponses comme nous l'avons déjà pour les requêtes.

[[information]]
|Il est possible de supporter d'autres formats en plus de celle par défaut. Pour en savoir plus, vous pouvez consulter [la documentation officielle](http://symfony.com/doc/1.7/bundles/FOSRestBundle/body_listener.html).

# Cas des réponses

L'utilisation de l'annotation `View` de *FOSRestBundle* permet de créer des réponses qui peuvent être affichées dans différents formats. Dans tous nos contrôleurs, nous nous contentons de renvoyer un objet ou un tableau et ces données sont envoyées au client dans le bon format.

Pour supporter plusieurs formats, les données renvoyées par les contrôleurs ne changent pas. Nous devons juste configurer *FOSRestBundle* correctement.
Ce bundle supporte deux types de réponses :

- celles ne nécessitant pas de template pour être affichées : celles au format JSON, au format XML, etc. Il suffit d'avoir les données pour les encoder et le sérialiseur fait le reste du travail.
- celles qui nécessitent un template : le html, etc. Pour ce genre de réponse, nous devons avoir des informations en plus permettant de *décorer* la réponse (mise en page, CSS, etc.) et le moteur de rendu (ici Twig) s'occupe du reste.

Dans le cadre du cours, nous allons juste aborder le premier type de réponse. [La documentation](http://symfony.com/doc/current/bundles/FOSRestBundle/2-the-view-layer.html) couvre bien l'ensemble du sujet si cela vous intéresse.

Pour activer ces fonctionnalités, nous devons configurer deux sections. La première nous permettra de déclarer les formats de réponses supportés et la seconde nous permettra de configurer la priorité entre ces formats, le comportement du serveur si aucun format n'est choisi par le client, etc.

Nous allons supporter les formats JSON et XML pour les réponses. La configuration devient maintenant (la clé `formats` a été rajoutée) :
```yaml
# app/config/config.yml
# ...

fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
        formats:
            json: true
            xml: true
    format_listener:
        rules:
            - { path: '^/', priorities: ['json'], fallback_format: 'json', prefer_extension: false }
    body_listener:
        enabled: true
```

En réalité, ces deux formats sont déjà activés par défaut mais par soucis de clarté nous allons les laisser visibles dans le fichier de configuration.

Le reste de la configuration se fait avec la clé `rules`. C'est au niveau des priorités (clé `priorities`) que les formats supportés sont définis. Pour notre configuration, nous avons une seule règle. Mais il est tout à fait possible de définir plusieurs règles différentes selon les URL utilisées. Nous pouvons imaginer par exemple une règle par version de l'api, ou bien encore une règle par ressources.

Il suffit de rajouter le format XML aux priorités et notre API pourra répondre aussi bien en XML qu'en JSON.
```yaml
# app/config/config.yml
# ...

fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
        formats:
            json: true
            xml: true
    format_listener:
        rules:
            - { path: '^/', priorities: ['json', 'xml'], fallback_format: 'json', prefer_extension: false }
    body_listener:
        enabled: true

```
[[information]]
|C'est maintenant au client d'informer le serveur sur le ou les formats qu'il préfère.

[[erreur]]
|L'ordre de déclaration est très important ici. Si une requête ne spécifie aucun format alors le serveur choisira du JSON.

# La négociation de contenu

La négociation de contenu est un mécanisme [du protocole HTTP](http://tools.ietf.org/html/rfc7231#section-4.3.4) qui permet de proposer plusieurs formats pour une même ressource.
Pour sa mise en œuvre, le client doit envoyer un entête HTTP de la famille `Accept`. Nous avons entre autres :

->

|Entête         |   Utilisation |
|---------------|---------------|
|Accept         | Pour choisir un média type (text, json, html etc).|
|Accept-Charset | Pour choisir le jeu de caractères (iso-8859-1, utf8, etc.)|
|Accept-Language| Pour choisir le langage (français, anglais, etc.)|

<-

L'entête qui nous intéresse ici est `Accept`. Comme pour l'entête `Content-Type`, la valeur de cet entête doit contenir un type MIME.

Mais en plus, avec cet entête, nous pouvons déclarer plusieurs formats à la fois en prenant le soin de définir un ordre de préférence en utilisant un facteur de qualité.

[[information]]
| Le facteur de qualité (`q`) est un nombre compris entre 0 et 1 qui permet de définir l'ordre de préférence. Plus `q` est élevé, plus le type MIME associé est prioritaire.

Une requête avec comme entête `Accept: application/json;q=0.7, application/xml;q=1,` veut dire que le client préfère du XML et en cas d'indisponibilité du XML alors du JSON.

Une requête avec comme entête `Accept: application/xml` veut dire que le client préfère du XML. Si le facteur de qualité n'est pas spécifié, sa valeur est à `1`.

Pour tester, nous allons ajouter cet entête à une requête pour lister tous les lieux de notre API.

![Récupération des lieux en XML avec Postman](http://zestedesavoir.com/media/galleries/3183/2ea83009-b7f6-46c8-b29b-5adc8c0ef88f.png)

La réponse est bien en XML et nous pouvons tester avec n'importe quelle méthode de notre API.
```xml
<?xml version="1.0"?>
<response>
    <item key="0">
        <id>1</id>
        <name>Tour Eiffel</name>
        <address>5 Avenue Anatole France, 75007 Paris</address>
        <prices>
            <id>1</id>
            <type>less_than_12</type>
            <value>5.75</value>
        </prices>
        <themes>
            <id>1</id>
            <name>architecture</name>
            <value>7</value>
        </themes>
        <themes>
            <id>2</id>
            <name>history</name>
            <value>6</value>
        </themes>
    </item>
    <item key="1">
        <id>2</id>
        <name>Mont-Saint-Michel</name>
        <address>50170 Le Mont-Saint-Michel</address>
        <prices/>
        <themes>
            <id>3</id>
            <name>history</name>
            <value>3</value>
        </themes>
        <themes>
            <id>4</id>
            <name>art</name>
            <value>7</value>
        </themes>
    </item>
    <item key="2">
        <id>4</id>
        <name>Disneyland Paris</name>
        <address>77777 Marne-la-Vall&#xE9;e</address>
        <prices/>
        <themes/>
    </item>
    <item key="3">
        <id>5</id>
        <name>Aquaboulevard</name>
        <address>4-6 Rue Louis Armand, 75015 Paris</address>
        <prices/>
        <themes/>
    </item>
    <item key="4">
        <id>6</id>
        <name>test</name>
        <address>test</address>
        <prices/>
        <themes/>
    </item>
</response>
```

Le serveur renvoie aussi un entête `Content-Type` pour signaler au client le format de la réponse.

![Entête renvoyée par le serveur pour le format de la réponse](http://zestedesavoir.com/media/galleries/3183/c231682c-a71d-4cc1-801d-050b5fba1b11.png)

[[attention]]
|Attention, certaines API proposent de rajouter un format à une URL pour sélectionner un format de réponse (places.json, places.xml, etc.). Cette technique ne respecte pas les contraintes REST vu que l'URL doit juste servir à identifier une ressource.

*[MIME]: Multipurpose Internet Mail Extensions