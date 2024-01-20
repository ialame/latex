# Quel outil pouvons-nous utiliser pour créer la documentation ?

Bien que le résultat final du fichier *OpenAPI* soit en JSON, il peut être rédigé aussi bien en JSON qu'en YAML. Nous préférerons d'ailleurs le YAML par la suite.

Pour créer ce fichier ***swagger.json***, il faut suivre les spécifications qui sont disponibles en ligne : [Spécification *OpenAPI* (Swagger)](http://swagger.io/specification/).

L'un des moyens les plus simples pour rédiger et tester les spécifications est d'utiliser le site [Swagger Editor](http://editor.swagger.io). Ce site propose une prévisualisation de la documentation qui sera générée et des exemples de configuration (en YAML) qui permettent de mieux appréhender les spécifications d'*OpenAPI*.

# Structure de base du fichier swagger.json

Un fichier ***swagger.json*** a trois attributs obligatoires :

- `swagger` : définit la version des spécifications utilisées ;
- `info` : définit les métadonnées de notre API ;
- et `paths` : définit les différentes URL et opérations disponibles dans l'API.

Le fichier de base ressemble donc a :
```yaml
swagger: '2.0' # obligatoire
info: # obligatoire
  title: Proposition de suggestions API    # obligatoire
  description: Proposer des idées de sortie à des utilisateurs en utilisant leurs préférences
  version: "1.0.0" # obligatoire

host: rest-api.local
schemes:
  - http
produces:
  - application/json
  - application/xml
consumes:
  - application/json
  - application/xml
  
paths: # obligatoire

```

![Prévisualisation de la structure de base](https://zestedesavoir.com/media/galleries/3183/faf6c865-2396-4065-b99e-ce7e57b40cc7.png)

Les attributs `produces` et `consumes` permettent de décrire les type MIME des réponses renvoyées et des requêtes acceptées par notre API.
Il est possible d'utiliser du [Markdown](https://help.github.com/articles/basic-writing-and-formatting-syntax/) pour formater les différentes descriptions (attributs `description`) dans la documentation.

[[information]]
| Tous les tests se feront en utilisant directement le site [http://editor.swagger.io](http://editor.swagger.io). Le fichier ***swagger.json*** définitif sera testé en local dans la dernière partie.

# Déclarer une opération avec l'API

## Documentation de la méthode de connexion

Pour commencer, nous allons essayer de rédiger la documentation de la méthode d'authentification à l'API.
Pour déclarer une opération, nous devons utiliser l'attribut `paths`.

```yaml
swagger: '2.0' # obligatoire
info: # obligatoire
  title: Proposition de suggestions API    # obligatoire
  description: Proposer des idées de sortie à des utilisateurs en utilisant leurs préférences
  version: "1.0.0" # obligatoire

host: rest-api.local
schemes:
  - http
produces:
  - application/json
  - application/xml
consumes:
  - application/json
  - application/xml
  
paths: # obligatoire
  /auth-tokens:
    post:
      summary: Authentifie un utilisateur
      description: Crée un token permettant à l'utilisateur d'accéder aux contenus protégés
      responses: # obligatoire

```

Voici la base permettant de créer des opérations. Sous l'attribut `paths`, il faut définir l'URL de notre ressource et ensuite il faut déclarer les différents verbes HTTP qui sont utilisés sur celle-ci.
Actuellement, nous avons la méthode `POST` permettant de créer un token. Nous devons maintenant définir :

- le payload de la requête ;
- la réponse en cas de succès ;
- la réponse en cas d'erreur.

Toutes ces données sont déclarées en utilisant les spécifications de [JSON Schema](http://json-schema.org/).

```yaml
# ...
paths: # obligatoire
  /auth-tokens:
    post:
      summary: Authentifie un utilisateur
      description: Crée un token permettant à l'utilisateur d'accéder aux contenus protégés
      parameters:
        - name: credentials  # obligatoire
          in: body  # obligatoire
          required: true
          description: Login et mot de passe de l'utilisateur
          schema:
            type: object
            required: [login, password]
            properties:
              login: 
                type: string
              password:
                type: string
            
            
      responses:
        200:
          description: Token créé  # obligatoire
          schema:
            type: object
            properties:
              id:
                type: integer
              value:
                type: string
              created_at:
                type: string
                format: date-time
              user:
                type: object
                properties:
                  id:
                    type: integer
                  email:
                    type: string
                    format: email
                  firstname:
                    type: string
                  lastname:
                    type: string
      
        400:
          description: Donnée invalide  # obligatoire
          schema:
            type: object
            required: [message]
            properties:
              code:
                type: integer
              message:
                type: string
              errors:
                type: object
                properties:
                  children:
                    type: object
                    properties:
                      login:
                        type: object
                        properties:
                          errors:
                            type: array
                            items:
                              type: string
                      password:
                        type: object
                        properties:
                          errors:
                            type: array
                            items:
                              type: string  

```

![Documentation de la méthode de création d'un token](https://zestedesavoir.com/media/galleries/3183/8ffb84a8-1d53-463c-8b4d-ae7c441788a7.png)

Il est aussi possible de mieux organiser le fichier en rajoutant une entrée `definitions` qui permet de regrouper tous les schémas que nous avons déclarés. Ensuite, il suffira de faire référence à ces schémas en utilisant l'attribut `$ref`.

```yaml
# ...
paths: # obligatoire
  /auth-tokens:
    post:
      summary: Authentifie un utilisateur
      description: Crée un token permettant à l'utilisateur d'accéder aux contenus protégés
      parameters:
        - name: credentials  # obligatoire
          in: body  # obligatoire
          required: true
          description: Login et mot de passe de l'utilisateur
          schema:
            $ref: "#/definitions/Credentials"
            
      responses:
        200:
          description: Token créé  # obligatoire
          schema:
            $ref: "#/definitions/AuthToken.auth-token"

        400:
          description: Donnée invalide  # obligatoire
          schema:
             $ref: "#/definitions/CredentialsTypeError" 

definitions:
  Credentials:
    type: object
    required: [login, password]
    properties:
      login: 
        type: string
      password:
        type: string
        
  AuthToken.auth-token:
    type: object
    required: [id, value, created_at, user]
    properties:
      id:
        type: integer
      value:
        type: string
        title: Token d'authentification
        description: Valeur à utiliser dans l'entête X-Auth-Token
      created_at:
        type: string
        format: date-time
      user:
        type: object
        properties:
          id:
            type: integer
          email:
            type: string
            format: email
          firstname:
            type: string
          lastname:
            type: string       
    
  CredentialsTypeError:
    type: object
    required: [message]
    properties:
      code:
        type: integer
      message:
        type: string
      errors:
        type: object
        properties:
          children:
            type: object
            properties:
              login:
                type: object
                properties:
                  errors:
                    type: array
                    items:
                      type: string
              password:
                type: object
                properties:
                  errors:
                    type: array
                    items:
                      type: string
```

Avec ces modifications, le résultat obtenu est exactement identique.

## Documentation de la méthode de déconnexion

De la même façon pour documenter la suppression d'un token, nous devons rajouter une nouvelle URL. Mais cette fois-ci, elle doit être dynamique comme pour les routes Symfony.

```yaml
# ...
  
paths: # obligatoire
  /auth-tokens:
   # ...

  /auth-tokens/{id}:
    delete:
      summary: Déconnecte un utilisateur
      description: Supprime le token de l'utilisateur
      parameters:
        - $ref: "#/parameters/X-Auth-Token"
        - name: id  # obligatoire
          in: path  # obligatoire
          type: integer  # obligatoire si le paramètre dans in est différent de 'body'
          required: true
          description: Identifiant du token à supprimer
            
      responses:
        204:
          description: Token supprimé  # obligatoire

        400:
          description: Donnée invalide  # obligatoire
          schema:
             $ref: "#/definitions/GenericError" 


parameters:
  X-Auth-Token:
    name: X-Auth-Token  # obligatoire
    in: header  # obligatoire
    type: string  # obligatoire si le paramètre dans in est différent de 'body'
    required: true
    description: Valeur du token d'authentification


definitions:
  # ...                
  GenericError:
    type: object
    required: [code, message]
    properties:
      code: 
        type: string
      message:
        type: string
```

À l'instar de la méthode de connexion, nous utilisons aussi le paramètre `in` pour désigner l'identifiant du token. Cet attribut peut valoir :

- `path` : le paramètre est extrait de l'URL de la ressource ;
- `query` : le paramètre est un query string ;
- `header` : le paramètre est une entête HTTP ;
- `body` : le paramètre est dans le payload ;
- `form` : le paramètre est dans le payload qui est encodé au format  *application/x-www-form-urlencoded* ou *multipart/form-data* (c'est le format utilisé par un formulaire classique).

L'entête HTTP `X-Auth-Token` est utilisée par plusieurs requêtes de notre API. En le déclarant dans l'attribut `parameters`, cela nous permet de le réutiliser dans les appels API qui nous intéressent.

[[information]]
|Il existe deux attributs `securityDefinitions` et `security` permettant de configurer la méthode d'authentification sans passer par l'attribut `parameters`. Mais pour les besoins de cet exemple, nous ne les utiliserons pas. 


![Documentation de la méthode de suppression d'un token](https://zestedesavoir.com/media/galleries/3183/eaa3f319-aedf-468e-8e57-acd04db5b35c.png)

Toutes les informations utilisées pour créer ce fichier sont issues [des spécifications officielles d'*OpenAPI*](http://swagger.io/specification/). Vous pourrez les consulter afin de voir l'ensemble des fonctionnalités qu'offrent *OpenAPI*.

*[MIME]: Multipurpose Internet Mail Extensions