<?php
class produit
{
    private ?int $id_produit = null;
    private ?string $nom = null;
    private ?int $prix= null;
    private ?int $remise = null;
    private ?int $quantite= null;

    public function __construct($id = null, $n, $p, $r, $q)
    {
        $this->id_produit= $id;
        $this-> nom= $n;
        $this->prix = $p;
        $this->remise = $r;
        $this->quantite = $q;
    }


    public function getid_produit()
    {
        return $this->id_produit;
    }


    public function getnom()
    {
        return $this->nom;
    }


    public function setnom($nom)
    {
        $this->nom = $nom;

        return $this;
    }


    public function getprix()
    {
        return $this->prix;
    }


    public function setprix($prix)
    {
        $this->prix = $prix;

        return $this;
    }


    public function getremise()
    {
        return $this->remise;
    }


    public function setremise($remise)
    {
        $this->remise = $remise;

        return $this;
    }


    public function getquantite()
    {
        return $this->quantite;
    }


    public function setquantite($quantite)
    {
        $this->quantite = $quantite;

        return $this;
    }
}

