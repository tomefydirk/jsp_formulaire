<%@ page import="Affichage.*" %>
<h1>
Coucou
</h1>

<div>
      <% 

              String g =  Composant.construireClassForm(Voiture.class,"/test_parse");
              out.println(g);  
      
      %>  
</div>