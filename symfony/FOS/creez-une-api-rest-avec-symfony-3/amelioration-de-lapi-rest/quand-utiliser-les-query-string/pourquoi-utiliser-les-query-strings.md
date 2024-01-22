Le premier cas d'usage qui est assez courant lorsque nous utilisons ou nous développons une API est la pagination ou le filtrage des réponses que nous obtenons.

Nous pouvons actuellement lister tous les lieux ou tous les utilisateurs de notre application. Cette réponse peut rapidement poser des problèmes de performance si ces listes grossissent dans le temps.

[[question]]
|Comment alors récupérer notre liste de lieux tout en réduisant/filtrant cette liste alors que nous avons un seul appel permettant de lister les lieux de notre application : `GET rest-api.local/places` ?

La liste de lieux est une ressource avec un identifiant `places`. Pour récupérer cette même liste tout en conservant son identifiant nous ne pouvons pas modifier l'URL.

Par contre, les query string nous permettent de pallier à ce genre de problèmes.

> The query component contains non-hierarchical data that, along with
   data in the path component (Section 3.3), serves **to identify a
   resource within the scope of the URI's scheme** and naming authority
   (if any).

Source: [RFC 3986](https://tools.ietf.org/html/rfc3986#section-3.4)

Donc au sein d'une même URL (ici `rest-api.local/places`), nous pouvons rajouter des query strings afin d'obtenir des réponses différentes mais qui représentent toutes une liste de lieux. 