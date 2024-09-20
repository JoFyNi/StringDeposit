package DeviceManager;
import java.io.File;

public class UserCompare {
    static String path = "Q:\\kws\\kin";
    public static boolean compareFolderName(String userName) {
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            // Hole alle Dateien und Ordner im Verzeichnis
            File[] files = directory.listFiles();

            if (files != null) {
                // Durchlaufe alle Einträge im Verzeichnis
                for (File file : files) {
                    if (file.isDirectory()) {
                        String folderNameInPath = file.getName();
                        System.out.println("Gefundener Ordner: " + folderNameInPath);

                        if (folderNameInPath.equalsIgnoreCase(userName)) {
                            System.out.println("User: " + userName + " gefunden");
                            return true;
                        }
                    }
                }
            }
            System.out.println("Kein Ordnername passt zu " + userName);
            return false;
        } else {
            System.out.println("Verzeichnis nicht gefunden oder es ist kein Ordner!");
            return false;
        }
    }
}