Avec la gestion des relations entre ressources, la méthode de suppression des lieux doit être revue. En effet, vu qu'un lieu peut avoir des prix, nous devons nous assurer qu'à sa suppression tous les prix qui lui sont associés le seront aussi. Sans cela, la clé étrangère empêcherait toute suppression d'un lieu ayant des prix.

La modification à effectuer reste cependant assez minime.
```php
<?php
# src/AppBundle/Controller/PlaceController.php

namespace AppBundle\Controller;

// ...

class PlaceController extends Controller
{
// ...

     /**
     * @Rest\View(statusCode=Response::HTTP_NO_CONTENT, serializerGroups={"place"})
     * @Rest\Delete("/places/{id}")
     */
    public function removePlaceAction(Request $request)
    {
        $em = $this->get('doctrine.orm.entity_manager');
        $place = $em->getRepository('AppBundle:Place')
                    ->find($request->get('id'));
        /* @var $place Place */

        if (!$place) {
            return;
        }

        foreach ($place->getPrices() as $price) {
            $em->remove($price);
        }
        $em->remove($place);
        $em->flush();
    }
// ...
}
```