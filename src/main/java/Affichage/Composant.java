package Affichage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import mg.dirk.csv.annotations.SkipDeserialization;
import mg.dirk.csv.annotations.SkipSerialization;
import mg.dirk.reflections.ReflectUtils;

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
            Composant cmp = (Composant) toSerialize.getConstructor().newInstance();
            // if (cmp instanceof Deroulante) {
            // html += ((Deroulante) cmp).construireDeroulanteComposant("");
            // } else {
            html += (cmp).construireHtmlInsertComposant();
            // }
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
                Composant instance = (Composant) type.getConstructor().newInstance();
                if (instance instanceof Deroulante) {
                    html += ((Deroulante) instance).construireDeroulanteComposant(fieldName);
                } else {
                    html += "</br>";
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

    @SuppressWarnings("unchecked")
    private static <T extends Object> String constructHtmlNestedHeader(Class<T> class1, String prefix) {
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

        for (Field field : ReflectUtils.getFieldsWithExcludedAnnotations(class1, SkipSerialization.class)) {
            if (ReflectUtils.isSerdable(field.getType())) {
                html += String.format("<th>%s%s</th>", maybePrefix, field.getName());
            } else if (!field.getType().isArray()) {
                html += constructHtmlNestedHeader(field.getType(), String.format("%s%s", maybePrefix, field.getName()));
            }
        }

        return html;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Object> String construireTableLigne(T data)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String html = "";

        Class<T> dataClass = (Class<T>) data.getClass();
        for (Field field : ReflectUtils.getFieldsWithExcludedAnnotations(dataClass, SkipSerialization.class)) {
            Class<?> fieldType = field.getType();
            if (ReflectUtils.isSerdable(fieldType)) {
                html += String.format("<td>%s</td>",
                        ReflectUtils.formatToString(ReflectUtils.getFieldGetter(dataClass, field).invoke(data)));
            } else if (!fieldType.isArray()) {
                html += construireTableLigne(ReflectUtils.getFieldGetter(dataClass, field).invoke(data));
            }
        }

        return html;
    }

    public static <T extends Object> String construireHtmlTable(List<T> data)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        if (data != null && data.size() > 0) {
            String htmlTable = "";
            htmlTable += "<table border='1'>\n";
            htmlTable += "<tr>\n";

            // Ajouter les en-têtes de colonnes
            htmlTable += constructHtmlNestedHeader(data.get(0).getClass(), null);
            htmlTable += "</tr>\n";

            // Ajouter les lignes de données
            for (int i = 0; i < data.size(); i++) {
                htmlTable += "<tr>\n";
                htmlTable += construireTableLigne(data.get(i));

                htmlTable += "</tr>\n";
            }
            htmlTable += "</table>";

            return htmlTable;
        }
        return "";
    }

    public String construireHtmlTable()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return Composant.construireHtmlTable(getData());
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
