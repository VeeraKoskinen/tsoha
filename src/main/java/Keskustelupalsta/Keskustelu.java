/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import java.util.Date;

/**
 *
 * @author veerakoskinen
 */
public class Keskustelu {

    private String otsikko;
    private int id;
    private Alue alue;
    private Date viimeisinViesti;
    private int viestimaara;

    public Keskustelu() {
    }

    public Keskustelu(int id, String otsikko) {
        this.id = id;
        this.otsikko = otsikko;
        this.viestimaara = 0;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public void setAlue(Alue alue) {
        this.alue = alue;
    }
    
    public Alue getAlue() {
        return alue;
    }

    public void setViestimaara(int viestimaara) {
        this.viestimaara = viestimaara;
    }

    public int getViestimaara() {
        return viestimaara;
    }

    public void setViimeisinViesti(Date viimeisinViesti) {
        this.viimeisinViesti = viimeisinViesti;
    }

    public Date getViimeisinViesti() {
        return viimeisinViesti;
    }

}
