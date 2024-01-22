
Notre fameux tableau récapitulatif s'enrichit d'un nouveau code de statut et listing des entêtes HTTP utilisables:

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
|*401*       |  Une authentification est nécessaire pour accéder à la ressource  |
|*403*       |  Le client authentifié ne dispose pas des droits nécessaires   |
|404         |  La ressource demandée n'existe pas                 |
|500         |  Une erreur interne a eu lieu sur le serveur        |

|Entête HTTP | Contenu |
|-----------------|----------------------------|
|Accept | Un ou plusieurs  média type souhaités﻿﻿ par le client |
|Content-Type| Le média type de la réponse ou de la requête|
|X-Auth-Token | Token d'authentification |



Un client de l'API peut maintenant se connecter et se déconnecter et toutes ses requêtes nécessitent une authentification. Notre API vient d'être sécurisée ! La durée de validité du token et les critères de validation de celui-ci sont purement arbitraires. Vous pouvez donc les changer à votre guise.

Pour les besoins de ce cours les requêtes se font via HTTP mais il faudra garder en tête que la meilleure des sécurités ne vaut rien si le protocole utilisé n'est pas sécurisé. Donc dans une API en production, il faut **systématiquement** utiliser le **HTTPS**.