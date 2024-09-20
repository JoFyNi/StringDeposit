package DeviceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static DeviceManager.getDevices.setNewDevice;

public class PopUp {

    public static void buchen(String serviceTag) {
        JFrame popUp = new JFrame("Buchung: " + serviceTag);
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUp.setSize(1000,600);

        JPanel labelPanel = new JPanel();
        JPanel enterPanel = new JPanel();
        JPanel btnPanel = new JPanel();

        popUp.add(labelPanel, BorderLayout.NORTH);
        popUp.add(enterPanel, BorderLayout.CENTER);
        popUp.add(btnPanel, BorderLayout.SOUTH);

        JLabel popUpLabel = new JLabel("Bitte ausfüllen");

        JTextField emailText = new JTextField("");
        emailText.setPreferredSize(null);  // Setzt die Größe auf automatisch
        emailText.setColumns(20);  // Setzt die Anzahl der sichtbaren Zeichen (kann angepasst werden)
        addPlaceholder(emailText, "E-Mail");

        // Erstelle die Startdatum-Auswahl
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JComponent startEditor = new JSpinner.DateEditor(startDateSpinner, "dd.MM.yyyy");
        startDateSpinner.setEditor(startEditor);

        // Erstelle die Enddatum-Auswahl
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JComponent endEditor = new JSpinner.DateEditor(endDateSpinner, "dd.MM.yyyy");
        endDateSpinner.setEditor(endEditor);

        JButton abBtn = new JButton("Abbrechen");
        JButton okBtn = new JButton("OK");

        labelPanel.add(popUpLabel);
        enterPanel.add(emailText);
        enterPanel.add(new JLabel("Ausleihdatum"));
        enterPanel.add(startDateSpinner);
        enterPanel.add(new JLabel("Rückgabedatum"));
        enterPanel.add(endDateSpinner);
        btnPanel.add(abBtn);
        btnPanel.add(okBtn);

        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (emailText.getText().equals("") || emailText.getText().equals("E-Mail") || startDateSpinner.equals(endDateSpinner)) {
                    popUpLabel.setText("Bitte alle Drei Felder ausfüllen!");
                } else {
                    String userName = getDevices.extractBeforeAt(emailText.getText());
                    System.out.println("Extrahierter Username: " + userName);
                    boolean userFound = UserCompare.compareFolderName(userName);

                    if (userFound) {
                        setNewDevice(serviceTag, emailText.getText(), startDateSpinner, endDateSpinner);
                        popUp.dispose();
                    } else {
                        popUpLabel.setText("E-Mail nicht gefunden!");
                    }
                }
            }
        });
        abBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popUp.dispose();
            }
        });

        popUp.revalidate();  // Aktualisiert die Layout-Anordnung
        popUp.repaint();     // Neuzeichnen des Panels
        popUp.setLocationRelativeTo(null);
        popUp.pack();
        popUp.setVisible(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    private static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
}
