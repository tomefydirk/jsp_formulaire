<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="Affichage.*" %>
    <!DOCTYPE html>
    <html>

    <head>
        <title>Import generalize</title>
    </head>

    <body>
        <h1>
            Reflect gen
        </h1>
        <br />
     
        <%= Composant.construireClassForm(Voiture.class, "voiture-import") %>
    </body>

    </html>