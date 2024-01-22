[[question]]
|Que serait une API s'il était impossible de comprendre son mode de fonctionnement ?

Parler de documentation dans une API RESTful se rapproche beaucoup d'un oxymore. En effet, une API dite RESTFul devrait pouvoir être utilisée sans documentation.

Mais si vous vous souvenez bien, notre API n'implémente pas le niveau 3 du modèle de maturité de Richardson : HATEOAS qui permettrait de l'explorer automatiquement et d'interagir avec elle. Dès lors, pour faciliter son usage nous devons créer une documentation.

Elle permettra ainsi aux clients de notre API de comprendre son mode de fonctionnement et d'explorer rapidement les différentes fonctionnalités qu'elle expose. 

Il existe un standard appelé *OpenAPI*, anciennement connu sous le nom de *Swagger RESTful API*, permettant d'avoir des spécifications simples pour une documentation exhaustive.

L'objectif de cette partie est d'avoir un aperçu de *OpenAPI* et de voir comment mettre en place une documentation en implémentant ces spécifications.
