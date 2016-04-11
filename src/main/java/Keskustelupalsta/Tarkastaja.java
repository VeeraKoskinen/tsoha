/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;


import java.util.*;
import java.sql.*;
/**
 *
 * @author veerakoskinen
 */
public class Tarkastaja {
    private Database data;
    private KayttajaDao kayttajaDao;
    
    public Tarkastaja(Database data, KayttajaDao kd) {
        this.data = data;
        this.kayttajaDao = kd;
    }
    
    public Boolean rightToJoin(String kayttajanimike, String salasana) throws SQLException {
        
        for (Kayttaja k : kayttajaDao.findAll()) {
            if (k.getKayttajanimi().equals(kayttajanimike) || k.getSahkoposti().equals(kayttajanimike)) {
                if (k.getSalasana().equals(salasana)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Boolean moderaattori(int id) throws SQLException {
        if (kayttajaDao.findOne(id).getModeraattori() == 1) {
            return true;
        }
        return false;
    }
}
