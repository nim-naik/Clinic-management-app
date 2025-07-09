/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clinicmanagement;

/**
 *
 * @author Nimisha Naik
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;

public class ClinicManagement {

    /**
     * @param args the command line arguments
     */
    private static final String url = "jdbc:mysql://localhost:3306/hospital?useSSL=true&serverTimezone=UTC";
    private static final String user = "root"; 
    private static final String password = "Nimisha@1234"; 
         
    public static void main(String[] args) {
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println("Driver not found.");
        }
        
        Scanner scanner = new Scanner(System.in);
        
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Patient patient = new Patient(connection, scanner);
            Doctors doctors = new Doctors(connection);
            
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Check Appointment");
                System.out.println("6. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                
                switch(choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                        
                    case 2:
                        patient.viewPatient();
                        System.out.println();
                        break;
                        
                    case 3:
                        doctors.viewDoctors();
                        System.out.println();
                        break;
                        
                    case 4:
                        bookAppointment(patient, doctors, connection, scanner);
                        System.out.println();
                        break;
                        
                    case 5:
                        checkAppointment(patient, doctors, connection, scanner);
                        System.out.println();
                        break;    
                        
                    case 6:
                        System.out.println("Thank you for using our system.");
                        return;
                        
                    default: 
                        System.out.println("Enter valid choice");
                        System.out.println();
                             
                }
            }
            //connection.close();
        } catch (SQLException e) {
            System.out.println("Error occured.");
        }
    }
    public static void bookAppointment(Patient patient, Doctors doctors, Connection connection, Scanner scanner) {
    System.out.println("Enter Patient id.");
    int patientID = scanner.nextInt();
    System.out.println("Enter Doctor id.");
    int doctorID = scanner.nextInt();
    System.out.println("Enter appointment date (YYYY-MM-DD):");
    String appointmentDateStr = scanner.next();
    System.out.println("Enter appointment time (HH:MM):");
    String appointmentTimeStr = scanner.next();
    if (appointmentTimeStr.length() == 5) {
    appointmentTimeStr += ":00"; 
}

    java.sql.Date appointmentDate = null;
    java.sql.Time appointmentTime = null;
    try {
        appointmentDate = java.sql.Date.valueOf(appointmentDateStr);
        appointmentTime = java.sql.Time.valueOf(appointmentTimeStr);
    } catch (IllegalArgumentException e) {
        System.out.println("Invalid date or time format. Please enter date as YYYY-MM-DD and time as HH:MM.");
        return;
    }

    if (patient.checkPatient(patientID)) {
        if (doctors.checkDoctor(doctorID)) {
            if (checkDoctorAvailability(doctorID, appointmentDate, appointmentTime, connection)) {
                try {
                    String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setDate(3, appointmentDate);
                    preparedStatement.setTime(4, appointmentTime);
                    int rowsInserted = preparedStatement.executeUpdate();

                    if (rowsInserted > 0) {
                        System.out.println("Appointment booked successfully.");
                    } else {
                        System.out.println("Appointment booking failed.");
                    }
                } catch (Exception e) {
                    System.out.println("Error occurred: " + e.getMessage());
                }
            } else {
                System.out.println("Doctor is not available at that date and time.");
            }
        } else {
            System.out.println("Doctor is not available.");
        }
    } else {
        System.out.println("Please add patient by selecting add patient.");
    }
}
    
public static boolean checkDoctorAvailability(int doctorID, java.sql.Date appointmentDate, java.sql.Time appointmentTime, Connection connection) {
    String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ?";
    try {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, doctorID);
        preparedStatement.setDate(2, appointmentDate);
        preparedStatement.setTime(3, appointmentTime);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            if(count == 0){
              return true;
            }else{
              return false;
            }
        }
    } catch (Exception e) {
        System.out.println("Error occurred: " + e.getMessage());
    }
    return false;
}

static void checkAppointment(Patient patient, Doctors doctors, Connection connection, Scanner scanner) {
    System.out.println("Enter Patient id.");
    int patientID = scanner.nextInt();

    if (!patient.checkPatient(patientID)) {
        System.out.println("Patient not found. Please add the patient first.");
        return;
    }

    String query = "SELECT a.id, a.appointment_date, a.appointment_time, d.name AS doctor_name " +
                   "FROM appointments a " +
                   "JOIN doctors d ON a.doctor_id = d.id " +
                   "WHERE a.patient_id = ? " +
                   "ORDER BY a.appointment_date, a.appointment_time";

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setInt(1, patientID);
        ResultSet resultSet = preparedStatement.executeQuery();

        boolean hasAppointments = false;

        System.out.println("+------------+------------+----------+--------------------------+");
        System.out.println("| ID         | Date       | Time     | Doctor Name              |");
        System.out.println("+------------+------------+----------+--------------------------+");

        while (resultSet.next()) {
            hasAppointments = true;
            int appointmentId = resultSet.getInt("id");
            Date appointmentDate = resultSet.getDate("appointment_date");
            Time appointmentTime = resultSet.getTime("appointment_time");
            String doctorName = resultSet.getString("doctor_name");

            System.out.printf("| %-10d | %-10s | %-8s | %-24s |\n",
                appointmentId, appointmentDate.toString(), appointmentTime.toString(), doctorName);
        }
        System.out.println("+------------+------------+----------+--------------------------+");

        if (!hasAppointments) {
            System.out.println("No appointments found for this patient.");
        }

    } catch (Exception e) {
        System.out.println("Error occurred: " + e.getMessage());
    }
}

}
   


