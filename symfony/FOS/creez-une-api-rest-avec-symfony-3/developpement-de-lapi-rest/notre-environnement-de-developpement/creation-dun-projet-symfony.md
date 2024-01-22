# Utilisons l'installateur de Symfony

## Création du projet

Maintenant que nous avons un environnement de développement bien configuré, il ne reste plus qu'à créer un nouveau projet basé sur Symfony 3.

```bash
symfony new rest_api 3.1
# Réponse attendue
Preparing project...
OK  Symfony 3.1.1 was successfully installed.
```

Après un long moment de chargement, nous avons un dossier nommé `rest_api` contenant une installation toute neuve de Symfony 3.1.X (3.1.1 pour mon cas). Ça sera notre point de départ pour nos développements.
Testons la création du projet en lançant le serveur de développement intégré à PHP:

```bash
cd rest_api
php bin/console server:run

[OK] Server running on http://127.0.0.1:8000
// Quit the server with CONTROL-C.
```

Accédez à l'URL de vérification de Symfony et effectuez les correctifs si nécessaires http://localhost:8000/config.php.

![Page de vérification de la configuration de Symfony](http://zestedesavoir.com/media/galleries/3183/06a08293-1a2a-43cf-b320-3252a09443c1.png)


## Problème de certificats SSL

Sous Windows, il est possible de rencontrer des problèmes de certificats. 

```
[GuzzleHttp\Exception\RequestException]
cURL error 60: SSL certificate problem: unable to get local issuer certificate
```

Pour corriger ce problème, il faut s'assurer que l'extension OpenSSL est activée et définir le chemin d'accès vers le fichier contenant les certificats racines.

Une liste de certificats est disponible sur [https://curl.haxx.se/ca/cacert.pem](https://curl.haxx.se/ca/cacert.pem). Pensez à le télécharger.

Commençons par identifier le fichier de configuration de PHP. Avec WAMP, ce fichier se situe dans le dossier d'installation (par exemple, `D:\wamp64\bin\php\php7.0.0\php.ini`). Il suffit maintenant de vérifier que la ligne concernant l'extension OpenSSL n'est pas commenté et de spécifier le chemin du fichier contenant les certificats racines.

```ini
extension=php_openssl.dll
[openssl]
openssl.cafile=D:\wamp64\bin\php\php7.0.0\cacert.pem
;openssl.capath=
```

# Configuration de Apache

Durant le reste du cours, j'accéderai à l'API en utilisant un virtual host apache personnalisé. Notre API sera donc disponible sur l'URL `http://rest-api.local`.

Pour ce faire, il faut [configurer un virtual host apache](http://symfony.com/doc/current/cookbook/configuration/web_server_configuration.html) et modifier le fichier host du système pour renseigner l'URL `rest-api.local`.

[[information]]
|Le virtual host fourni est compatible avec Windows. Penser à remplacer `D:/wamp64/www/rest_api` par votre dossier d'installation et à effectuer les adaptations nécessaires pour un autre système d'exploitation.

```xml
<VirtualHost *:80>
    ServerName rest-api.local

    DocumentRoot "D:/wamp64/www/rest_api/web"

    <Directory "D:/wamp64/www/rest_api/web">
      DirectoryIndex app_dev.php
      Require all granted
      AllowOverride None

      RewriteEngine On
      RewriteCond %{REQUEST_FILENAME} -f
      RewriteRule ^ - [L]
      RewriteRule ^ app_dev.php [L]
    </Directory>

    # Ajuster le chemin vers les fichiers de logs à votre convenance
    ErrorLog logs/rest-api-error.log 
    CustomLog logs/rest-api-access.log combined
</VirtualHost>
```

[[attention]]
|Le mode rewrite d'apache est obligatoire pour que ce virtual host fonctionne. Notez aussi que les requêtes seront redirigées directement vers `app_dev.php` avec cette configuration.

Ensuite sous Windows, éditez en tant qu'administrateur le fichier `C:\Windows\System32\drivers\etc\hosts` et sous Linux, éditez avec les droits root le fichier `/etc/hosts`, et rajouter une ligne:
```
127.0.0.1 rest-api.local
::1 rest-api.local
```
Sous Windows, l’astuce consiste à lancer votre éditeur de texte en tant qu'administrateur avant d'ouvrir le fichier à éditer.

Maintenant en accédant à l'URL [http://rest-api.local/](http://rest-api.local/), nous atteignons notre page web de bienvenue.

![Page d'accueil de notre futur site](http://zestedesavoir.com/media/galleries/3183/0b0c54a1-8272-4159-bd51-eacbe8aa8a0f.png)
