<?php
include "../../controller/produitC.php";
include "../../model/produit.php";

/*
include '..controller/produitC.php';
include '../model/produit.php';
*/
// create client
$produit = null;

// create an instance of the controller

$produitC = new produitC();
if (
    isset($_POST["nom"]) && isset($_POST["prix"]) && isset($_POST["remise"]) && isset($_POST["quantite"])
    ) {
        if (!empty($_POST['nom']) && !empty($_POST["prix"]) && !empty($_POST["remise"]) && !empty($_POST["quantite"]))
             {
                $produit = new produit(
                    null,
                    $_POST['nom'],
                    $_POST['prix'],
                    $_POST['remise'],
                    $_POST['quantite']
                );
                $produitC->addproduit($produit);
                print_r($_POST);
        header('Location:listproduit.php');
    }
}

?>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Produit </title>
</head>

<body>
    <a href="listproduit.php">Back to list </a>
    <hr>

    <form action="" method="POST">
        <table>
            <tr>
                <td><label for="nom">nom :</label></td>
                <td>
                    <input type="text" id="nom" name="nom" />
                </td>
            </tr>
            <tr>
                <td><label for="prix">prix:</label></td>
                <td>
                    <input type="number" id="prix" name="prix" />
                </td>
            </tr>
            <tr>
                <td><label for="remise">remise :</label></td>
                <td>
                    <input type="number" id="remise" name="remise" />
                </td>
            </tr>
            <tr>
                <td><label for="quantite">quantite :</label></td>
                <td>
                    <input type="number" id="quantite" name="quantite" />
                </td>
            </tr>
            <td>
                <input type="submit" value="Save">
            </td>
            <td>
                <input type="reset" value="Reset">
            </td>
        </table>

    </form>
</body>

</html>