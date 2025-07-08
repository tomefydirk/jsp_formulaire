<%@ page import="java.util.Vector" %>
<%@ page import="Affichage.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Page 2</title>
</head>
<body>
<h1>Reflection / Generalise
</h1>
<br/>
<%
// Personne p = new Personne();
  /*  Vector tableau = new Vector();
    tableau.add(new Personne("Jean","Jaques",  30));
    tableau.add(new Personne("Marie","Jeanne",  25));
    tableau.add(new Personne("Paul","Phoenix", 40));*/
//Liste l = new Liste(tableau);
//Localite loc = new Localite();
//PersonneLocalise pl = new PersonneLocalise();
//    Voiture v = new Voiture();
//out.println(pl.construireHtmlInsertComposant());
out.println(Composant.construireClassForm(Personne.class, "persone-import"));
//out.println(loc.construireHtmlInsertComposant());
//out.println(l.construireHtmlTable());
%>
<a href="./personne-table.jsp">table</a>
</body>
</html>