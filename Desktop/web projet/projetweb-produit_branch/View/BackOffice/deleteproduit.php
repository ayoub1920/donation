<?php
echo("deleting");
include '../../controller/produitC.php';
$produitC = new produitC();
$produitC->deleteproduit($_GET["id_produit"]);
header('Location:listproduit.php');

