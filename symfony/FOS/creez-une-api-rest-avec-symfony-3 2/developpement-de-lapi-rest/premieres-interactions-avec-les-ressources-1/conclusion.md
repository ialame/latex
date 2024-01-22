Après cette première introduction, nous pouvons retenir qu'en REST les interactions ont lieu avec soit une collection soit une instance de celle-ci : une ressource.

Chaque opération peut alors être décrite comme étant une requête sur une URL bien identifiée avec un verbe HTTP adéquat. Le type de la réponse est décrit par un code de statut.

Voici un petit récapitulatif du mode de fonctionnement :

|Opération souhaitée|  Verbe HTTP   |
|-------------------|---------------|
|Lecture            |  GET          |

|Code statut |  Signification         |
|------------|------------------------|
|200         |  Tout s'est bien passé |
|404         |  La ressource demandée n'existe pas |
|500         |  Une erreur interne a eu lieu sur le serveur |

En résumé, chaque verbe est destiné à une action et la réponse est décrite en plus des données explicitées par un code de statut.

Pour concevoir une bonne API RESTful, il faut donc toujours se poser ces questions : 

- Sur quelle ressource mon opération doit s'effectuer ?
- Quel verbe HTTP décrit le mieux cette opération ?
- Quelle URL permet d'identifier la ressource ?
- Et quel code de statut doit décrire la réponse ?