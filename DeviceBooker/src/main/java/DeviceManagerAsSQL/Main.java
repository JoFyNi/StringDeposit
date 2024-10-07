package DeviceManagerAsSQL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static DeviceManagerAsSQL.config.*;


public class Main extends Application {
    List<Device> devicesFromDB = getDevicesFromDatabase();

    @Override
    public void start(Stage primaryStage) {
        // Root Layout für das GUI
        BorderPane root = new BorderPane();

        // Tabelle zur Anzeige der deviceElementedaten
        TableView<Device> tableView = new TableView<>();

        // Spalten der Tabelle
        TableColumn<Device, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Device, String> serviceTagCol = new TableColumn<>("Service Tag");
        serviceTagCol.setCellValueFactory(new PropertyValueFactory<>("serviceTag"));

        TableColumn<Device, String> benutzerCol = new TableColumn<>("Benutzer");
        benutzerCol.setCellValueFactory(new PropertyValueFactory<>("benutzer"));

        TableColumn<Device, String> startDateCol = new TableColumn<>("Startdatum");
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDatum"));

        TableColumn<Device, String> endDateCol = new TableColumn<>("Enddatum");
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDatum"));

        // Hinzufügen der Spalten zur Tabelle
        tableView.getColumns().addAll(statusCol, serviceTagCol, benutzerCol, startDateCol, endDateCol);

        ////////////////////////////////////////////////////////////////////////
        // Neue Methode für das Abrufen der Geräte aus der Datenbank
        tableView.getItems().addAll(devicesFromDB);
        generateXML(devicesFromDB);
        getDevices.setDevices(devicesFromDB);
        //loadDevicesFromXML(tableView);
        ////////////////////////////////////////////////////////////////////////

        // Buchungsbutton hinzufügen
        Button bookButton = new Button("Buchen");
        bookButton.setOnAction(event -> {
            Device selectedDevice = tableView.getSelectionModel().getSelectedItem();
            if (selectedDevice != null && selectedDevice.getStatus()) {
                PopUp.buchen(selectedDevice.getServiceTag());
            }
        });
        Button returnButton = new Button("Rückgabe");
        returnButton.setOnAction(event -> {
            Device selectedDevice = tableView.getSelectionModel().getSelectedItem();
            if (selectedDevice != null && !selectedDevice.getStatus()) {
                ReturnPopUp.returnDevice(selectedDevice.getServiceTag());
            }
        });

        // ------------------------------------------------------------------------------------------------
        // Mouse interaction
        // ------------------------------------------------------------------------------------------------
        // Kontextmenü für die Tabelle erstellen
        ContextMenu contextMenu = new ContextMenu();

        // Menüeinträge hinzufügen
        MenuItem selectItem = new MenuItem("Gerät auswählen");
        MenuItem bookItem = new MenuItem("Buchen");
        MenuItem returnItem = new MenuItem("Rückgabe");

        // Aktionen für Menüeinträge
        selectItem.setOnAction(event -> {
            Device selectedDevice = tableView.getSelectionModel().getSelectedItem();
            if (selectedDevice != null) {
                System.out.println("Gerät ausgewählt: " + selectedDevice.getServiceTag());
            }
        });

        bookItem.setOnAction(event -> {
            Device selectedDevice = tableView.getSelectionModel().getSelectedItem();
            if (selectedDevice != null && selectedDevice.getStatus()) {
                PopUp.buchen(selectedDevice.getServiceTag());
            }
        });

        returnItem.setOnAction(event -> {
            Device selectedDevice = tableView.getSelectionModel().getSelectedItem();
            if (selectedDevice != null && !selectedDevice.getStatus()) {
                ReturnPopUp.returnDevice(selectedDevice.getServiceTag());
            }
        });

        // Füge Menüeinträge zum Kontextmenü hinzu
        contextMenu.getItems().addAll(selectItem, bookItem, returnItem);

        // Setze das Kontextmenü für die Tabelle
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {  // Rechtsklick erkennen
                contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();  // Menü verstecken, wenn nicht Rechtsklick
            }
        });

        HBox buttonBox = new HBox(bookButton, returnButton);
        root.setCenter(tableView);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Geräteverwaltung");
        primaryStage.show();

        new Thread(() -> {
            try {
                watchForChanges(tableView);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // SQL
    private List<Device> getDevicesFromDatabase() {
        List<Device> devicesList = new ArrayList<>();
        String query = "SELECT * FROM devices"; // SQL-Abfrage, um alle Geräte abzurufen

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Lade den MySQL JDBC-Treiber
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String status = resultSet.getString("status");
                String serviceTag = resultSet.getString("serviceTag");
                String user = resultSet.getString("user");
                String startDate = resultSet.getString("startDate");
                String endDate = resultSet.getString("endDate");

                boolean isActive = status.equalsIgnoreCase("Verfügbar");
                devicesList.add(new Device(isActive, serviceTag, user, startDate, endDate));
            }
            connection.close(); // Verbindung schließen
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return devicesList;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // Generiert eine XML-Datei basierend auf der Liste der deviceElemente
    private void generateXML(List<Device> devicesXML) {
        try {
            // Erstelle ein XML-Dokument
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Wurzelelement
            org.w3c.dom.Element rootElement = doc.createElement("deviceElementeverwaltung");
            doc.appendChild(rootElement);

            for (Device device : devicesXML) {
                org.w3c.dom.Element deviceElement = doc.createElement("deviceElement");

                org.w3c.dom.Element status = doc.createElement("Status");
                status.appendChild(doc.createTextNode(device.getStatusText()));
                deviceElement.appendChild(status);

                org.w3c.dom.Element serviceTag = doc.createElement("ServiceTag");
                serviceTag.appendChild(doc.createTextNode(device.getServiceTag()));
                deviceElement.appendChild(serviceTag);

                org.w3c.dom.Element benutzer = doc.createElement("Benutzer");
                benutzer.appendChild(doc.createTextNode(device.getBenutzer()));
                deviceElement.appendChild(benutzer);

                org.w3c.dom.Element startDatum = doc.createElement("Startdatum");
                startDatum.appendChild(doc.createTextNode(device.getStartDatum()));
                deviceElement.appendChild(startDatum);

                org.w3c.dom.Element endDatum = doc.createElement("Enddatum");
                endDatum.appendChild(doc.createTextNode(device.getEndDatum()));
                deviceElement.appendChild(endDatum);

                rootElement.appendChild(deviceElement);
            }

            // Schreibe das XML-Dokument in eine Datei 'devices.xml'
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("devices.xml"));
            transformer.transform(source, result);

            System.out.println("XML-Datei 'devices.xml' wurde erfolgreich erstellt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------
    // Lädt die device daten aus der XML-Datei in die Tabelle
    private void loadDevicesFromXML(TableView<Device> tableView) {
        try {
            // Sicherstellen, dass die aktuellste Version der XML-Datei geladen wird
            File xmlFile = new File("devices.xml");
            if (!xmlFile.exists()) {
                System.out.println("XML-Datei 'devices.xml' existiert nicht.");
                return;
            }

            // Reset the DocumentBuilder to avoid any cache issues
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Neu einlesen und parsen
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList deviceList = doc.getElementsByTagName("deviceElement");

            if (deviceList == null || deviceList.getLength() == 0) {
                System.out.println("Keine Geräte zum Laden.");
                return;
            }

            // Tabelle füllen
            for (int i = 0; i < deviceList.getLength(); i++) {
                String statusText = doc.getElementsByTagName("Status").item(i).getTextContent();
                boolean status = statusText.equalsIgnoreCase("Verfügbar");

                String serviceTag = doc.getElementsByTagName("ServiceTag").item(i).getTextContent();
                String benutzer = doc.getElementsByTagName("Benutzer").item(i).getTextContent();
                String startDatum = doc.getElementsByTagName("Startdatum").item(i).getTextContent();
                String endDatum = doc.getElementsByTagName("Enddatum").item(i).getTextContent();

                // Gerät zur Tabelle hinzufügen
                Device device = new Device(status, serviceTag, benutzer, startDatum, endDatum);
                tableView.getItems().add(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------------------------------
    // WatchService für Dateiänderungen
    private void watchForChanges(TableView<Device> tableView) throws Exception {
        Path path = Paths.get(KinPath);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.context().toString().equals(ListPath)) {
                    System.out.println("DB wurde Aktualisiert, Tabelle wird aktualisiert.");
                    reloadTableData(tableView);
                }
            }
            key.reset();
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    private void reloadTableData(TableView<Device> tableView) {
        // Neue Methode für das Abrufen der Geräte aus der Datenbank
        tableView.getItems().addAll(devicesFromDB);
        generateXML(devicesFromDB);

        tableView.getItems().clear();
        loadDevicesFromXML(tableView);
        tableView.refresh();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Startpunkt der Anwendung
    public static void main(String[] args) {
        launch(args);
    }
}