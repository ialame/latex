La référence en matière de documentation d'une API avec Symfony est le bundle *NelmioApiDocBundle*. Comme pour tous les bundles de Symfony, l'installation est particulièrement simple.
Avec *Composer*, nous allons rajouter la dépendance :

```bash
composer require nelmio/api-doc-bundle
# Using version ^2.12 for nelmio/api-doc-bundle
./composer.json has been updated
```
Nous pouvons maintenant activer le bundle :
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
            new Nelmio\ApiDocBundle\NelmioApiDocBundle(),
            new AppBundle\AppBundle(),
        ];

       // ...
        return $bundles;
    }
// ...
}
```
