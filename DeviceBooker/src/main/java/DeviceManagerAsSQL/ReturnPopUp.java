package DeviceManagerAsSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ReturnPopUp {
    public static void returnDevice(String serviceTag) {
        JFrame returnPopUp = new JFrame("Rückgabe: " + serviceTag);
        returnPopUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        returnPopUp.setSize(1000,600);

        JPanel labelPanel = new JPanel();
        JPanel enterPanel = new JPanel();
        JPanel btnPanel = new JPanel();

        returnPopUp.add(labelPanel, BorderLayout.NORTH);
        returnPopUp.add(enterPanel, BorderLayout.CENTER);
        returnPopUp.add(btnPanel, BorderLayout.SOUTH);

        JLabel returnPopUpLabel = new JLabel("Bitte ausfüllen");

        JTextField emailText = new JTextField("");
        emailText.setPreferredSize(null);  // Setzt die Größe auf automatisch
        emailText.setColumns(20);  // Setzt die Anzahl der sichtbaren Zeichen (kann angepasst werden)
        addPlaceholder(emailText, "E-Mail");

        JButton abBtn = new JButton("Abbrechen");
        JButton okBtn = new JButton("OK");

        labelPanel.add(returnPopUpLabel);
        enterPanel.add(emailText);

        btnPanel.add(abBtn);
        btnPanel.add(okBtn);

        okBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (emailText.equals("") | emailText.equals("E-Mail")) {
                returnPopUpLabel.setText("Bitte Feld ausfüllen!");
            } else {
                String userName = getDevices.extractBeforeAt(emailText.getText());
                System.out.println("Extrahierter Username: " + userName);
                boolean userFound = UserCompare.compareFolderName(userName);

                if (userFound) {
                    getDevices.resetDevice(serviceTag);
                    returnPopUp.dispose();
                } else {
                    returnPopUpLabel.setText("E-Mail nicht gefunden!");
                }

            }
        }
    });
        abBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            returnPopUp.dispose();
        }
    });

        returnPopUp.revalidate();  // Aktualisiert die Layout-Anordnung
        returnPopUp.repaint();     // Neuzeichnen des Panels
        returnPopUp.setLocationRelativeTo(null);
        returnPopUp.pack();
        returnPopUp.setVisible(true);
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