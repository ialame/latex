Force est de constater que le code dans nos contrôleurs est assez répétitifs. Toutes les réponses sont en JSON via l'objet ***JsonResponse***, la logique de formatage de celles-ci est dupliqué et toutes les routes suivent un même modèle.

Nous avons là un schéma classique de code facilement factorisable et justement Symfony nous propose beaucoup d'outils via les composants et les bundles afin de gérer ce genre de tâches courantes et/ou répétitifs.

Nous allons donc utiliser les avantages qu'offre le framework Symfony à travers le bundle *FOSRestBundle* afin de mieux gérer les problématiques d'implémentation liées au contrainte REST et gagner ainsi en productivité.