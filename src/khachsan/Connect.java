/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khachsan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Pham Duc Luong
 */
public class Connect {
    public String url=null;
    public Connection conn = null; 
    public Connect(String url1){
      try{
          url=url1;
          Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
          conn = DriverManager.getConnection(url);
      }catch(Exception ex){
          ex.printStackTrace();
      }
    
    }
    public ResultSet ExcuteQuery(String u) throws Exception{
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery(u);
        return rs;
        
        
    }
    public int ExcuteUpdate(String u) throws Exception{
        Statement st=conn.createStatement();
        int kq=st.executeUpdate(u);
        return kq;
    }
    public void close()throws Exception{
        conn.close();
        
    }
}
