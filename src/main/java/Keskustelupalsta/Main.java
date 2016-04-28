/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import datahallinta.Database;
import datahallinta.ViestiDao;
import datahallinta.KeskusteluDao;
import datahallinta.KayttajaDao;
import datahallinta.AlueDao;
import static spark.Spark.*;
import java.util.*;
import spark.ModelAndView;
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
        String jdbcOsoite = "jdbc:sqlite:taulut.db";
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
        
        Tarkastaja t = new Tarkastaja(database, kayttajaDao);
        
        //aloitussivu
        get("/uHup", (req, res) -> {
            
            HashMap map = new HashMap<>(); 
            
            return new ModelAndView(map, "aloitussivu");
        }, new ThymeleafTemplateEngine());
        post("/uHup", (req, res) -> { 
            if (t.rightToJoin(req.queryParams("kayttajanimi"), req.queryParams("salasana"))) {
                String nimi = req.queryParams("kayttajanimi");
                int id = kayttajaDao.findOneWithUsername(nimi).getId();
                req.session(true).attribute("kayttajaId",id);               
                res.redirect("/uHup/alue");
                return "Kirjautuminen onnistui!";
            }
                res.redirect("/uHup");
            return "Kirjautuminen epäonnistui!";
        });
        
        
        //aluelistaus
        get("/uHup/alue", (req, res) -> {
            int id = req.session().attribute("kayttajaId");
            HashMap map = new HashMap<>();
            if (t.moderaattori(id)) {
                map.put("moderaattori", kayttajaDao.findOne(id));
            }
            map.put("uloskirjaaja", kayttajaDao.findOne(id));
            map.put("kayttaja", kayttajaDao.findOne(id));
            map.put("alueet", alueDao.findAll());
            return new ModelAndView(map, "aluelistaus");
        }, new ThymeleafTemplateEngine());
        post("/uHup/alue", (req, res) -> { 
            alueDao.addNew(req.queryParams("Otsikko"));
            res.redirect("/uHup/alue");
            return "Lisäys onnistui!";
        });
        post("/uHup/alue/poista", (req, res) -> {
            alueDao.delete(Integer.parseInt(req.queryParams("poisto")));
            res.redirect("/uHup/alue");
            return "Poisto onnistui!";
        });
        
        
        //keskustelulistaus
        get("/uHup/alue/:id", (req, res) -> {
            int id1 = req.session().attribute("kayttajaId");
            int id2 = Integer.parseInt(req.params(":id"));
            HashMap map = new HashMap<>();
            if (t.moderaattori(id1)) {
                map.put("moderaattori", kayttajaDao.findOne(id1));
            }
            map.put("uloskirjaaja", kayttajaDao.findOne(id1));
            map.put("kayttaja", kayttajaDao.findOne(id1));
            map.put("alue", alueDao.findOne(id2));
            map.put("keskustelut", keskusteluDao.findAllTenFirst(id2));   
            return new ModelAndView(map, "keskustelulistaus");
        }, new ThymeleafTemplateEngine());
        post("/uHup/alue/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            keskusteluDao.addNew(req.queryParams("otsikko"), id);
            res.redirect("/uHup/alue/" + id);
            return "Lisäys onnistui!";
        });
        post("/uHup/alue/:alueid/poisto", (req, res) -> {
            int id = Integer.parseInt(req.params(":alueid"));
            keskusteluDao.delete(Integer.parseInt(req.queryParams("poisto")));
            res.redirect("/uHup/alue/" + id);
            return "Poisto onnistui!";
        });
        
        //viestilistaus
        get("/uHup/alue/:alueid/keskustelu/:id", (req, res) -> {
            int id1 = req.session().attribute("kayttajaId");
            int id2 = Integer.parseInt(req.params(":alueid"));
            int id3 = Integer.parseInt(req.params(":id"));
            HashMap map = new HashMap<>();
            map.put("uloskirjaaja", kayttajaDao.findOne(id1));
            map.put("kayttaja", kayttajaDao.findOne(id1));
            map.put("alue", alueDao.findOne(id2));
            map.put("keskustelu", keskusteluDao.findOne(id3));
            map.put("viestit", viestiDao.findAll(id3));          
            return new ModelAndView(map, "viestilistaus");
        }, new ThymeleafTemplateEngine());
        post("/uHup/alue/:alueid/keskustelu/:id", (req, res) -> {
            int id1 = req.session().attribute("kayttajaId");
            int id2 = Integer.parseInt(req.params(":alueid"));
            int id3 = Integer.parseInt(req.params(":id"));
            viestiDao.addNew(req.queryParams("viesti"), id1, id3);
            res.redirect("/uHup/alue/" + id2 + "/keskustelu/" + id3);
            return "Lähetys onnistui!";
        });
        post("/uHup/alue/:alueid/keskustelu/:id/poisto", (req, res) -> {
            int alueId = Integer.parseInt(req.params(":alueid"));
            int keskusteluId = Integer.parseInt(req.params(":id"));
            viestiDao.delete(Integer.parseInt(req.queryParams("poisto")));
            res.redirect("/uHup/alue/" + alueId + "/keskustelu/" + keskusteluId);
            return "Poisto onnistui!";
        });
        
        
        // hallintasivu
         get("/uHup/kayttaja/:kayttajaid/hallitsemaailmaasi987654321/nyt", (req, res) -> {
            int id = Integer.parseInt(req.params(":kayttajaid"));
            HashMap map = new HashMap<>(); 
            map.put("kayttaja", kayttajaDao.findOne(id));
            map.put("etsihenkilo", kayttajaDao.findOneWithUsername(req.queryParams("etsihenkilo")));
            return new ModelAndView(map, "hallintasivu");
        }, new ThymeleafTemplateEngine());
        post("/uHup/kayttaja/:kayttajaid/hallitsemaailmaasi987654321/nyt", (req, res) -> { 
            int id = Integer.parseInt(req.params(":kayttajaid")); 
            kayttajaDao.addNew(req.queryParams("sahkoposti"), req.queryParams("kayttajanimi"), req.queryParams("salasana"), Integer.parseInt(req.queryParams("moderaattori")));
            res.redirect("/uHup/kayttaja/" + id + "/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttaja/:kayttajaid/hallitsemaailmaasi987654321/nyt/poistakayttaja", (req, res) -> { 
            int id = Integer.parseInt(req.params(":kayttajaid"));
            kayttajaDao.delete(Integer.parseInt(req.queryParams("kayttajanpoisto")));
            res.redirect("/uHup/kayttaja/" + id + "/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttaja/:kayttajaid/hallitsemaailmaasi987654321/nyt/poistaalue", (req, res) -> { 
            int id = Integer.parseInt(req.params(":kayttajaid"));
            alueDao.delete(Integer.parseInt(req.queryParams("alueenpoisto")));
            res.redirect("/uHup/kayttaja/" + id + "/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttaja/:kayttajaid/hallitsemaailmaasi987654321/nyt/poistakeskustelu", (req, res) -> { 
            int id = Integer.parseInt(req.params(":kayttajaid"));
            keskusteluDao.delete(Integer.parseInt(req.queryParams("keskustelunpoisto")));
            res.redirect("/uHup/kayttaja/" + id + "/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttaja/:kayttajaid/hallitsemaailmaasi987654321/nyt/poistaviesti", (req, res) -> { 
            int id = Integer.parseInt(req.params(":kayttajaid"));
            viestiDao.delete(Integer.parseInt(req.queryParams("viestinpoisto")));
            res.redirect("/uHup/kayttaja/" + id + "/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        
    }    
    
}
