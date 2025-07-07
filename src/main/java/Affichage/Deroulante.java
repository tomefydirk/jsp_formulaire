package Affichage;

import mg.dirk.csv.annotations.SkipDeserialization;
import mg.dirk.csv.annotations.SkipSerialization;

public class Deroulante extends Composant {
    @SkipDeserialization
    @SkipSerialization
    private String[] cle;
    @SkipDeserialization
    @SkipSerialization
    private String[] valeur;

    public String[] getCle() {
        return cle;
    }

    public void setCle(String[] cle) {
        this.cle = cle;
    }

    public String[] getValeur() {
        return valeur;
    }

    public void setValeur(String[] valeur) {
        this.valeur = valeur;
    }

    public String construireDeroulanteComposant(String name) throws Exception {
        String html = "";
        if (cle != null) {
            html += "<select name = '" + name + "'>";
            html += "<option value='%'>Tous</option>";
            for (int i = 0; i < cle.length; i++) {
                html += "<option value='" + getCle()[i] + "'>" + getValeur()[i] + "</option>";
            }
            html += "</select>";
        }
        return html;
    }

    @Override
    public String construireHtmlInsertComposant() throws Exception {
        return this.construireDeroulanteComposant(this.getClass().getName());
    }

}
