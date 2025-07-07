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
        <!-- NOTE Tonga dia mamorona formulaire par rapport amin'ilay Classe -->
        <%= Composant.construireClassForm(Voiture.class, "voiture/import.do") %>
    </body>

    </html>