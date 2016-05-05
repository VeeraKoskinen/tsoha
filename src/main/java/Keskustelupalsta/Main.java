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
//        post("/uHup/alue/muokkaa", (req, res) -> {
//            alueDao.updateHeadline(Integer.parseInt(req.queryParams("alueid")),req.queryParams("muokkaus"));
//            res.redirect("/uHup/alue");
//            return "Muokkaus onnistui!";
//        });
        
        
        
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
         get("/uHup/hallitsemaailmaasi987654321/nyt", (req, res) -> {
            int id = req.session().attribute("kayttajaId");
            HashMap map = new HashMap<>(); 
            map.put("henkilo", kayttajaDao.findOne(id));
            map.put("kayttajat", kayttajaDao.findAll());
            return new ModelAndView(map, "hallintasivu");
        }, new ThymeleafTemplateEngine());
        post("/uHup/hallitsemaailmaasi987654321/nyt", (req, res) -> {  
            kayttajaDao.addNew(req.queryParams("sahkoposti"), req.queryParams("kayttajanimi"), req.queryParams("salasana"), Integer.parseInt(req.queryParams("moderaattori")));
            res.redirect("/uHup/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        post("/uHup/hallitsemaailmaasi987654321/nyt/poistakayttaja", (req, res) -> { 
            kayttajaDao.delete(Integer.parseInt(req.queryParams("kayttajanpoisto")));
            res.redirect("/uHup/hallitsemaailmaasi987654321/nyt");
            return "Lähetys onnistui!";
        });
        
        // kayttajan muokkaussivu
        get("/uHup/kayttajanmuokkaus/:kayttajaid", (req, res) -> {
            int sessioId = req.session().attribute("kayttajaId");
            int kayttajaId = Integer.parseInt(req.params(":kayttajaid"));
            HashMap map = new HashMap<>();
            map.put("henkilo", kayttajaDao.findOne(sessioId));
            map.put("kayttaja",kayttajaDao.findOne(kayttajaId));
            return new ModelAndView(map, "kayttajamuokkaus"); 
        }, new ThymeleafTemplateEngine());
        post("/uHup/kayttajanmuokkaus/:kayttajaid", (req, res) -> {
            int id = Integer.parseInt(req.params(":kayttajaid"));
            kayttajaDao.updateKayttajanimi(req.queryParams("kayttajanimi"), id);
            res.redirect("/uHup/kayttajanmuokkaus/" + id);
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttajanmuokkaus/:kayttajaid/salasana", (req, res) -> {
            int id = Integer.parseInt(req.params(":kayttajaid"));
            kayttajaDao.updateSalasana(req.queryParams("salasana"), id);
            res.redirect("/uHup/kayttajanmuokkaus/" + id);
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttajanmuokkaus/:kayttajaid/sahkoposti", (req, res) -> {
            int id = Integer.parseInt(req.params(":kayttajaid"));
            kayttajaDao.updateSahkoposti(req.queryParams("sahkoposti"), id);
            res.redirect("/uHup/kayttajanmuokkaus/" + id);
            return "Lähetys onnistui!";
        });
        post("/uHup/kayttajanmuokkaus/:kayttajaid/moderaattori", (req, res) -> {
            int id = Integer.parseInt(req.params(":kayttajaid"));
            kayttajaDao.updateModeraattori(Integer.parseInt(req.queryParams("moderaattori")), id);
            res.redirect("/uHup/kayttajanmuokkaus/" + id);
            return "Lähetys onnistui!";
        });
    
    }   
    
}
