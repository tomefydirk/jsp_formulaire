package Affichage;

import java.lang.reflect.Field;
import java.util.Vector;

public class Composant {
    private Vector<Object> data;

    public Vector<Object> getData() {
        return data;
    }

    public void setData(Vector<Object> data) {
        this.data = data;
    }

    public String construireHtmlInsertComposant()throws Exception{
        String html = "";
        Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            Class<?> type = f.getType();
            html += "<label>" + f.getName() + "</label> : ";
            if(f.getType().getName().contains("Affichage.")){
                html+="</br>";
                Class<? extends Composant> classModel = (Class<? extends Composant>) Class.forName(f.getType().getName());
                Composant instance = classModel.newInstance();
                html += instance.construireHtmlInsertComposant();
            }
            if (type.equals(String.class)) {
                html += "<input type='text' name='" + f.getName() + "' />\n";
            }
            if (type.equals(int.class) || type.equals(Integer.class)
                    || type.equals(double.class) || type.equals(Double.class)) {
                html += "<input type='number' name='" + f.getName() + "' />\n";
            }

            html+="</br>";
        }

        return html;
    }

    public String construireHtmlTable(){
        if(getData()!=null && getData().size()>0) {
            String htmlTable = "";
            htmlTable += "<table border='1'>\n";
            htmlTable += "<tr>\n";

            // Ajouter les en-têtes de colonnes
            Field[] tableau_champ = getData().get(0).getClass().getDeclaredFields();
            for (int i = 0;i<tableau_champ.length;i++) {
                htmlTable += "<th>";
                htmlTable += tableau_champ[i].getName();
                htmlTable += "</th>\n";
            }
            htmlTable+="</tr>\n";

            // Ajouter les lignes de données
            for (int i = 0;i<getData().size();i++) {
                htmlTable+="<tr>\n";
                for (int j = 0;j<tableau_champ.length;j++) {
                    tableau_champ[j].setAccessible(true);
                    htmlTable+= "<td>";
                    htmlTable+=getValField(getData().get(i),tableau_champ[j]);
                    htmlTable+= "</td>\n";

                }
                htmlTable+="</tr>\n";
            }
            htmlTable+="</table>";

            return htmlTable;
        }
        return "";
    }

    public static String convertDebutMajuscule(String autre) {
        char[] c = autre.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    public static Object getValField(Object classe, Field f) {
        try {
            String nomMethode = "get" + convertDebutMajuscule(f.getName());
            Object o = classe.getClass().getMethod(nomMethode, null).invoke(classe, null);
            if (o == null)
                return "";
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
