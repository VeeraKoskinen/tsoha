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
public class AlueDao implements Dao<Alue, Integer>{
    private Database data;
    
    public AlueDao(Database data) {
        this.data = data;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE id = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String otsikko = rs.getString("otsikko");

            Alue a = new Alue(id, otsikko);

            rs.close();
            stmt.close();

            return a;
        }
    }
       

   
    public ArrayList findAll() throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue");

            ResultSet rs = stmt.executeQuery();
            ArrayList<Alue> alueet = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String otsikko = rs.getString("otsikko");

                alueet.add(new Alue(id, otsikko));
            }

            rs.close();
            stmt.close();

            return alueet;
        }
    }
       

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
