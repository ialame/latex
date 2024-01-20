Comme pour tous les bundles de Symfony, la méthode la plus simple pour l'installateur est d'utiliser le gestionnaire de dépendances *Composer*. 
Pour les besoins du cours, nous allons installer la version ^2.1 (2.1.0 pour mon cas) qui apporte un support plus complet de Symfony 3.
Depuis la console, il suffit de lancer la commande :
```bash
composer require friendsofsymfony/rest-bundle "^2.1"

# Réponse
#> ./composer.json has been updated
#> Loading composer repositories with package informatio
#> Updating dependencies (including require-dev)
#>   - Installing willdurand/jsonp-callback-validator (v
#>     Downloading: 100%
#> 
#>   - Installing willdurand/negotiation (1.5.0)
#>     Downloading: 100%
#> 
#>   - Installing friendsofsymfony/rest-bundle (2.1.0)
#>     Downloading: 100%

```

Ensuite, il suffit d'activer le bundle dans Symfony en éditant le fichier ***AppKernel***.

```php
# app/AppKernel.php
<?php

use Symfony\Component\HttpKernel\Kernel;
use Symfony\Component\Config\Loader\LoaderInterface;

class AppKernel extends Kernel
{
    public function registerBundles()
    {
        $bundles = [
            // ... D'autres bundles déjà présents
            new Sensio\Bundle\FrameworkExtraBundle\SensioFrameworkExtraBundle(),
            new FOS\RestBundle\FOSRestBundle(),
            new AppBundle\AppBundle(),
        ];
        // ...
    }

    // ...
}
```

À l'état actuel, l'installation n'est pas encore complète. Si nous lançons la commande `php bin/console debug:config fos_rest` une belle exception est affichée.

```text
 [InvalidArgumentException]
  Neither a service called "jms_serializer.serializer" nor "serializer" is available and no serializer is explicitly configured. You must either enable the JMSSerializerBundle, enable the Framework
  Bundle serializer or configure a custom serializer.
```

En effet, pour traiter les réponses, ce bundle a besoin d'un outil de sérialisation.

[[information]]
|La sérialisation est un processus permettant de convertir des données (une instance d'une classe, un tableau, etc.) en un format prédéfini. Pour le cas de notre API, la sérialisation est le mécanisme par lequel nos objets PHP seront transformés en un format textuel (JSON, XML, etc.).

Heureusement pour nous, l'installation standard de Symfony contient un composant de sérialisation que nous pouvons utiliser.

Par ailleurs, *FOSRestBundle* supporte le sérialiseur fourni par le bundle [JMSSerializerBundle](https://github.com/schmittjoh/JMSSerializerBundle) qui fournit plus de possibilités. 

Mais pour nos besoins, le sérialiseur standard suffira largement. Nous allons donc l'activer en modifiant la configuration de base dans le fichier ***app/config/config.yml***.
```yaml
# app/config/config.yml
framework:
    # ...
    serializer:
        enabled: true
```

Maintenant en retapant la commande `php bin/console debug:config fos_rest`, nous obtenons :

```text
php bin/console debug:config fos_rest
Current configuration for extension with alias "fos_rest"
=========================================================

fos_rest:
    disable_csrf_role: null
    access_denied_listener:
        enabled: false
        service: null
        formats: {  }
    unauthorized_challenge: null
    param_fetcher_listener:
        enabled: false
    ...
```

Et voilà !

Le bundle *FOSRestBundle* fournit un ensemble de fonctionnalités permettant de développer une API REST. Nous allons en explorer une bonne partie tout au long de ce cours. Mais commençons d'abord par le système de routage et de gestion des réponses.