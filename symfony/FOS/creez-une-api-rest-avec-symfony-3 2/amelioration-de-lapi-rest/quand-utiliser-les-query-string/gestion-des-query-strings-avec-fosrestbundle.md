Avant d'aborder les cas pratiques, nous allons commencer par voir comment *FOSRestBundle* nous permet de définir les query strings.

Le framework Symfony supporte de base les query strings mais *FOSRestBundle* rajoute beaucoup de fonctionnalités comme : 

- définir des règles de validation pour ce query string ;
- définir une valeur par défaut ;
- et beaucoup d'autres fonctionnalités.

# L'annotation QueryParam

Pour accéder à toutes ces fonctionnalités, il suffit d'utiliser une annotation `FOS\RestBundle\Controller\Annotations\QueryParam` sur le ou les actions de nos contrôleurs.

[[information]]
|Il est aussi possible d'utiliser cette annotation sur un contrôleur mais nous ne parlerons pas de ce cas d'usage.

```php
<?php
/**
 * @QueryParam(
 *   name="",
 *   key=null,
 *   requirements="",
 *   incompatibles={},
 *   default=null,
 *   description="",
 *   strict=false,
 *   array=false,
 *   nullable=false
 * )
 */
```

Nous aborderons les cas d'utilisation des attributs de cette annotation dans la suite.

# Le listener

Pour dire à *FOSRestBundle* de traiter cette annotation, nous devons activer un listener dédié appelé le *Param Fetcher Listener*. 
Pour ce faire, nous allons modifier le fichier de configuration :

```yaml
# app/config/config.yml

# ...

fos_rest:
    routing_loader:
        include_format: false
    view:
        view_response_listener: true
        formats:
            json: true
            xml: true
    format_listener:
        rules:
            - { path: '^/', priorities: ['json', 'xml'], fallback_format: 'json', prefer_extension: false }
    body_listener:
        enabled: true
    param_fetcher_listener:
        enabled: true
# ...
```

Maintenant que le listener est activé, nous pouvons passer aux choses sérieuses.

