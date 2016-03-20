/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import java.util.*;
import java.sql.*;


/**
 *
 * @author veerakoskinen
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:testi.db");

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT 1");

        if(resultSet.next()) {
            System.out.println("Hei tietokantamaailma!");
        } else {
            System.out.println("Yhteyden muodostaminen epäonnistui.");
        }
    }    
    
}
