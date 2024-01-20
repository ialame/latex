# Plateforme

Nous avons ci-dessous un tableau récapitulatif des différentes technologies et la version utilisée. Le système d'exploitation utilisé importe peu et vous pouvez donc utiliser celui de votre choix pour suivre le reste du cours.
Sur Windows, vous avez la suite [WAMP](http://www.wampserver.com/) et son équivalent [LAMP](https://doc.ubuntu-fr.org/lamp) sur Ubuntu.

| Technologie   | Version   | Exemples       |
| ------------- | --------- |--------------- |
| PHP           | 7.0.x     | 7.0.0, 7.0.3   |
| MySQL         | 5.7.x     | 5.7.0, 5.7.9   |
| Apache        | 2.4.x     | 2.4.0, 2.4.17  |


# Symfony Installer

La méthode recommandée pour installer un projet Symfony est d'utiliser [l'installateur](http://symfony.com/doc/current/book/installation.html#installing-the-symfony-installer). Cet utilitaire nous permettra d'installer Symfony avec la version que nous souhaitons. 

## Installation sur Linux

Il suffit de lancer dans la console:

```bash
 curl -LsS https://symfony.com/installer -o ~/bin/symfony
 chmod a+x ~/bin/symfony
```
Cela téléchargera l'installateur et le placera dans le répertoire bin de l'utilisateur connecté.


## Installation sous Windows:

Avant tout, il faut s'assurer que l'exécutable de PHP est bien disponible dans l'invite de commande. Des consignes d'installation sont disponibles sur [le site officiel de PHP](http://php.net/manual/fr/faq.installation.php#faq.installation.addtopath).
Ensuite, Il suffit exécuter dans l'invite de commande :

```bash
c:\> php -r "readfile('https://symfony.com/installer');" > symfony
```

Ensuite, il est judicieux de créer un fichier `symfony.bat` contenant `@php "%~dp0symfony" %*`. 

[[information]]
|Il est possible de placer les fichiers symfony et symfony.bat dans un même dossier que vous rajouter dans le PATH avec les variables d'environnement de Windows afin d'accéder à la commande partout.


Une fois l'installation finie, lancer la commande symfony pour vérifier le bon fonctionnement du tout.
```bash
symfony
# réponse attendue
 Symfony Installer (1.5.0)
 =========================

 This is the official installer to start new projects based on the
 Symfony full-stack framework.
```

# Composer

Nous utiliserons [Composer](https://getcomposer.org) pour rajouter de nouvelles dépendances à notre projet.

Il suffit d'utiliser l'installateur disponible sur [le site officiel](https://getcomposer.org/download/).

Pour tester le bon fonctionnement, il faut lancer la commande `composer`:
```bash
composer
# réponse attendue
#   ______
#  / ____/___  ____ ___  ____  ____  ________  _____
# / /   / __ \/ __ `__ \/ __ \/ __ \/ ___/ _ \/ ___/
#/ /___/ /_/ / / / / / / /_/ / /_/ (__  )  __/ /
#\____/\____/_/ /_/ /_/ .___/\____/____/\___/_/
#                    /_/             
```
	
# Installation de Git

Si vous  n'avez pas déjà Git sur votre machine, il va falloir l'installer car Composer peut être amené à l'utiliser pour télécharger des dépendances. L'installation est bien détaillée sur le site [Git SCM](https://git-scm.com/book/fr/v1/D%C3%A9marrage-rapide-Installation-de-Git).
Il faudra juste vous assurer que l’exécutable `git` est disponible dans votre path.