Le sérialiseur natif de Symfony est disponible depuis les toutes premières versions du framework. Cependant, les fonctionnalités supportées par celui-ci étaient assez basique.

Par exemple, les groupes de sérialisation - permettant entre autres de gérer les références circulaires - n'ont été supportés qu'à partir de la version 2.7 [sortie en 2015](http://symfony.com/blog/symfony-2-7-0-released). La sérialisation des dates PHP (*DateTime* et *DateTimeImmutable*) n'a été supporté qu'avec la version 3.1 [sortie en 2016](http://symfony.com/blog/symfony-3-1-0-released).

Pour pallier à ce retard, un bundle a été développé pour la gestion de la sérialisation dans Symfony : *JMSSerializerBundle*. Il permet d'intégrer la librairie [JMSSerializer](http://jmsyst.com/libs/serializer) et est très largement utilisé dans le cadre du développement d'une API avec Symfony.
