# Création d'un lieu avec deux prix de même type

Si nous essayons de créer un lieu avec des prix du même type, nous obtenons une erreur interne car il y a une contrainte d'unicité sur l'identifiant du lieu et le type du produit.

```php
<?php
# src/AppBundle/Entiy/Price.php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * @ORM\Entity()
 * @ORM\Table(name="prices",
 *      uniqueConstraints={@ORM\UniqueConstraint(name="prices_type_place_unique", columns={"type", "place_id"})}
 * )
 */
class Price
{
    // ...
}
```

Pour s'en convaincre, il suffit d'essayer de créer un nouveau lieu avec comme payload :

```json
{
    "name": "Arc de Triomphe",
    "address": " Place Charles de Gaulle, 75008 Paris",
    "prices": [
        {
            "type": "less_than_12",
            "value": 0.0
        },
        {
            "type": "less_than_12",
            "value": 0.0
        } 
    ]
}
```

La réponse est sans appel :

```json
{
  "code": 500,
  "message": "An exception occurred while executing 'INSERT INTO prices (type, value, place_id) VALUES (?, ?, ?)' with params [\"less_than_12\", 0, 10]:\n\nSQLSTATE[23000]: Integrity constraint violation: 1062 Duplicata du champ 'less_than_12-10' pour la clef 'prices_type_place_unique'"
}
```

Pour corriger le problème, nous allons créer une règle de validation personnalisée.

# Validation personnalisée avec Symfony

## Création de la contrainte

La contrainte est la partie la plus simple à implémenter. Il suffit d'une classe pour la nommer et d'un message en cas d'erreur.

```php
<?php
# src/AppBundle/Form/Validator/Constraint/PriceTypeUnique.php

namespace AppBundle\Form\Validator\Constraint;

use Symfony\Component\Validator\Constraint;

/**
 * @Annotation
 */
class PriceTypeUnique extends Constraint
{
    public $message = 'A place cannot contain prices with same type';
}
```

## Création du validateur

Une fois que nous avons une nouvelle contrainte, il reste à créer un validateur pour gérer cette contrainte. 

```php
<?php
# src/AppBundle/Form/Validator/Constraint/PriceTypeUniqueValidator.php

namespace AppBundle\Form\Validator\Constraint;

use Symfony\Component\Validator\Constraint;
use Symfony\Component\Validator\ConstraintValidator;

class PriceTypeUniqueValidator extends ConstraintValidator
{
    public function validate($prices, Constraint $constraint)
    {
        if (!($prices instanceof \Doctrine\Common\Collections\ArrayCollection)) {
            return;
        }

        $pricesType = [];

        foreach ($prices as $price) {
            if (in_array($price->getType(), $pricesType)) {
                $this->context->buildViolation($constraint->message)
                    ->addViolation();
                return; // Si il y a un doublon, on arrête la recherche
            } else {
                // Sauvegarde des types de prix déjà présents
                $pricesType[] = $price->getType();
            }
        }
    }
}
```

Le nom choisi n'est pas un hasard. Vu que la contrainte s'appelle ***PriceTypeUnique***, le validateur a été nommé  ***PriceTypeUniqueValidator*** afin d'utiliser les conventions de nommage de Symfony. Ainsi notre contrainte est validée en utilisant le validateur que nous venons de créer.

[[information]]
|Ce comportement par défaut peut être modifié en étendant la méthode `validatedBy` de la contrainte. [La documentation officielle](http://symfony.com/doc/current/cookbook/validation/custom_constraint.html) de Symfony apporte plus d'informations à ce sujet.

Pour utiliser notre nouvelle contrainte, nous allons modifier les règles de validation qui s'applique à un lieu :

```yaml
# src/AppBundle/Resources/config/validation.yml
AppBundle\Entity\Place:
    constraints:
        - Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity: name
    properties:
        name:
            - NotBlank: ~
            - Type: string
        address:
            - NotBlank: ~
            - Type: string
        prices:
            - Valid: ~
            - AppBundle\Form\Validator\Constraint\PriceTypeUnique: ~
```

En testant à nouveau la création d'un lieu avec deux prix du même type, nous obtenons une belle erreur de validation avec un message clair.

```json
{
  "code": 400,
  "message": "Validation Failed",
  "errors": {
    "children": {
      "name": [],
      "address": [],
      "prices": {
        "errors": [
          "A place cannot contain prices with same type"
        ],
        "children": [
          {
            "children": {
              "type": [],
              "value": []
            }
          },
          {
            "children": {
              "type": [],
              "value": []
            }
          }
        ]
      }
    }
  }
}
```
