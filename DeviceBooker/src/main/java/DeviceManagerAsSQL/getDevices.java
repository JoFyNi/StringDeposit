package DeviceManagerAsSQL;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static DeviceManagerAsSQL.config.*;


public class getDevices {
    public static File ListFile = new File(ListPath);
    // Pfad zur JSON-Datei

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // from SQL DB
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setDevices(List<Device> devices) {
        devices.clear();  // Liste leeren, bevor wir neue Daten hinzufügen
        String query = "SELECT status, serviceTag, user, startDate, endDate FROM devices";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String status = resultSet.getString("status");
                String serviceTag = resultSet.getString("serviceTag");
                String user = resultSet.getString("user");
                String startDate = resultSet.getString("startDate");
                String endDate = resultSet.getString("endDate");

                Device device = new Device(status.equalsIgnoreCase("true"), serviceTag, user, startDate, endDate);
                devices.add(device);  // Füge das Gerät zur Liste hinzu
            }

            displayDevices(devices);  // Geräte in der Liste anzeigen

        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen der Geräte aus der Datenbank: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void setNewDevice(String serviceTag, String email, JSpinner startDate, JSpinner endDate) {
        System.out.println("Neue Buchung: " + serviceTag);
        String result = extractBeforeAt(email);
        String startDateFormat = formatDate(startDate);
        String endDateFormat = formatDate(endDate);

        String query = "UPDATE devices SET status = ?, user = ?, startDate = ?, endDate = ? WHERE serviceTag = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "false");  // Status auf 'false' setzen (nicht verfügbar)
            preparedStatement.setString(2, result);   // E-Mail-Benutzername
            preparedStatement.setString(3, startDateFormat);  // Startdatum
            preparedStatement.setString(4, endDateFormat);    // Enddatum
            preparedStatement.setString(5, serviceTag);  // Nach ServiceTag filtern

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                // Falls kein Datensatz aktualisiert wurde, füge einen neuen Datensatz hinzu
                insertNewDevice(serviceTag, result, startDateFormat, endDateFormat);
            } else {
                System.out.println("Gerät erfolgreich aktualisiert.");
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Aktualisieren der Geräteinformationen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertNewDevice(String serviceTag, String user, String startDate, String endDate) {
        String query = "INSERT INTO devices (status, serviceTag, user, startDate, endDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "false");  // Status auf 'false' setzen (nicht verfügbar)
            preparedStatement.setString(2, serviceTag);  // ServiceTag
            preparedStatement.setString(3, user);  // E-Mail-Benutzername
            preparedStatement.setString(4, startDate);  // Startdatum
            preparedStatement.setString(5, endDate);  // Enddatum

            preparedStatement.executeUpdate();
            System.out.println("Neues Gerät erfolgreich hinzugefügt.");

        } catch (SQLException e) {
            System.out.println("Fehler beim Einfügen eines neuen Geräts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void resetDevice(String serviceTag) {
        System.out.println("Rückgabe: " + serviceTag);

        String query = "UPDATE devices SET status = ?, user = ?, startDate = ?, endDate = ? WHERE serviceTag = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "true");  // Status zurücksetzen (verfügbar)
            preparedStatement.setString(2, "----");  // Benutzer auf '----' setzen
            preparedStatement.setString(3, "----");  // Startdatum auf '----' setzen
            preparedStatement.setString(4, "----");  // Enddatum auf '----' setzen
            preparedStatement.setString(5, serviceTag);  // Nach ServiceTag filtern

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Gerät erfolgreich zurückgesetzt.");
            } else {
                System.out.println("Gerät nicht gefunden.");
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Zurücksetzen des Geräts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //------------------------------------------------------------------------------------------------

    public static String formatDate(JSpinner DATE) {
        JComponent editor = new JSpinner.DateEditor(DATE, "dd.MM.yyyy");
        DATE.setEditor(editor);
        Date startDate = (Date) DATE.getValue();
        // Formatieren des Datums
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(startDate);
    }

    public static String extractBeforeAt(String input) {
        // Finde die Position des Zeichens '@'
        int atIndex = input.indexOf('@');

        // Falls das '@' im String vorhanden ist, extrahiere den Teil davor
        if (atIndex != -1) {
            return input.substring(0, atIndex);  // Extrahiere alles bis zum '@'
        } else {
            return null;  // Falls kein '@' im String vorhanden ist
        }
    }

    //------------------------------------------------------------------------------------------------
    public static void displayDevices(List<Device> devices) {
        for (Device device : devices) {
            System.out.println("Status: " + device.getStatus());
            System.out.println("Service Tag: " + device.getServiceTag());
            System.out.println("Benutzer: " + device.getBenutzer());
            System.out.println("Startdatum: " + device.getStartDatum());
            System.out.println("Enddatum: " + device.getEndDatum());
            System.out.println("----------------------------");
        }
    }
}