// Diese Funktion wird von confirmBooking() in der HTML-Datei aufgerufen
function sendToJava(email, startDate, endDate) {
    // Überprüfe, ob window.javaApp verfügbar ist, um Java aufzurufen
    if (window.javaApp && typeof window.javaApp.callSetNewDevice === 'function') {
        try {
            // Rufe die Java-Methode über die JavaBridge auf
            window.javaApp.callSetNewDevice(email, startDate, endDate);
            getDevice.setNewDevice(email, startDate, endDate);
            console.log("Daten wurden erfolgreich an Java übermittelt.");
        } catch (e) {
            console.log("Fehler beim Aufrufen von Java: ", e);
        }
    } else {
        console.log("JavaBridge ist nicht verfügbar.");
    }
}
