/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Keskustelupalsta;

import java.sql.SQLException;

/**
 *
 * @author veerakoskinen
 */
public interface Dao <T, K> {
    T findOne(K key) throws SQLException;
   
    void delete(K key) throws SQLException;
    
}
