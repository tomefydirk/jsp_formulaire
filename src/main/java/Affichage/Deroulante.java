package Affichage;

public class Deroulante extends Composant{
    private String[] cle;
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

    @Override
    public String construireHtmlInsertComposant()throws Exception{
        String html = "";
        if(cle!=null){
            html+="<select name = '"+this.getClass().getName()+"'>";
            html+="<option value='%'>Tous</option>";
            for(int i = 0;i< cle.length;i++){
                html+="<option value='"+getCle()[i]+"'>"+getValeur()[i]+"</option>";
            }
            html+="</select>";
        }
        return html;
    }
}
