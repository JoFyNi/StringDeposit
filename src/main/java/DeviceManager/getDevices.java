package DeviceManager;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class getDevices {
    public static File ListFile = new File("C:\\Users\\j.nievelstein\\Java\\StringDeposit\\src\\main\\java\\DeviceManager\\list.txt");

    public static void setDevices(List<Device> devices) {
        String line = "";
        String SplitBy = ",";
        try (BufferedReader reader = new BufferedReader(new FileReader(ListFile))) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(SplitBy);

                // Konvertiere das erste Element in einen boolean-Wert
                boolean status = data[0].equalsIgnoreCase("true");  // Vergleiche auf "true", wenn es in der Datei als Text steht

                // Verwende den Konstruktor, um das Gerät zu erstellen
                Device device = new Device(status, data[1], data[2], data[3], data[4]);

                devices.add(device);  // Gerät zur Liste hinzufügen
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Debugging-Ausgabe, um zu überprüfen, ob die Geräte korrekt gelesen wurden
        displayDevices(devices);
    }


    public static void setNewDevice(String serviceTag, String email, JSpinner startDate, JSpinner endDate) {
        // Beispiel-Logik, um eine Buchung hinzuzufügen
        System.out.println("Neue Buchung: " + serviceTag);
        System.out.println("E-Mail: " + email);
        System.out.println("Startdatum: " + startDate.getValue());
        System.out.println("Enddatum: " + endDate.getValue());
        System.out.println("------------------------------");

        String result = extractBeforeAt(email);
        String startDateFormat = formatDate(startDate);
        String endDateFormat = formatDate(endDate);
        // -----------------------------------------------------------------------------
        List<String> lines = new ArrayList<>();
        String line;
        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(ListFile))) {
            // Datei Zeile für Zeile lesen
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // Überprüfe, ob die Zeile die gewünschte Variable enthält
                if (data[1].equals(serviceTag)) {
                    // Überschreibe data[2], data[3], data[4]
                    data[0] = "false";
                    data[2] = result;
                    data[3] = startDateFormat;
                    data[4] = endDateFormat;

                    // Erstelle die neue Zeile mit den geänderten Daten
                    line = String.join(",", data);
                    isUpdated = true;
                }
                // Füge die (aktualisierte oder unveränderte) Zeile zur Liste hinzu
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wenn die Datei aktualisiert wurde, schreibe sie neu
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ListFile))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();  // Zeilenumbruch hinzufügen
                }
                System.out.println("Datei erfolgreich aktualisiert.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Variable nicht gefunden oder keine Aktualisierung erforderlich.");
        }
    }

    // ------------------------------------------------------------------------------------------------
    public static void resetDevice(String serviceTag) {
        // Beispiel-Logik, um eine Buchung hinzuzufügen
        System.out.println("Rückgabe: " + serviceTag);
        System.out.println("------------------------------");

        // -----------------------------------------------------------------------------
        List<String> lines = new ArrayList<>();
        String line;
        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(ListFile))) {
            // Datei Zeile für Zeile lesen
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // Überprüfe, ob die Zeile die gewünschte Variable enthält
                if (data[1].equals(serviceTag)) {
                    // Überschreibe data[2], data[3], data[4]
                    data[0] = "true";
                    data[2] = "none";
                    data[3] = "none";
                    data[4] = "none";

                    // Erstelle die neue Zeile mit den geänderten Daten
                    line = String.join(",", data);
                    isUpdated = true;
                }
                // Füge die (aktualisierte oder unveränderte) Zeile zur Liste hinzu
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wenn die Datei aktualisiert wurde, schreibe sie neu
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ListFile))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();  // Zeilenumbruch hinzufügen
                }
                System.out.println("Datei erfolgreich aktualisiert.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Variable nicht gefunden oder keine Aktualisierung erforderlich.");
        }
    }

    //------------------------------------------------------------------------------------------------

    public static String formatDate(JSpinner DATE) {
        // Beispiel für JSpinner
        JComponent editor = new JSpinner.DateEditor(DATE, "dd.MM.yyyy");
        DATE.setEditor(editor);

        // Abrufen des Werts aus dem JSpinner
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

        Object[][] dataPending = new Object[devices.size()][5];
        for (int i = 0; i < devices.size(); i++) {
            dataPending[i][0] = devices.get(i).getStatus();
            dataPending[i][1] = devices.get(i).getServiceTag();
            dataPending[i][2] = devices.get(i).getBenutzer();
            dataPending[i][3] = devices.get(i).getStartDatum();
            dataPending[i][4] = devices.get(i).getEndDatum();

        }

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