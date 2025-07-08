<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="Affichage.*" %>
<%@ page import="Affichage.importp.*" %>
<%@ page import="mg.dirk.csv.CSVUtils" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
    <body>
        <%
            // Mamaky ilay fichier `VoitureServelet.voitureCSV`
            List<Personne> voitures = CSVUtils.deserializeFile(Personne.class, PersonneServelet.personeCSV);
            // Affichage tableau
            out.println(Composant.construireHtmlTable(voitures));
        %>
    </body>
</html>