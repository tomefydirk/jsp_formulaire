package Affichage;

public class Voiture extends Composant{
    private PersonneLocalise infoPers;
    private String nom_voiture;
    private int place;

    public PersonneLocalise getInfoPers() {
        return infoPers;
    }

    public void setInfoPers(PersonneLocalise infoPers) {
        this.infoPers = infoPers;
    }

    public String getNom_voiture() {
        return nom_voiture;
    }

    public void setNom_voiture(String nom_voiture) {
        this.nom_voiture = nom_voiture;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
