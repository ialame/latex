# Installation de JMSSerializerBundle

Comme pour tous les bundles de Symfony, il suffit de le télécharger avec *Composer* et de l'activer.
Téléchargement du bundle :
```bash
composer require jms/serializer-bundle
# Using version ^1.1 for jms/serializer-bundle
./composer.json has been updated
```

Activation du bundle :
```php
<?php
# app/AppKernel.php
use Symfony\Component\HttpKernel\Kernel;
use Symfony\Component\Config\Loader\LoaderInterface;

class AppKernel extends Kernel
{
    public function registerBundles()
    {
        $bundles = [
            // ...
            new JMS\SerializerBundle\JMSSerializerBundle(),
            new FOS\RestBundle\FOSRestBundle(),
            new AppBundle\AppBundle(),
        ];
        // ...
        return $bundles;
    }
    // ...
}
```

# Configuration de JMSSerializerBundle

La configuration par défaut de ce bundle suffit largement pour commencer à l'exploiter.
Mais pour notre cas, puisque nous avons déjà pas mal de fonctionnalités qui dépendent du sérialiseur, nous allons modifier sa configuration.

## Gestion des dates PHP

Comme pour le sérialiseur natif de Symfony (depuis la version 3.1), la sérialisation des dates dans php est supportée nativement par *JMSSerializerBundle*. Nous pouvons, en plus, personnaliser ce comportement avec juste 4 lignes de configuration.

```yaml
# app/config/config.yml

# ...
jms_serializer:
    handlers:
        datetime:
            default_format: "Y-m-d\\TH:i:sP"
            default_timezone: "UTC"
```

La valeur `"Y-m-d\\TH:i:sP"` désigne [le format ISO 8601](https://fr.wikipedia.org/wiki/ISO_8601) pour les dates.

[[information]]
| L'attribut `default_format` prend en paramètre le même format que [la fonction date de PHP](http://php.net/manual/fr/function.date.php).

## Une question de casse : CamelCase ou snake_case ?

Dans tous les exemples que nous avons pu voir, les attributs dans les requêtes et les réponses sont toutes en minuscules.
À part l'attribut `plainPassword` utilisé pour créer un utilisateur et le champ `createdAt` associé à un token d'authentification, toutes nos attributs sont en minuscule. Mais dans le cadre d'une API plus complète, la question de la casse va se poser.

La seule contrainte qu'il faudra garder en tête est **la cohérence**. Si nous décidons d'utiliser des noms d'attributs en camelCase ou en snake_case, il faudra s'en tenir à ça pour tous les appels de l'API.

La configuration de tels paramètres est très simple aussi bien avec le sérialiseur de base de Symfony qu'avec le *JMSSerializer*. Nous allons donc garder la configuration par défaut du sérialiseur de Symfony qui est de conserver le même nom que celui des attributs de nos objets.

```yaml
# app/config/config.yml

imports:
    - { resource: parameters.yml }
    - { resource: security.yml }
    - { resource: services.yml }

parameters:
    locale: en
    jms_serializer.camel_case_naming_strategy.class: JMS\Serializer\Naming\IdenticalPropertyNamingStrategy

# ...
```

## Désactivation du sérialiseur natif

Maintenant que nous avons fini la configuration, il faut désactiver le sérialiseur natif de Symfony.

Vu qu'il n'est pas activé par défaut, nous pouvons retirer la configuration associée ou passer sa valeur à `false`.

```yaml
# app/config/config.yml

# ...

framework:
    # ...
    serializer:
        enabled: false
# ...
```

*FOSRestBundle* va maintenant utiliser directement le sérialiseur fournit par *JMSSerializerBundle*.

# Sérialiser les attributs même s'ils sont nuls

Le comportement par défaut de *JMSSerializer* est d'ignorer tous les attributs nuls d'un objet. Ce fonctionnement peut entrainer des réponses avec des payloads partiels manquant certains attributs.
Pour éviter ce problème, *FOSRestBundle* propose un paramètre de configuration pour forcer *JMSSerializer* à sérialiser les attributs nuls.

```yaml
# app/config/config.yml

# ...

fos_rest:
    serializer:
        serialize_null:  true
```