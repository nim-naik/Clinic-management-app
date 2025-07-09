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
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }
    
    public void addPatient(){
        System.out.println("Enter Patient Name");
        String name = scanner.next();
        System.out.println("Enter Patient Age");
        int age = scanner.nextInt();
        System.out.println("Enter Patient Gender: Female / Male");
        String gender = scanner.next();
        
        
        try {
         String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         preparedStatement.setString(1, name);
         preparedStatement.setInt(2, age);
         preparedStatement.setString(3, gender);
         int affectedRows = preparedStatement.executeUpdate();
         if(affectedRows>0){
             System.out.println("Patient added successfully.");
         }
         else{
             System.out.println("Failed to add patient.");
         }
            
        }catch(Exception e) {
            System.out.println("Error occured.");
        }
    }
    
      public void viewPatient(){
 
        try {
         String query = "select * from patients";
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery();
         System.out.println("Patients: ");
         System.out.println("+------------+--------------------+----------+------------+");
         System.out.println("| Patient Id | Name               | Age      | Gender     |");
         System.out.println("+------------+--------------------+----------+------------+");
         while (resultSet.next()) {
               int id = resultSet.getInt("id");
               String name = resultSet.getString("name");
               int age = resultSet.getInt("age");
               String gender = resultSet.getString("gender");
               System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender);
               System.out.println("+------------+--------------------+----------+------------+");
                
         }
            
        }catch(Exception e) {
            System.out.println("Error occured.");
        }
    } 
      
    public boolean checkPatient(int id){
    String query = "select * from patients where id =?";
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
