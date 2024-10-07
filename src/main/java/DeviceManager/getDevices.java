package DeviceManager;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static DeviceManager.config.ListPath;

public class getDevices {
    public static File ListFile = new File(ListPath);
    public static void setDevices(List<Device> devices) {
        devices.clear();
        String line = "";
        String SplitBy = ",";
        try (BufferedReader reader = new BufferedReader(new FileReader(ListFile))) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(SplitBy);
                boolean status = data[0].equalsIgnoreCase("true");
                Device device = new Device(status, data[1], data[2], data[3], data[4]);
                devices.add(device);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        displayDevices(devices);
    }


    public static void setNewDevice(String serviceTag, String email, JSpinner startDate, JSpinner endDate) {
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
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[1].equals(serviceTag)) {
                    data[0] = "false";
                    data[2] = result;
                    data[3] = startDateFormat;
                    data[4] = endDateFormat;

                    line = String.join(",", data);
                    isUpdated = true;
                }
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
        System.out.println("Rückgabe: " + serviceTag);
        System.out.println("------------------------------");
        // -----------------------------------------------------------------------------
        List<String> lines = new ArrayList<>();
        String line;
        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(ListFile))) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[1].equals(serviceTag)) {
                    data[0] = "true";
                    data[2] = "----";
                    data[3] = "----";
                    data[4] = "----";

                    line = String.join(",", data);
                    isUpdated = true;
                }
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