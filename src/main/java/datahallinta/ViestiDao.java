/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahallinta;

import datahallinta.*;
import tauluoliot.*;
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
            PreparedStatement stmt = connection.prepareStatement("SELECT ka.kayttajanimi AS kirjoittaja, v.* FROM Viesti v, Kayttaja ka WHERE v.id = ? AND v.kayttaja = ka.id");
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
            String kirjoittaja = rs.getString("kirjoittaja");
            v.setKirjoittaja(kirjoittaja);

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
    public ArrayList findAll(int keskustelu) throws SQLException {

        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT ka.kayttajanimi AS kirjoittaja, v.* FROM Alue a, Keskustelu k, Viesti v, Kayttaja ka WHERE a.id = k.alue AND k.id = v.keskustelu AND v.keskustelu = ? AND v.kayttaja = ka.id ORDER BY v.aika;");
            stmt.setInt(1, keskustelu);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, List<Viesti>> keskustelunViestit = new HashMap<>();

            ArrayList<Viesti> viestit = new ArrayList<>();

            while (rs.next()) {

                Integer id = rs.getInt("id");
                //tuleeko ongelma timestampista
                Timestamp aika = rs.getTimestamp("aika");
                String viesti = rs.getString("viesti");

                Viesti v = new Viesti(id, viesti, aika);
                
                String kirjoittaja = rs.getString("kirjoittaja");
                v.setKirjoittaja(kirjoittaja);
                v.setKayttaja(kayttajaDao.findOne(rs.getInt("kayttaja")));
                viestit.add(v);

                Integer keskustelunaloitus = rs.getInt("keskustelu");
                if (!keskustelunViestit.containsKey(keskustelunaloitus)) {
                    keskustelunViestit.put(keskustelu, new ArrayList<>());
                }
                keskustelunViestit.get(keskustelunaloitus).add(v);
            }

            rs.close();
            stmt.close();

            ArrayList<Keskustelu> keskustelut = this.keskusteluDao.pureFindAll();
            for (Keskustelu k : keskustelut) {
                if (!keskustelunViestit.containsKey(k.getId())) {
                    continue;
                }

                for (Viesti viesti : keskustelunViestit.get(k.getId())) {
                    viesti.setKeskustelu(k);
                }
            }

            return viestit;
//
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Viesti WHERE id = ?;");
            stmt.setObject(1, key);
            stmt.executeUpdate();
            stmt.close();
            connection.close();
        }
    }

    public void addNew(String viesti, int kayttaja, int keskustelu) throws SQLException {
        Connection connection = data.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti (viesti, kayttaja, keskustelu) VALUES(?, ?, ?);");
        stmt.setString(1, viesti);
        stmt.setInt(2, kayttaja);
        stmt.setInt(3, keskustelu);
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

}
