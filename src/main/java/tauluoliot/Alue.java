/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tauluoliot;

import java.util.Date;

/**
 *
 * @author veerakoskinen
 */
public class Alue {
    private String otsikko;
    private int id;
    private Date viimeisinViesti;
    private int viestimaara;
    
    public Alue() {        
    }
    
    public Alue(int id, String otsikko) {
        this.id = id;
        this.otsikko = otsikko;
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
