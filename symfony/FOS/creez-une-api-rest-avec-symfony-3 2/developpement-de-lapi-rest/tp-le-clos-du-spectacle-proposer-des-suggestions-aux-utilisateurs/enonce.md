Afin de gérer les suggestions, nous partons sur un design simple. Dans l'application, nous aurons une notion de préférences et de thèmes. Chaque utilisateur pourra choisir un ou plusieurs préférences avec une note sur 10. Et de la même façon, un lieu sera lié à un ou plusieurs thèmes avec une note sur 10.

Un lieu sera suggéré à un utilisateur si au moins une préférence de l'utilisateur correspond à un des thèmes du lieu et que le niveau de correspondance est supérieur ou égale à 25.

[[information]]
| Le niveau de correspondance est une valeur calculée qui nous permettra de quantifier à quel point **un lieu pourrait intéresser un utilisateur**. La méthode de calcul est détaillée ci-dessous.

Pour un utilisateur donné, il faut d'abord prendre toutes ses préférences.
Ensuite **pour chaque lieu** enregistré dans l'application, si une des préférences de l'utilisateur correspond au thème du lieu, il faudra calculer le produit : **valeur de la préférence de l'utilisateur * valeur du thème du lieu**.

La somme de tous ces produits représente le *niveau de correspondance* pour ce lieu .

Un exemple vaut mieux que mille discours :
Un utilisateur a comme préférences (art, 5), (history, 8) et (architecture, 2). Un lieu 1 a comme thèmes (architecture, 3), (sport, 2), (history, 3). et un lieu 2 a comme thèmes (art, 3), (science-fiction, 2).

Pour le lieu 1, nous avons 2 thèmes qui correspondent à ses préférences: history et architecture.

->

|                  | history | architecture|
|------------------|---------|-------------| 
|utilisateur       | 8       |      2      |
|lieu 1            | 4       |      3      |

<-

La valeur de correspondance est donc : $$8 * 4 + 2 * 3 = 32 + 6 = 38$$
38 est supérieur à 25 donc c'est une suggestion valide.

Pour le lieu 2, nous avons un seul thème qui correspond : art.

->

|                  |  art | 
|------------------|------|
|utilisateur       | 5    | 
|lieu 2            | 3    |

<-

La valeur de correspondance est donc : $$5 * 3 = 15$$
15 étant inférieur à 25 donc ce n'est pas une suggestion valide.
