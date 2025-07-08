package Affichage.importp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Affichage.Personne;
import mg.dirk.csv.CSVUtils;
import mg.dirk.servlet.utils.RequestParser;

@WebServlet(name = "persone-serve", value = "/persone-import")
public class PersonneServelet extends HttpServlet {
    public static final String personeCSV = "./personne.csv";

    @Override
    public void init() throws ServletException {

        super.init();
        File file = new File(personeCSV);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestParser parser = new RequestParser(req);
        try {
            PrintWriter writer = resp.getWriter();
            for (Map.Entry<String, String[]> entry : parser.getRequest().getParameterMap().entrySet()) {
                writer.print(String.format("%s: ", entry.getKey()));
                for (String str : entry.getValue()) {
                    writer.print(String.format("%s, ", str));
                }
                writer.println();
            }
            Personne persone = parser.getFromParameters(Personne.class);

            List<Personne> voitures;
            try {
                voitures = CSVUtils.deserializeFile(Personne.class, personeCSV);
            } catch (Exception e) {
                voitures = new ArrayList<>();
            }

            voitures.add(persone);

            CSVUtils.saveToFile(voitures, personeCSV);

        } catch (Exception e) {

            e.printStackTrace(resp.getWriter());
        }
    }
}
