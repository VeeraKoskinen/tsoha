/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tauluoliot;

import tauluoliot.Kayttaja;
import java.util.Date;

/**
 *
 * @author veerakoskinen
 */
public class Viesti {

    private int id;
    private Kayttaja kayttaja;
    private Keskustelu keskustelu;
    private String viesti;
    private Date lahetysaika;
    private String kirjoittaja;

    public Viesti() {
    }

    public Viesti(int id, String viesti, Date paiva) {
        this.id = id;
        this.lahetysaika = paiva;
        this.viesti = viesti;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public void setKayttaja(Kayttaja kayttaja) {
        this.kayttaja = kayttaja;
    }
    
    public Kayttaja getKayttaja() {
        return kayttaja;
    }
    
    public int getKayttajaId() {
        return kayttaja.getId();
    }
    
    public void setKeskustelu(Keskustelu keskustelu) {
        this.keskustelu = keskustelu;
    }
    
    public Keskustelu getKeskustelu() {
        return keskustelu;
    } 
    
    public void setLahetysaika(Date lahetysaika) {
        this.lahetysaika = lahetysaika;
    }

    public Date getLahetysaika() {
        return lahetysaika;
    }
    
    public void setViesti(String viesti) {
        this.viesti = viesti;
    }
    
    public String getViesti() {
        return viesti;
    }
    
    public void setKirjoittaja(String kirjoittaja) {
        this.kirjoittaja = kirjoittaja;
    }
    
    public String getKirjoittaja() {
        return kirjoittaja;
    }
}
