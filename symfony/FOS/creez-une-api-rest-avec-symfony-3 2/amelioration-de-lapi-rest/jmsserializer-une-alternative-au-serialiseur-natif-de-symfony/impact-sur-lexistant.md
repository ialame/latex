# Tests de la configuration

Pour tester notre configuration, nous allons lister les lieux dans notre application.

La réponse obtenue est :
```json
{
  "0": {},
  "1": {}
}
```
Nous avons là une bonne et une mauvaise nouvelle. Le bundle est bien utilisé pour sérialiser la réponse mais les groupes de sérialisation, que nous avons définis, ne sont pas encore exploités.

[[question]]
|Pourquoi la réponse n'est pas sérialisée correctement ?

Le sérialiseur est pleinement supporté par *FOSRestBundle*. Les configurations dans tous nos contrôleurs sont déjà compatibles.
Par contre, le fichier ***src/AppBundle/Resources/config/serialization.yml*** décrivant les règles de sérialisation, est ignoré par *JMSSerializerBundle*.

La configuration par défaut se base sur une convention simple. Pour un bundle, les fichiers décrivant la sérialisation doivent être dans le dossier ***src/NomDuBundle/Resources/config/serializer/***.

Le nom de chaque fichier contenant les règles de sérialisation d'une classe est obtenu en faisant deux opérations :

- le nom du bundle est retiré du namespace (espace de nom) de la classe ;
- les séparateurs anti-slash (`\`) sont remplacés par des points (`.`) ;
- et enfin, l'extension yml ou xml est rajouté au nom ainsi obtenu.

Par exemple, pour la classe `NomDuBundle\A\B`, si nous voulons utiliser une configuration en YAML, nous devons avoir un fichier ***src/NomDuBundle/Resources/config/serializer/A.B.yml***.


[[information]]
| *JMSSerialiserBundle* supporte aussi les annotations et les fichiers XML pour la configuration des règles de sérialisation. D'ailleurs, si nous avions utilisé les annotations, le code fonctionnerait sans adaptation de notre part.

# Mise à jour de nos règles de sérialisation

Pour remettre notre API d'aplomb, nous allons créer les fichiers de configuration pour les classes utilisées.

Commençons par l'entité `Place`. La configuration pour cette classe devient :
```yaml
# src/AppBundle/Resources/config/serializer/Entity.Place.yml
AppBundle\Entity\Place:
    exclusion_policy: none
    properties:
        id:
            groups: ['place', 'price', 'theme']
        name:
            groups: ['place', 'price', 'theme']
        address:
            groups: ['place', 'price', 'theme']
        prices:
            groups: ['place']
        themes:
            groups: ['place']
``` 

Par défaut, aucune propriété de nos classes n'est affichée pendant la sérialisation. En mettant l'attribut `exclusion_policy` à `none`, nous configurons le sérialiseur pour inclure par défaut toutes les propriétés de la classe. Nous pourrons bien sûr exclure certaines propriétés à la demande (`exclude: true`).

De même, il est aussi possible d'adopter la stratégie inverse à savoir exclure par défaut toutes les propriétés de nos classes et les ajouter à la demande (`expose: true`).

Il faut aussi noter que l'attribut `attributes` dans l'ancien fichier de configuration est remplacé par `properties`. Tout le reste est identique à notre ancien fichier de configuration. 

La configuration des nouvelles classes devient maintenant :
```yaml
# src/AppBundle/Resources/config/serializer/Entity.Price.yml
AppBundle\Entity\Price:
    exclusion_policy: none
    properties:
        id:
            groups: ['place', 'price']
        type:
            groups: ['place', 'price']
        value:
            groups: ['place', 'price']
        place:
            groups: ['price']
```

```yaml
# src/AppBundle/Resources/config/serializer/Entity.Theme.yml
AppBundle\Entity\Theme:
    exclusion_policy: none
    properties:
        id:
            groups: ['place', 'theme']
        name:
            groups: ['place', 'theme']
        value:
            groups: ['place', 'theme']
        place:
            groups: ['theme']
```

```yaml
# src/AppBundle/Resources/config/serializer/Entity.User.yml
AppBundle\Entity\User:
    exclusion_policy: none
    properties:
        id:
            groups: ['user', 'preference', 'auth-token']
        firstname:
            groups: ['user', 'preference', 'auth-token']
        lastname:
            groups: ['user', 'preference', 'auth-token']
        email:
            groups: ['user', 'preference', 'auth-token']
        preferences:
            groups: ['user']
```

```yaml
# src/AppBundle/Resources/config/serializer/Entity.Preference.yml
AppBundle\Entity\Preference:
    exclusion_policy: none
    properties:
        id:
            groups: ['user', 'preference']
        name:
            groups: ['user', 'preference']
        value:
            groups: ['user', 'preference']
        user:
            groups: ['preference']
```

```yaml
# src/AppBundle/Resources/config/serializer/Entity.AuthToken.yml
AppBundle\Entity\AuthToken:
    exclusion_policy: none
    properties:
        id:
            groups: ['auth-token']
        value:
            groups: ['auth-token']
        createdAt:
            groups: ['auth-token']
        user:
            groups: ['auth-token']

```

[[erreur]]
| N'oubliez pas de vider le cache pour éviter tout problème.

En testant cette nouvelle configuration, la liste des lieux dans notre application redevient correcte.

```json
[
  {
    "id": 1,
    "name": "Tour Eiffel",
    "address": "5 Avenue Anatole France, 75007 Paris",
    "prices": [
      {
        "id": 1,
        "type": "less_than_12",
        "value": 5.75
      }
    ],
    "themes": [
      {
        "id": 1,
        "name": "architecture",
        "value": 7
      },
      {
        "id": 2,
        "name": "history",
        "value": 6
      }
    ]
  },
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
  }
]
```

Vous pouvez tester l'ensemble des appels que nous avons déjà mis en place. L'API se comporte exactement de la même façon.
