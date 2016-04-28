/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahallinta;

import datahallinta.AlueDao;
import datahallinta.Dao;
import tauluoliot.Keskustelu;
import tauluoliot.Alue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author veerakoskinen
 */
public class KeskusteluDao implements Dao<Keskustelu, Integer> {
    private Database data;
    private AlueDao alueDao;
    
    public KeskusteluDao(Database data, AlueDao dao) {
        this.data = data;
        this.alueDao = dao;
    }

    @Override
    public Keskustelu findOne(Integer key) throws SQLException {
          try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE id = ?;");                                                              
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String otsikko = rs.getString("otsikko");

            Keskustelu k = new Keskustelu(id, otsikko);

            Integer alue = rs.getInt("alue");

            rs.close();
            stmt.close();

            k.setAlue(this.alueDao.findOne(alue));
            return k;
        }
    }
    
    public ArrayList pureFindAll() throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu;");
            
            ResultSet rs = stmt.executeQuery();
//
            Map<Integer, List<Keskustelu>> alueenKeskustelut = new HashMap<>();
//
            ArrayList<Keskustelu> keskustelut = new ArrayList<>();
//
            while (rs.next()) {

                Integer id = rs.getInt("id");
                String otsikko = rs.getString("otsikko");
//
                Keskustelu k = new Keskustelu(id, otsikko);
                keskustelut.add(k);
                
                Integer kAlue = rs.getInt("alue");

                if (!alueenKeskustelut.containsKey(kAlue)) {
                    alueenKeskustelut.put(kAlue, new ArrayList<>());
                }
                alueenKeskustelut.get(kAlue).add(k);
            }
            
            ArrayList<Alue> alueet = alueDao.findAll();
            for (Alue a : alueet) {
                if (!alueenKeskustelut.containsKey(a.getId())) {
                    continue;
                }

                for (Keskustelu keskustelu : alueenKeskustelut.get(a.getId())) {
                    keskustelu.setAlue(a);
                }
            }
            return keskustelut;
        }  
    }
       
    public ArrayList findAllTenFirst(int alue) throws SQLException {
        return findAll(0, alue);
    }
 
    public ArrayList findAll(int offset, int kAlue) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT k.id AS ID, k.otsikko AS Keskustelunaihe, k.alue AS AlueenId, COUNT(v.id) AS Maara, MAX(v.aika) AS Paiva FROM Keskustelu k LEFT JOIN Viesti v ON k.id = v.keskustelu WHERE k.alue = ? GROUP BY k.id ORDER BY Paiva DESC LIMIT 10 OFFSET ?;");            
                                                                
            stmt.setInt(1, kAlue);
            stmt.setInt(2, offset * 10);
            
            ResultSet rs = stmt.executeQuery();
//
            Map<Integer, List<Keskustelu>> alueenKeskustelut = new HashMap<>();
//
            ArrayList<Keskustelu> keskustelut = new ArrayList<>();
//
            while (rs.next()) {

                Integer id = rs.getInt("id");
                String otsikko = rs.getString("Keskustelunaihe");
//
                Keskustelu k = new Keskustelu(id, otsikko);
                keskustelut.add(k);

                Integer alue = rs.getInt("alueenId");

                if (!alueenKeskustelut.containsKey(alue)) {
                    alueenKeskustelut.put(alue, new ArrayList<>());
                }
                alueenKeskustelut.get(alue).add(k);
                
                Integer maara = rs.getInt("Maara");
                java.sql.Date paiva = rs.getDate("Paiva");

                k.setViestimaara(maara);
                k.setViimeisinViesti(paiva);
            }

            rs.close();
            stmt.close();
            ArrayList<Alue> alueet = alueDao.findAll();
            for (Alue a : alueet) {
                if (!alueenKeskustelut.containsKey(a.getId())) {
                    continue;
                }

                for (Keskustelu keskustelu : alueenKeskustelut.get(a.getId())) {
                    keskustelu.setAlue(a);
                }
            }

            return keskustelut;
        }
        
    }
        

    @Override
    public void delete(Integer key) throws SQLException {
           try (Connection connection = data.getConnection()) {
            PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM Viesti WHERE keskustelu = ?;");
            stmt1.setObject(1, key);
            stmt1.executeUpdate();
            PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM Keskustelu WHERE id = ?;");
            stmt2.setObject(1, key);
            stmt2.executeUpdate();
        }    
    }
    
    public void addNew(String otsikko, int alue) throws SQLException {
        Connection connection = data.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu (otsikko, alue) VALUES(?, ?);");
        
        stmt.setString(1, otsikko);
        stmt.setInt(2, alue);
        
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
        
    }
    
}
