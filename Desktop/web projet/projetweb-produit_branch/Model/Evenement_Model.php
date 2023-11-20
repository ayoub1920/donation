    <?php
class Evenement{
    public string $id_evenement;
    public string $nom;
    public string $Date_evenement;
    public string $lieu;
    /*-----------------------  constructor of "Evenement" ---------------------------*/
    public function __construct($id_ev, $nom_ev, $Date_ev, $lieu_ev){
        $this->id_evenement = $id_ev;
        $this->nom = $nom_ev;
        $this->Date_evenement = $Date_ev;
        $this->lieu = $lieu_ev;
    }
    public function getIdEvenement(){
        return $this->id_evenement;
    }
    public function getNomEvent(){
        return $this->nom;
    }
    public function setNomEvent($new_name){
        $this->nom = $new_name;
        return $this;
    }
    public function setDateEvenement($new_date_evenement){
         $this->Date_evenement = $this->$new_date_evenement;
         return $this;
    }
    public function getDate_evenement(){
        return $this->Date_evenement;
    }
    public function set_lieu($new_lieu){
        $this->lieu= $this->$new_lieu;
        return $this;
   }
   public function get_lieu(){
       return $this->lieu;
   }
}
?>