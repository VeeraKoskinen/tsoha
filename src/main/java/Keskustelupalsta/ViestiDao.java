/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 *
 * @author veerakoskinen
 */
public class ViestiDao implements Dao<Viesti, Integer> {

    private Database data;
    private KeskusteluDao keskusteluDao;
    private KayttajaDao kayttajaDao;

    public ViestiDao(Database data, KeskusteluDao keskusteluDao, KayttajaDao kayttajaDao) {
        this.data = data;
        this.kayttajaDao = kayttajaDao;
        this.keskusteluDao = keskusteluDao;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String viesti = rs.getString("viesti");
            Date aika = rs.getDate("aika");

            Viesti v = new Viesti(id, viesti, aika);

            Integer keskustelu = rs.getInt("keskustelu");
            Integer kayttaja = rs.getInt("kayttaja");

            rs.close();
            stmt.close();

            v.setKeskustelu(this.keskusteluDao.findOne(keskustelu));
            v.setKayttaja(this.kayttajaDao.findOne(kayttaja));
            return v;
        }
    }
    
//    public List findAll() throws SQLException {
//        return findAll(0);
//    }

    public ArrayList findAll(int offset) throws SQLException {

        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE keskustelu = ?");
            stmt.setInt(1, offset * 10);
            ResultSet rs = stmt.executeQuery();
            
            Map<Integer, List<Viesti>> keskustelunViestit = new HashMap<>();

            ArrayList<Viesti> viestit = new ArrayList<>();

            while (rs.next()) {

                Integer id = rs.getInt("id");
                //tuleeko ongelma timestampista
                Timestamp aika = rs.getTimestamp("aika");
                String viesti = rs.getString("viesti");

                Viesti v = new Viesti(id, viesti, aika);
                
                viestit.add(v);

                Integer keskustelu = rs.getInt("keskustelu");
                if (!keskustelunViestit.containsKey(keskustelu)) {
                    keskustelunViestit.put(keskustelu, new ArrayList<>());
                }
                keskustelunViestit.get(keskustelu).add(v);
            }

            rs.close();
            stmt.close();
            
            ArrayList<Keskustelu> keskustelut = this.keskusteluDao.findAll();
            for (Keskustelu keskustelu : keskustelut) {
                if (!keskustelunViestit.containsKey(keskustelu.getId())) {
                    continue;
                }

                for (Viesti viesti : keskustelunViestit.get(keskustelu.getId())) {
                    viesti.setKeskustelu(keskustelu);
                }
            }

            return viestit;
//
        }       
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
