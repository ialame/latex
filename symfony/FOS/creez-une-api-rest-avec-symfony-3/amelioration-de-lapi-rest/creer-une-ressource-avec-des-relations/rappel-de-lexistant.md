Jusqu'à présent pour créer un lieu, il fallait juste renseigner le nom et l'adresse. Le payload pour la création d'un lieu ressemblait donc à :

```json
{
    "name": "Disneyland Paris",
    "address": "77777 Marne-la-Vallée"
}
```

En réalité, pour supporter la création d'un lieu avec ses tarifs, les contraintes de REST ne rentrent pas en jeu. Nous pouvons adapter librement le payload afin de rajouter toutes les informations nécessaires pour supporter la création d'un lieu avec ses tarifs avec un seul appel.