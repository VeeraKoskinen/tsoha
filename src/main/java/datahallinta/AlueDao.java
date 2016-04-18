/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahallinta;

import datahallinta.Dao;
import tauluoliot.Alue;
import java.sql.Connection;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;

/**
 *
 * @author veerakoskinen
 */
public class AlueDao implements Dao<Alue, Integer> {

    private Database data;

    public AlueDao(Database data) {
        this.data = data;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE id = ?;");
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
        return findAll(0);
    }

    public ArrayList findAll(int offset) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT a.id AS ID, a.otsikko AS Otsikko, COUNT(v.id) AS Maara, MAX(v.aika) AS Paiva FROM Alue a LEFT JOIN Keskustelu k  ON a.id = k.alue LEFT JOIN Viesti v ON k.id=v.keskustelu GROUP BY a.id ORDER BY Paiva DESC LIMIT 10 OFFSET ?;");
            stmt.setInt(1, offset * 10);
            
            ResultSet rs = stmt.executeQuery();
            
            Map<Integer, List<Alue>> palstanAlueet = new HashMap<>();
//
            ArrayList<Alue> alueet = new ArrayList<>();

            while (rs.next()) {
                Integer id = rs.getInt("ID");
                String otsikko = rs.getString("Otsikko");

                Alue ka = new Alue(id, otsikko);

                alueet.add(ka);
                Integer maara = rs.getInt("Maara");
                java.sql.Date paiva = rs.getDate("Paiva");

                ka.setViestimaara(maara);
                ka.setViimeisinViesti(paiva);

            }

            rs.close();
            stmt.close();

            return alueet;
        }   
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Alue WHERE id = ?;");
            stmt.setObject(1, key);
            stmt.executeUpdate();
        }
    }
    
    public void addNew(String otsikko) throws SQLException {
        Connection connection = data.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue (otsikko) VALUES(?);");
        stmt.setString(1, otsikko);
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }

}
