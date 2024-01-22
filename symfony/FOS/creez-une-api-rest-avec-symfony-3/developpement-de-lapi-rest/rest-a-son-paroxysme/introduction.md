Il reste un point sur les contraintes REST que nous n'avons toujours pas abordé : l'Hypermédia. En plus, notre API supporte un seul format le JSON. Toutes les requêtes et toutes les réponses sont en JSON. Nous imposons donc une contrainte aux futurs clients de notre API.

Pour remédier à cela, nous allons voir comment supporter facilement d'autre format de réponse en utilisant *FOSRestBundle* et le sérialiseur de Symfony.
Et pour finir, nous verrons comment mettre en place de l'hypermédia dans une API REST, son utilité et comment l'exploiter (*si cela est possible*) ?