package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Affichage.Voiture;
import mg.dirk.servlet.utils.RequestParser;

@WebServlet(name="test-parse",value="/test_parse")
public class TestParse extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestParser parser = new RequestParser(req);
        try {
            parser.getFromParameters(Voiture.class);     
        } catch (Exception e) {
        }
        
    }
    
}
