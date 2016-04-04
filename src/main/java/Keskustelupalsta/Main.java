/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import java.util.*;
import java.sql.*;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;


/**
 *
 * @author veerakoskinen
 */
public class Main {
    public static void main(String[] args) throws Exception {
        
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
         // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:keskustelupalsta.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        } 
    
        Database database = new Database(jdbcOsoite);
        database.setDebugMode(true);
        
        AlueDao alueDao = new AlueDao(database);
        KayttajaDao kayttajaDao = new KayttajaDao(database);
        KeskusteluDao keskusteluDao = new KeskusteluDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, keskusteluDao, kayttajaDao);
        
        
        //aloitussivu
        get("/tatti", (req, res) -> {
            HashMap map = new HashMap<>(); 
            return new ModelAndView(map, "aloitussivu");
        }, new ThymeleafTemplateEngine());
        post("/kanttarelli", (req, res) -> { 
            res.redirect("/kanttarelli");
            return "Kirjautuminen onnistui!";
        });
        
        
        //omaProfiili (aluelistaus, henkilot)
        get("/kanttarelli", (req, res) -> {
            HashMap map = new HashMap<>(); 
            return new ModelAndView(map, "aluelistaus");
        }, new ThymeleafTemplateEngine());
        
        //keskustelulistaus
        
        //viestilistaus
        
        // yksityinen keskustelu ??
        
        
    }    
    
}
