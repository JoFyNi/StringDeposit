import DeviceManager.Device;
import DeviceManager.getDevices;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Erstelle die WebView-Komponente
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Lade die HTML-Seite
        String url = "file:///C:\\Users\\j.nievelstein\\Java\\StringDeposit\\index.html";
        webEngine.load(url);

        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                // Binde die JavaBridge-Instanz an das JavaScript-Window-Objekt
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", new JavaBridge());
                System.out.println("JavaBridge wurde erfolgreich verbunden.");
            }
        });

        // Erstelle ein Layout (BorderPane)
        BorderPane root = new BorderPane();
        root.setCenter(webView);
        Scene scene = new Scene(root, 800, 600);

        // Setze die Szene auf das Fenster
        primaryStage.setTitle("WebView Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static class JavaBridge {

        // Diese Methode wird von JavaScript aufgerufen
        public void callSetNewDevice(String email, String startDate, String endDate) {
            System.out.println("JavaScript hat folgende Daten gesendet:");
            System.out.println("E-Mail: " + email);
            System.out.println("Startdatum: " + startDate);
            System.out.println("Enddatum: " + endDate);

            // Übergabe der Daten an die Java-Methode
            getDevices.setNewDevice(email, startDate, endDate);
        }
    }

    // Startpunkt der Anwendung
    public static void main(String[] args) {
        //getDevices.setDevices(devices);
        //generateHtml(devices);
        //System.out.println("HTML-Seite wurde erfolgreich generiert. Öffne die Datei 'index.html' in deinem Browser.");

        launch(args);
    }

    public static void generateHtml(List<Device> devices) {
        String html = "<!DOCTYPE html>\n"
                + "<html lang=\"de\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    <title>Geräteverwaltung</title>\n"
                + "    <style>\n"
                + "        body {\n"
                + "            font-family: Arial, sans-serif;\n"
                + "            background-color: #f4f4f4;\n"
                + "            margin: 0;\n"
                + "            padding: 0;\n"
                + "        }\n"
                + "        .container {\n"
                + "            width: 80%;\n"
                + "            margin: 50px auto;\n"
                + "            background-color: #fff;\n"
                + "            padding: 20px;\n"
                + "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n"
                + "        }\n"
                + "        h1 {\n"
                + "            text-align: center;\n"
                + "            color: #333;\n"
                + "        }\n"
                + "        table {\n"
                + "            width: 100%;\n"
                + "            border-collapse: collapse;\n"
                + "            margin-bottom: 20px;\n"
                + "        }\n"
                + "        table, th, td {\n"
                + "            border: 1px solid #ddd;\n"
                + "        }\n"
                + "        th, td {\n"
                + "            padding: 12px;\n"
                + "            text-align: center;\n"
                + "        }\n"
                + "        th {\n"
                + "            background-color: #f8f8f8;\n"
                + "        }\n"
                + "        tr:nth-child(even) {\n"
                + "            background-color: #f9f9f9;\n"
                + "        }\n"
                + "        tr:hover {\n"
                + "            background-color: #f1f1f1;\n"
                + "        }\n"
                + "        .button {\n"
                + "            padding: 10px 20px;\n"
                + "            border: none;\n"
                + "            border-radius: 5px;\n"
                + "            cursor: pointer;\n"
                + "            color: white;\n"
                + "        }\n"
                + "        .button-green {\n"
                + "            background-color: #4CAF50;\n"
                + "        }\n"
                + "        .button-green:hover {\n"
                + "            background-color: #45a049;\n"
                + "        }\n"
                + "        .button-grey {\n"
                + "            background-color: grey;\n"
                + "            cursor: not-allowed;\n"
                + "        }\n"
                + "        .status-available {\n"
                + "            color: green;\n"
                + "        }\n"
                + "        .status-unavailable {\n"
                + "            color: red;\n"
                + "        }\n"
                + "        /* Modal Styling */\n"
                + "        .modal {\n"
                + "            display: none;\n"
                + "            position: fixed;\n"
                + "            z-index: 1;\n"
                + "            left: 0;\n"
                + "            top: 0;\n"
                + "            width: 100%;\n"
                + "            height: 100%;\n"
                + "            background-color: rgba(0, 0, 0, 0.5);\n"
                + "        }\n"
                + "        .modal-content {\n"
                + "            background-color: #fff;\n"
                + "            margin: 15% auto;\n"
                + "            padding: 20px;\n"
                + "            border: 1px solid #888;\n"
                + "            width: 400px;\n"
                + "            text-align: center;\n"
                + "        }\n"
                + "        .modal input {\n"
                + "            margin: 10px 0;\n"
                + "            padding: 8px;\n"
                + "            width: 90%;\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div class=\"container\">\n"
                + "        <h1>Geräteverwaltung - Verfügbare Geräte</h1>\n"
                + "        <table>\n"
                + "            <thead>\n"
                + "                <tr>\n"
                + "                    <th>Status</th>\n"
                + "                    <th>Service Tag</th>\n"
                + "                    <th>Benutzer</th>\n"
                + "                    <th>Startdatum</th>\n"
                + "                    <th>Enddatum</th>\n"
                + "                    <th>Aktion</th>\n"
                + "                </tr>\n"
                + "            </thead>\n"
                + "            <tbody>\n";

        // Gerätedaten in die Tabelle einfügen
        for (Device device : devices) {
            String statusClass = device.status ? "status-available" : "status-unavailable";
            String statusText = device.status ? "Verfügbar" : "Nicht verfügbar";
            String buttonClass = device.status ? "button-green" : "button-grey";
            String buttonAction = device.status ? "onclick=\"openModal('" + device.ServiceTag + "')\"" : "disabled";

            html += "                <tr>\n"
                    + "                    <td class=\"status " + statusClass + "\">" + statusText + "</td>\n"
                    + "                    <td>" + device.ServiceTag + "</td>\n"
                    + "                    <td>" + device.user + "</td>\n"
                    + "                    <td>" + device.startDate + "</td>\n"
                    + "                    <td>" + device.endDate + "</td>\n"
                    + "                    <td><button class=\"button " + buttonClass + "\" " + buttonAction + ">Buchen</button></td>\n"
                    + "                </tr>\n";
        }

        // Modal für die Buchung
        html += "            </tbody>\n"
                + "        </table>\n"
                + "    </div>\n"
                + "\n"
                + "    <!-- Modal für die Buchung -->\n"
                + "    <div id=\"bookingModal\" class=\"modal\">\n"
                + "        <div class=\"modal-content\">\n"
                + "            <h2>Gerät Buchen</h2>\n"
                + "            <p id=\"deviceInfo\"></p>\n"
                + "            <input type=\"email\" id=\"email\" placeholder=\"Ihre E-Mail Adresse\">\n"
                + "            <input type=\"date\" id=\"startDate\" placeholder=\"Startdatum\">\n"
                + "            <input type=\"date\" id=\"endDate\" placeholder=\"Enddatum\">\n"
                + "            <br>\n"
                + "            <button onclick=\"closeModal()\">Abbruch</button>\n"
                + "            <button onclick=\"confirmBooking()\">OK</button>\n"
                + "        </div>\n"
                + "    </div>\n"
                + "\n"
                + "    <script>\n"
                + "        function openModal(serviceTag) {\n"
                + "            document.getElementById('deviceInfo').innerText = 'Buchung für Gerät: ' + serviceTag;\n"
                + "            document.getElementById('bookingModal').style.display = 'block';\n"
                + "        }\n"
                + "        function closeModal() {\n"
                + "            document.getElementById('bookingModal').style.display = 'none';\n"
                + "        }\n"
                + "        function confirmBooking() {\n"
                + "            let email = document.getElementById('email').value;\n"
                + "            let startDate = document.getElementById('startDate').value;\n"
                + "            let endDate = document.getElementById('endDate').value;\n"
                + "            if (email && startDate && endDate) {\n"
                + "                try {\n"
                + "                    sendToJava(email, startDate, endDate);\n"
                + "                    closeModal();\n"
                + "                } catch (e) {\n"
                + "                    console.log('Fehler beim Aufrufen von Java: ', e);\n"
                + "                }\n"
                + "            } else {\n"
                + "                alert('Bitte füllen Sie alle Felder aus.');\n"
                + "            }\n"
                + "        }\n"
                + "    </script>\n"
                + "</body>\n"
                + "</html>";

        // Schreibe den HTML-Code in die Datei 'index.html'
        try (FileWriter fileWriter = new FileWriter("index.html")) {
            fileWriter.write(html);
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der HTML-Datei: " + e.getMessage());
        }
    }
}