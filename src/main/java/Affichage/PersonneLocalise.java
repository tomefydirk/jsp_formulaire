package Affichage;

public class PersonneLocalise extends Composant{
    private Personne infopers;
    private Localite loc;

    public Personne getInfopers() {
        return infopers;
    }

    public void setInfopers(Personne infopers) {
        this.infopers = infopers;
    }

    public Localite getLoc() {
        return loc;
    }

    public void setLoc(Localite loc) {
        this.loc = loc;
    }
}
