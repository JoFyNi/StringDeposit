package DeviceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class getDevices {
    public static String DEVICE;
    public static File ListFile = new File("src/main/java/DeviceManager/list.txt");

    public static void setDevices(List<Device> devices) {
        String line = "";
        String SplitBy = ",";
        try (BufferedReader reader = new BufferedReader(new FileReader(ListFile))) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(SplitBy);

                Device device = new Device();
                device.status = data[0].equalsIgnoreCase("true");
                device.ServiceTag = data[1];
                device.user = data[2];
                device.startDate = data[3];
                device.endDate = data[4];

                devices.add(device);  // Gerät wird immer hinzugefügt
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(devices);
        displayDevices(devices);
    }

    public static void setNewDevice(String email, String startDate, String endDate) {
        // Beispiel-Logik, um eine Buchung hinzuzufügen
        System.out.println("Neue Buchung:");
        System.out.println("E-Mail: " + email);
        System.out.println("Startdatum: " + startDate);
        System.out.println("Enddatum: " + endDate);

        // In einer realen Anwendung würdest du hier Logik hinzufügen, um die Daten
        // zu speichern oder das entsprechende Gerät in der Liste zu aktualisieren.
    }

    public static void displayDevices(List<Device> devices) {

        Object[][] dataPending = new Object[devices.size()][5];
        for (int i = 0; i < devices.size(); i++) {
            dataPending[i][0] = devices.get(i).status;
            dataPending[i][1] = devices.get(i).ServiceTag;
            dataPending[i][2] = devices.get(i).user;
            dataPending[i][3] = devices.get(i).status;
            dataPending[i][4] = devices.get(i).endDate;

        }

        for (Device device : devices) {
            System.out.println("Status: " + device.status);
            System.out.println("Service Tag: " + device.ServiceTag);
            System.out.println("Benutzer: " + device.user);
            System.out.println("Startdatum: " + device.startDate);
            System.out.println("Enddatum: " + device.endDate);
            System.out.println("----------------------------");
        }
    }

    public String getName() {
        return DEVICE;
    }
}