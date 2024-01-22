Pour revenir sur nos tableaux récapitulatifs, voici le mode de fonctionnement simplifié d'une API REST :

|Opération souhaitée|  Verbe HTTP   |
|-------------------|---------------|
|Lecture            |  GET          |
|Création           |  POST         |
|Suppression        |  DELETE       |

|Code statut |  Signification                                      |
|------------|-----------------------------------------------------|
|200         |  Tout s'est bien passé et la réponse a du contenu   |
|204         |  Tout s'est bien passé mais la réponse est vide     |
|400         |  Les données envoyées par le client sont invalides  |
|404         |  La ressource demandée n'existe pas                 |
|500         |  Une erreur interne a eu lieu sur le serveur        |