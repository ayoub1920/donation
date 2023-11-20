<?php
include "../../controller/produitC.php";

$c = new produitC();
$tab = $c->listproduit();
?>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Produits</title>
    <style>

        
body {
    font-family: 'Arial', sans-serif;
    margin: 0;
    padding: 0;
    background-image: url('pp15.jpeg'); /* Ajoutez le chemin de votre image */
    background-size: cover;
    background-position: center;
    color: #333;
}
body {
      margin: 0;
      padding: 0;
      height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background-size: cover;
      transition: background-image 1s ease-in-out;
    }
background-color: rgba(51, 51, 51, 0.8){
    color: #fff;
    position: fixed;
    top: 0;
    right: 0;
    height: 100%;
    width: 100px; /* Largeur de la barre latérale */
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-around;
    padding: 20px 0;
}

#cart-btn {
    background-color: #3498db;
    color: #fff;
    border: none;
    padding: 10px;
    cursor: pointer;
}

#cart-btn:hover {
    background-color: #2980b9;
}

header {
    background-color: rgba(51, 51, 51, 0.8); /* Ajout d'une opacité */
    color: #fff;
    padding: 10px 0;
    text-align: center;
}

#main-content {
    margin-top: 20px;
}

h1, h2 {
    color: #fff; /* Couleur du texte dans l'en-tête */
}

/* Tableau styles */
table {
    border-collapse: collapse;
    width: 70%;
    margin: 20px auto;
    background-color: rgba(255, 255, 255, 0.8); /* Ajout d'une opacité */
}

th, td {
    border: 1px solid #ddd;
    padding: 12px;
    text-align: left;
}

th {
    background-color: #f2f2f2;
}

/* Hover effect on rows */
tr:hover {
    background-color: #f5f5f5;
}

/* Link styles */
a {
    text-decoration: none;
    color: #3498db;
}

a:hover {
    color: #2980b9;
}

/* Footer styles */
footer {
    background-color: rgba(51, 51, 51, 0.8); /* Ajout d'une opacité */
    color: #fff;
    text-align: center;
    padding: 10px 0;
    position: fixed;
    bottom: 0;
    width: 100%;
}
</style>
</head>
<script>
  // Attendre 3 secondes avant de changer l'image de fond
  setTimeout(function() {
    document.body.style.backgroundImage = "url('pp13.jpeg')";
  }, 3000);
</script>

<body>

    <header>
        <h1>Liste des Produits</h1>
    </header>

    <section id="main-content">
        <h2><a href="addproduit.php">Ajouter un Produit</a></h2>

        <table>
            <thead>
                <tr>
                    <th>Id Produit</th>
                    <th>Nom</th>
                    <th>Prix</th>
                    <th>Remise</th>
                    <th>Quantité</th>
                    <th>Modifier</th>
                    <th>Supprimer</th>
                </tr>
            </thead>
            <aside id="sidebar">
        <button id="cart-btn">Panier</button>
    </aside>

            <tbody>
                <?php foreach ($tab as $produit) { ?>
                    <tr>
                        <td><?= $produit['id_produit']; ?></td>
                        <td><?= $produit['nom']; ?></td>
                        <td><?= $produit['prix']; ?></td>
                        <td><?= $produit['remise']; ?></td>
                        <td><?= $produit['quantite']; ?></td>
                        <td><a href="updateproduit.php?id_produit=<?= $produit['id_produit']; ?>">Modifier</a></td>
                        <td><a href="deleteproduit.php?id_produit=<?= $produit['id_produit']; ?>">Supprimer</a></td>
                    </tr>
                <?php } ?>
            </tbody>
        </table>
    </section>

    <footer>
        <p>&copy; 2023 Notre Boutique de vente en ligne. Tous droits réservés.</p>
    </footer>

</body>

</html>

