Nous avons déjà eu l'occasion de voir le nom *JMSSerializerBundle* dans les premières parties de ce cours.
Ce bundle permet d'inclure et de configurer la librairie PHP [jms/serializer](http://jmsyst.com/libs/serializer) dans Symfony.

Cette librairie présente beaucoup d'avantages :

- Elle est beaucoup plus mature que le sérialiseur de Symfony ;
- De par son ancienneté, elle est supportée par beaucoup de bundles et facilite donc l'interopérabilité entre les bundles et/ou composants que nous pouvons utiliser dans notre API ;
- Et pour finir, les ressources (documentation, cours etc.) sur cette librairie sont plus abondantes.

À l'installation de *FOSRestBundle*, nous étions obligés d'utiliser la version *2.0* afin de supporter pleinement le sérialiseur de Symfony. Mais avec *JMSSerializerBundle*, nous pourrons profiter de toutes les fonctionnalités de *jms/serializer* tout en utilisant une version de *FOSRestBundle* inférieure à la *2.0*.
