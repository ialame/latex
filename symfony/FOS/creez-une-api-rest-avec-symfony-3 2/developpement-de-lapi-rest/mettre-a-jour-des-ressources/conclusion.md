Nos tableaux récapitulatifs s'étoffent encore plus et nous pouvons rajouter les opérations de mise à jour.

|Opération souhaitée                    |  Verbe HTTP   |
|---------------------------------------|---------------|
|Lecture                                |  GET          |
|Création                               |  POST         |
|Suppression                            |  DELETE       |
|Modification complète (remplacement)   |  PUT          |
|Modification partielle                 |  PATCH        |

|Code statut |  Signification                                      |
|------------|-----------------------------------------------------|
|200         |  Tout s'est bien passé et la réponse a du contenu   |
|204         |  Tout s'est bien passé mais la réponse est vide     |
|400         |  Les données envoyées par le client sont invalides  |
|404         |  La ressource demandée n'existe pas                 |
|500         |  Une erreur interne a eu lieu sur le serveur        |