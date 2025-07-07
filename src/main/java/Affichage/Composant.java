package Affichage;

import java.lang.reflect.Field;
import java.util.Vector;

import mg.dirk.csv.annotations.SkipDeserialization;
import mg.dirk.csv.annotations.SkipSerialization;

public class Composant {
    @SkipDeserialization
    @SkipSerialization
    private Vector<Object> data;

    public Vector<Object> getData() {
        return data;
    }

    public void setData(Vector<Object> data) {
        this.data = data;
    }

    public static String construireClassForm(Class<? extends Object> toSerialize, String action) throws Exception {
        String html = "";
        html += String.format("<form method='POST' action='%s'>", action);
        if (Composant.class.isAssignableFrom(toSerialize)) {
            html += ((Composant) toSerialize.getConstructor().newInstance()).construireHtmlInsertComposant();
        } else {
            html += construireHtmlInsertComposantPriv(toSerialize);
        }
        html += String.format("<button type='submit'>Inserer</button>");
        html += "</form>";
        return html;
    }

    private static String construireHtmlInsertComposantPriv(Class<? extends Object> obj, String prefix)
            throws Exception {
        String html = "";
        boolean isPrefixUnvalid = prefix == null || prefix.trim().isEmpty();
        String maybePrefix;
        if (!isPrefixUnvalid) {
            if (prefix.endsWith(".")) {
                maybePrefix = prefix;
            } else {
                maybePrefix = String.format("%s.", prefix);
            }
        } else {
            maybePrefix = "";
        }
        Field[] fields = obj.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            Class<?> type = f.getType();
            String fieldName = String.format("%s%s", maybePrefix, f.getName());
            html += "<label>" + fieldName + "</label> : ";
            if (Composant.class.isAssignableFrom(type)) {
                html += "</br>";
                Composant instance = (Composant) type.getConstructor().newInstance();
                if (instance instanceof Deroulante) {
                    ((Deroulante) instance).construireDeroulanteComposant(fieldName);
                } else {
                    html += instance.construireHtmlInsertComposant(fieldName);
                }
            } else if (type.equals(String.class)) {
                html += "<input type='text' name='" + fieldName + "' />\n";
            } else if (type.equals(int.class) || type.equals(Integer.class)
                    || type.equals(double.class) || type.equals(Double.class)) {
                html += "<input type='number' name='" + fieldName + "' />\n";
            } else if (!type.isArray()) {
                html += construireHtmlInsertComposantPriv(obj, fieldName);
            }
            html += "</br>";
        }

        return html;
    }

    private static String construireHtmlInsertComposantPriv(Class<? extends Object> obj) throws Exception {
        return construireHtmlInsertComposantPriv(obj, null);
    }

    public String construireHtmlInsertComposant() throws Exception {
        return Composant.construireHtmlInsertComposantPriv(getClass());
    }

    public String construireHtmlInsertComposant(String prefix) throws Exception {
        return Composant.construireHtmlInsertComposantPriv(getClass(), prefix);
    }

    public String construireHtmlTable() {
        if (getData() != null && getData().size() > 0) {
            String htmlTable = "";
            htmlTable += "<table border='1'>\n";
            htmlTable += "<tr>\n";

            // Ajouter les en-têtes de colonnes
            Field[] tableau_champ = getData().get(0).getClass().getDeclaredFields();
            for (int i = 0; i < tableau_champ.length; i++) {
                htmlTable += "<th>";
                htmlTable += tableau_champ[i].getName();
                htmlTable += "</th>\n";
            }
            htmlTable += "</tr>\n";

            // Ajouter les lignes de données
            for (int i = 0; i < getData().size(); i++) {
                htmlTable += "<tr>\n";
                for (int j = 0; j < tableau_champ.length; j++) {
                    tableau_champ[j].setAccessible(true);
                    htmlTable += "<td>";
                    htmlTable += getValField(getData().get(i), tableau_champ[j]);
                    htmlTable += "</td>\n";

                }
                htmlTable += "</tr>\n";
            }
            htmlTable += "</table>";

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
            Object o = classe.getClass().getMethod(nomMethode).invoke(classe);
            if (o == null)
                return "";
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
