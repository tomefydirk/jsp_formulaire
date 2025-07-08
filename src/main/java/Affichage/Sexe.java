package Affichage;

public class Sexe extends Deroulante {
    private String sexeCle;
    private String nom;

    public String getSexeCle() {
        return sexeCle;
    }

    public String getNom() {
        return nom;
    }

    public void setSexeCle(String sexeCle) {
        this.sexeCle = sexeCle;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Sexe() {
        String[] cle = { "0", "1" };
        String[] val = { "Vavy", "Lahy" };
        super.setCle(cle);
        super.setValeur(val);
    }

    // NOTE ato no mamadika cle ho lasa deroulante
    public void deroulanteParseFunc(String paramData) {
        int ind = 0;
        if (this.getCle() == null) {
            return;
        }
        for (int i = 0; i < this.getCle().length; i++) {
            if (paramData.trim() == this.getCle()[i]) {
                ind = i;
                break;
            }
        }
        this.setSexeCle(this.getCle()[ind]);
        this.setNom(this.getValeur()[ind]);
    }
}
