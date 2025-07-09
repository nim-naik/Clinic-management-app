/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinicmanagement;

/**
 *
 * @author Nimisha Naik
 */

import java.sql.*;

public class Doctors {
    private Connection connection;

    public Doctors(Connection connection) {
        this.connection = connection;
    }
    
      public void viewDoctors(){
 
        try {
         String query = "select * from doctors";
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery();
         System.out.println("Doctors: ");
         System.out.println("+------------+--------------------+------------------+");
         System.out.println("| Doctor Id  | Name               | Specialization   |");
         System.out.println("+------------+--------------------+------------------+");
         while (resultSet.next()) {
               int id = resultSet.getInt("id");
               String name = resultSet.getString("name");
               String specialization = resultSet.getString("specialization");
               System.out.printf("| %-10s | %-18s | %-16s |\n", id, name, specialization);
               System.out.println("+------------+--------------------+------------------+");    
         }
            
        }catch(Exception e) {
            System.out.println("Error occured.");
        }
    } 
      
    public boolean checkDoctor(int id){
    String query = "select * from doctors where id =?";
        try {
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         preparedStatement.setInt(1, id);
         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
               return true;
         }
         else{
             return false;
         }
            
        }catch(Exception e) {
            System.out.println("Error occured.");
        }
        return false;
    }
}

