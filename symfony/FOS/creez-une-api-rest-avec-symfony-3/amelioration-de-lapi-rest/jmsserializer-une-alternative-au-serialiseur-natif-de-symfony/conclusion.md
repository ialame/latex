L'intégration de *JMSSerializerBundle* avec *FOSRestBundle* est aussi simple qu'avec le sérialiseur natif de Symfony.
En effet, *FOSRestBundle* nous offre une interface unique et s'adapte au sérialiseur mis à sa disposition.

En plus, le bundle [*JMSSerializerBundle*](http://jmsyst.com/bundles/JMSSerializerBundle) supporte beaucoup de fonctionnalités que nous n'avons pas abordées (gestion des versions, propriétés virtuelles, etc.). 

Vous avez pu remarquer que *JMSSerializer* nécessite un peu plus de configuration que le sérialiseur natif de Symfony. Par contre, le travail fourni pour obtenir un résultat correct est très rapidement rentabilisé vu que *JMSSerializerBundle* s’intègre facilement avec beaucoup d'autres bundles de Symfony.

Nous aurons d'ailleurs l'occasion d'exploiter ce bundle dans le chapitre sur la documentation.