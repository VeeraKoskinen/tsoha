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
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE id = ?");
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
       

 
    public ArrayList findAll() throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu");
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

                Integer alue = rs.getInt("alue");

                if (!alueenKeskustelut.containsKey(alue)) {
                    alueenKeskustelut.put(alue, new ArrayList<>());
                }
                alueenKeskustelut.get(alue).add(k);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
