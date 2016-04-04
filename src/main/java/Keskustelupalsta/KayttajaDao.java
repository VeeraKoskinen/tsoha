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
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE id = ?");
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
      

    
    public List findAll() throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja");

            ResultSet rs = stmt.executeQuery();
            List<Kayttaja> kayttajat = new ArrayList<>();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
