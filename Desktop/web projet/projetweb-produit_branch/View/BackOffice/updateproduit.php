<?php
include "../../controller/produitC.php";

include '../../model/produit.php';

// create client
$produit = null;
// create an instance of the controller
$produitC = new produitC();
if (isset($_POST["nom"]) && isset($_POST["prix"]) && isset($_POST["remise"])){
    $nom = $_POST["nom"];
    $prix = $_POST["prix"];
    $remise = $_POST["remise"];
    $quantite = $_POST["quantite"];
    if (!empty($nom) &&!empty($prix) &&!empty($remise) &&!empty($quantite)){
        $produit = new produit(null, $nom, $prix, $remise, $quantite);
        var_dump($produit);
        $produitC->updateproduit($produit, $_GET['id_produit']);
        header('Location:listproduit.php');
}
}


?>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Display</title>
</head>

<body>
    <button><a href="listproduit.php">Back to list</a></button>
    <hr>

    <?php
    if (isset($_GET['id_produit'])) {
        $oldproduit = $produitC->showproduit($_GET['id_produit']);
        
    ?>

        <form action="" method="POST">
            <table>
            <tr>
                    <td><label for="id_produit">id_produit :</label></td>
                    <td>
                        <input type="number" id="id_produit" name="id_produit" value="<?php echo $_GET['id_produit'] ?>" readonly />
                    </td>
                </tr>
                <tr>
                    <td><label for="nom">nom :</label></td>
                    <td>
                        <input type="text" id="nom" name="nom" value="<?php echo $oldproduit['nom'] ?>" />
                    </td>
                </tr>
                <tr>
                    <td><label for="prix">prix:</label></td>
                    <td>
                        <input type="number" id="prix" name="prix" value="<?php echo $oldproduit['prix'] ?>" />
                    </td>
                </tr>
                <tr>
                    <td><label for="remise">remise :</label></td>
                    <td>
                        <input type="number" id="remise" name="remise" value="<?php echo $oldproduit['remise'] ?>" />
                    </td>
                </tr>
                <tr>
                    <td><label for="quantite">quantite :</label></td>
                    <td>
                        <input type="number" id="quantite" name="quantite" value="<?php echo $oldproduit['quantite'] ?>" />
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
    <?php
    }
    ?>
</body>

</html>