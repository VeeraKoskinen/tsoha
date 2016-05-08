/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahallinta;

import datahallinta.Dao;
import tauluoliot.Kayttaja;
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
public class KayttajaDao implements Dao<Kayttaja, Integer> {

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
            int mod = rs.getInt("moderaattori");
            k.setModeraattori(mod);

            rs.close();
            stmt.close();

            return k;
        }
    }

    public Kayttaja findOneWithUsername(String key) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE kayttajanimi = ?;");
            stmt.setString(1, key);

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
            int mod = rs.getInt("moderaattori");
            k.setModeraattori(mod);

            rs.close();
            stmt.close();

            return k;
        }
    }

//    public String moderaattoriFindOne(String kayttajanimi) throws SQLException {
//        try (Connection connection = data.getConnection()) {
//            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE kayttajanimi = ?;");
//            stmt.setObject(1, kayttajanimi);
//
//            ResultSet rs = stmt.executeQuery();
//            boolean hasOne = rs.next();
//            if (!hasOne) {
//                return null;
//            }
//
//            Integer id = rs.getInt("id");
//            String salasana = rs.getString("salasana");
//            String nimi = rs.getString("kayttajanimi");
//            String sahkoposti = rs.getString("sahkoposti");
//
//            Kayttaja k = new Kayttaja(id, nimi, salasana, sahkoposti);
//
//            rs.close();
//            stmt.close();
//            
//            if (k.getModeraattori() == 1) {
//                return "Kayttajanimi: " + k.getKayttajanimi() + "\n Salasana" + k.getSalasana() + "\n Sahkoposti: " + k.getSahkoposti() + "\n ID: " + k.getId() + "Moderaattori: KYLLÄ";
//            }
//
//            return "Kayttajanimi: " + k.getKayttajanimi() + "\n Salasana" + k.getSalasana() + "\n Sahkoposti: " + k.getSahkoposti() + "\n ID: " + k.getId() + "Moderaattori: EI";
//        }
//       
//    }
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
                Kayttaja k = new Kayttaja(id, kayttajanimi, salasana, sahkoposti);
                int mod = rs.getInt("moderaattori");
                k.setModeraattori(mod);
                kayttajat.add(k);
            }

            rs.close();
            stmt.close();

            return kayttajat;
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {

        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM Viesti WHERE kayttaja = ?;");
            stmt1.setObject(1, key);
            stmt1.executeUpdate();
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Kayttaja WHERE id = ?;");
            stmt.setObject(1, key);
            stmt.executeUpdate();
        }
    }

    public void addNew(String sahkoposti, String kayttajanimi, String salasana, int moderaattori) throws SQLException {
        if (sahkoposti != null && kayttajanimi != null && salasana != null && sahkoposti.length() < 100 && kayttajanimi.length() < 100 && salasana.length() < 100) {
            Connection connection = data.getConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES(?, ?, ?, ?);");
            stmt.setString(1, sahkoposti);
            stmt.setString(2, kayttajanimi);
            stmt.setString(3, salasana);
            stmt.setInt(4, moderaattori);
            stmt.executeUpdate();

            stmt.close();
            connection.close();
        }
    }

    public void addNewWithKey(int key, String sahkoposti, String kayttajanimi, String salasana, int moderaattori) throws SQLException {
        if (sahkoposti != null && kayttajanimi != null && salasana != null && sahkoposti.length() < 100 && kayttajanimi.length() < 100 && salasana.length() < 100) {
            Connection connection = data.getConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Kayttaja (id, sahkoposti, kayttajanimi, salasana, moderaattori) VALUES(?, ?, ?, ?, ?);");
            stmt.setInt(1, key);
            stmt.setString(2, sahkoposti);
            stmt.setString(3, kayttajanimi);
            stmt.setString(4, salasana);
            stmt.setInt(5, moderaattori);
            stmt.executeUpdate();

            stmt.close();
            connection.close();
        }
    }

    public void updateKayttajanimi(String kayttajanimi, int id) throws SQLException {
        try (Connection connection = data.getConnection()) {
            if (kayttajanimi != null) {
                PreparedStatement stmt1 = connection.prepareStatement("UPDATE Kayttaja SET kayttajanimi= ? WHERE id = ?;");
                stmt1.setObject(1, kayttajanimi);
                stmt1.setObject(2, id);
                stmt1.executeUpdate();
            }
        }
    }

    public void updateSahkoposti(String sahkoposti, int id) throws SQLException {
        try (Connection connection = data.getConnection()) {
            if (sahkoposti != null) {
                PreparedStatement stmt1 = connection.prepareStatement("UPDATE Kayttaja SET sahkoposti= ? WHERE id = ?;");
                stmt1.setObject(1, sahkoposti);
                stmt1.setObject(2, id);
                stmt1.executeUpdate();
            }
        }
    }

    public void updateSalasana(String salasana, int id) throws SQLException {
        try (Connection connection = data.getConnection()) {
            if (salasana != null) {
                PreparedStatement stmt1 = connection.prepareStatement("UPDATE Kayttaja SET salasana = ? WHERE id = ?;");
                stmt1.setObject(1, salasana);
                stmt1.setObject(2, id);
                stmt1.executeUpdate();
            }
        }
    }

    public void updateModeraattori(int mod, int id) throws SQLException {
        try (Connection connection = data.getConnection()) {
            PreparedStatement stmt1 = connection.prepareStatement("UPDATE Kayttaja SET moderaattori= ? WHERE id = ?;");
            stmt1.setObject(1, mod);
            stmt1.setObject(2, id);
            stmt1.executeUpdate();
        }
    }

}
