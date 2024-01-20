Les codes de statuts `401` et `403` sont souvent source de confusion. Ils sont tous les deux utiliser pour gérer les informations liées à la sécurité.

Le code `401` est utilisé pour signaler que **la requête nécessite une authentification**. Avec notre système de sécurité actuel, nous exigeons que toutes les requêtes - à part celle de création de token - aient une entête `X-Auth-Token` valide.
Donc si une requête ne remplit pas ces conditions, elle est considérée comme non autorisée d'où le code de statut `401`.
En général, c'est depuis [le pare-feu de Symfony](http://symfony.com/doc/current/components/security/firewall.html) que ce code de statut doit être renvoyé.


Par contre, le code `403` est utilisé pour signaler qu'**une requête est interdite**. La différence réside dans le fait que pour qualifier une requête comme étant interdite, nous devons d'abord identifier le client de l'API à l'origine de celle-ci. Le code `403` doit donc être utilisé si le client de l'API est bien identifié via l'entête `X-Auth-Token` mais ne dispose pas des privilèges nécessaires pour effectuer l'opération qu'il souhaite.

Si par exemple, nous disposons d'un appel API réservé uniquement aux administrateurs, si un client simple essaye d'effectuer cette requête, nous devrons renvoyer un code de statut `403`. Ce code de statut peut être utilisé au niveau des ACLs (Access Control List) ou [des voteurs de Symfony](http://symfony.com/doc/current/cookbook/security/voters.html).

[[information]]
| En résumé, le code de statut `401` permet de signaler au client qu'il doit décliner son identité et le code de statut `403` permet de notifier à un client déjà identifié qu'il ne dispose pas de droits suffisants. 
