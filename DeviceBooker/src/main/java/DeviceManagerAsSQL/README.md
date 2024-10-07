# Device Booker

Geräte Buchungssystem
- Geräte werden aus liste ausgelesen
- Status wird je nach Inforationen angegeben sprich:
  - Verfügbar
  - Nacht verfügbar, da Ausgeliehen
- Freies Gerät kann gebucht werden mithilfe des Buttons
  - E-Mail angeben
  - Start- und End-Datum auswählen
- Informationen werden in der Liste aktualisiert und gespeichert (in der DB)
- XML datei wird anhand der Liste erstellt und in JFrame angezeigt


-> Abgespaltetes Modul, Klassen Kommunizieren mit SQL Datenbank
 ⇛ Daten Lesen, Schreiben, Löschen