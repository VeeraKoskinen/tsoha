/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author veerakoskinen
 */
public class Kayttaja {
    
    private int id;
    private String kayttajanimi;
    private String salasana;
    private String sahkoposti;
    
    public Kayttaja() {
        
    }
    
    public Kayttaja(int id, String kayttajanimi, String salasana, String sPosti) {
        this.id = id;
        this.kayttajanimi = kayttajanimi;
        this.sahkoposti = sPosti;
        this.salasana = salasana;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSalasana() {
        return this.salasana;
    }
    
    public void setSalasana(String sana) {
        this.salasana = sana;
    }
    
//    public String vaihdaSalasana(String uusiSalasana, String vahvistus) {
//        if (uusiSalasana.length() > 6 && uusiSalasana.equals(vahvistus)) {
//            salasana = uusiSalasana;
//            return "Salasanasi vaihdettiin onnistuneesti!";
//        }
//            return "Tarkista että kirjoitit riittävän pitkän salasanan."; 
//    }
    
    public String getKayttajanimi() {
        return this.kayttajanimi;
    }
    
    public void setKayttajanimi(String kayttajanimi) {
        this.kayttajanimi = kayttajanimi;
    }
    
    public String getSahkoposti() {
        return this.sahkoposti;
    }
    
    public void setSahkoposti(String sahkoposti) {
        this.sahkoposti = sahkoposti;
    }
    
}
