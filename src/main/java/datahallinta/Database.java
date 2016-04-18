/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahallinta;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author veerakoskinen
 */
public class Database <T> {
    private String databaseAddress;
    private boolean debug;
    
    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
        
        init();

    }
    
    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }
    
    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE IF EXISTS Viesti;");
        lista.add("DROP TABLE IF EXISTS Keskustelu;");
        lista.add("DROP TABLE IF EXISTS Alue;");
        lista.add("DROP TABLE IF EXISTS Kayttaja;");
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Kayttaja (id serial PRIMARY KEY, sahkoposti VARCHAR(100) UNIQUE, kayttajanimi VARCHAR(100) UNIQUE, salasana VARCHAR(100) NOT NULL, moderaattori integer)");
        lista.add("CREATE TABLE Alue (id serial PRIMARY KEY, otsikko varchar(100) NOT NULL);");
        lista.add("CREATE TABLE Keskustelu (id serial PRIMARY KEY, otsikko varchar(1000) NOT NULL, alue integer NOT NULL, FOREIGN KEY (alue) REFERENCES Alue(id));");
        lista.add("CREATE TABLE Viesti (id serial PRIMARY KEY, keskustelu integer NOT NULL, kayttaja integer NOT NULL, aika TIMESTAMP DEFAULT NOW(), viesti text NOT NULL, FOREIGN KEY (kayttaja) REFERENCES Kayttaja(id), FOREIGN KEY (keskustelu) REFERENCES Keskustelu(id));");
        // testidata
        lista.add("INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES ('mikko.supermallikas@kuukkeli.com', 'MikkoMallikas', 'mi84as', 1);");
        lista.add("INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES ('sanni.supermallikas@kuukkeli.com', 'SanniMallikas', 'sa91as', 0);");
        return lista;
    }
    
    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        
        lista.add("CREATE TABLE Kayttaja (id integer PRIMARY KEY, sahkoposti VARCHAR(100) UNIQUE, kayttajanimi VARCHAR(100) UNIQUE, salasana VARCHAR(100) NOT NULL, moderaattori integer)");
        lista.add("CREATE TABLE Alue (id integer PRIMARY KEY, otsikko varchar(100) NOT NULL);");
        lista.add("CREATE TABLE Keskustelu (id integer PRIMARY KEY, otsikko varchar(1000) NOT NULL, alue integer NOT NULL, FOREIGN KEY (alue) REFERENCES Alue(id));");
        lista.add("CREATE TABLE Viesti (id integer PRIMARY KEY, keskustelu integer NOT NULL, kayttaja integer NOT NULL, aika DATETIME DEFAULT(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), viesti text NOT NULL, FOREIGN KEY (kayttaja) REFERENCES Kayttaja(id), FOREIGN KEY (keskustelu) REFERENCES Keskustelu(id));");
        
        
        lista.add("INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES ('mikko.supermallikas@kuukkeli.com', 'MikkoMallikas', 'mi84as', 1);");
        lista.add("INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES ('sanni.supermallikas@kuukkeli.com', 'SanniMallikas', 'sa91as', 0);");
        
        return lista;
    }   
    
    public void setDebugMode(boolean d) {
        debug = d;
    }    
    
    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(
                    rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }
    
     private void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }
}
