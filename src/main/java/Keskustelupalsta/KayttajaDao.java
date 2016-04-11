/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veerakoskinen
 */
public class KayttajaDao implements Dao<Kayttaja, Integer>{
    private Database data;
    
    public KayttajaDao(Database data) {
        this.data = data;
    }

    @Override
    public Kayttaja findOne(Integer key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE id = ?;");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String salasana = rs.getString("salasana");
            String kayttajanimi = rs.getString("kayttajanimi");
            String sahkoposti = rs.getString("sahkoposti");

            Kayttaja k = new Kayttaja(id, kayttajanimi, salasana, sahkoposti);

            rs.close();
            stmt.close();

            return k;
        }
    }
    
    public String moderaattoriFindOne(String kayttajanimi) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE kayttajanimi = ?;");
            stmt.setObject(1, kayttajanimi);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String salasana = rs.getString("salasana");
            String nimi = rs.getString("kayttajanimi");
            String sahkoposti = rs.getString("sahkoposti");

            Kayttaja k = new Kayttaja(id, nimi, salasana, sahkoposti);

            rs.close();
            stmt.close();
            
            if (k.getModeraattori() == 1) {
                return "Kayttajanimi: " + k.getKayttajanimi() + "\n Salasana" + k.getSalasana() + "\n Sahkoposti: " + k.getSahkoposti() + "\n ID: " + k.getId() + "Moderaattori: KYLLÄ";
            }

            return "Kayttajanimi: " + k.getKayttajanimi() + "\n Salasana" + k.getSalasana() + "\n Sahkoposti: " + k.getSahkoposti() + "\n ID: " + k.getId() + "Moderaattori: EI";
        }
       
    }
      

    
    public ArrayList<Kayttaja> findAll() throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja;");

            ResultSet rs = stmt.executeQuery();
            ArrayList<Kayttaja> kayttajat = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String kayttajanimi = rs.getString("kayttajanimi");
                String salasana = rs.getString("salasana");
                String sahkoposti = rs.getString("sahkoposti");

                kayttajat.add(new Kayttaja(id, kayttajanimi, salasana, sahkoposti));
            }

            rs.close();
            stmt.close();

            return kayttajat;
        }
    }
       

    @Override
    public void delete(Integer key) throws SQLException {
         try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Kayttaja WHERE id = ?;");
            stmt.setObject(1, key);
            stmt.executeUpdate();
        }    
    }
    
     public void addNew(String sahkoposti, String kayttajanimi, String salasana, int moderaattori) throws SQLException {
        Connection connection = data.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES(?, ?, ?, ?);");
        stmt.setString(1,sahkoposti );
        stmt.setString(2, kayttajanimi);
        stmt.setString(3, salasana);
        stmt.setInt(4, moderaattori);
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
}
