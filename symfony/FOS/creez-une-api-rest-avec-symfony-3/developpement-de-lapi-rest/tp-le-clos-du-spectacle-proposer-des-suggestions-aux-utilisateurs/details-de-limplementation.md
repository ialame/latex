Comme pour la liste des types de tarifs, nous disposons d'une liste de préférences et de thèmes prédéfinis :

- Art (art) ;
- Architecture (architecture) ;
- Histoire (history) ;
- Sport (sport) ;
- Science-fiction (science-fiction).

Une préférence associée à un utilisateur doit avoir 3 attributs :

- id : représente l'identifiant unique de la préférence utilisateur (auto-incrémenté) ;
- name : une des valeurs parmi la liste des préférences prédéfinies ;
- value : un entier désignant le niveau de préférence sur 10.

Un thème lié à un lieu doit avoir 3 attributs :

- id : représente l'identifiant unique du thème (auto-incrémenté) ;
- name : une des valeurs parmi la liste des thèmes prédéfinies ;
- value : un entier désignant le niveau du thème sur 10.

Une préférence associée à un utilisateur doit avoir une relation bidirectionnelle avec cet utilisateur et idem pour les lieux.

Une même préférence ne peut pas être associée deux fois à un même utilisateur ou un même lieu. (ex : un utilisateur ne peut pas avoir 2 fois la préférence art) et idem pour les lieux.

Il faudra 2 tables (donc 2 entités distinctes) :

- preferences (entité ***Preference***) pour stocker les préférences utilisateurs ;
- themes (entité ***Theme***) pour stocker les thèmes sur les lieux.

Il faudra 3 appels API :

- un permettant d'ajouter une préférence pour un utilisateur avec sa valeur ;
- un permettant d'ajouter un thème à un lieu avec sa valeur ;
- un pour récupérer les suggestions d'un utilisateur.

[[information]]
|Une ressource REST n'est pas forcément une entité brute de notre modèle de données. Nous pouvons utiliser un appel GET sur l'URL rest-api.local/users/1/suggestions pour récupérer la liste des suggestions pour l'utilisateur 1.

Une fois que les préférences et les thèmes seront rajoutés, les appels de listing des utilisateurs et des lieux doivent remonter respectivement les informations sur les préférences et les informations sur les thèmes. Il faudra donc penser à gérer les références circulaires.

À vous de jouer !
