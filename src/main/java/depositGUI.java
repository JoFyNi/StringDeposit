import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class depositGUI extends JFrame implements ActionListener {
    public JPanel depositPanel;
    public JTable depositTable;
    public DefaultTableModel depositTableModel;
    public JScrollPane scrollPane;
    public JButton addButton;
    public JButton saveButton;
    public JButton deleteButton;
    public JButton copyButton;
    public File logFile = new File("log.txt");
    static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private int rowCount = 1;

    public depositGUI() {
        setTitle("DEPO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        depositPanel = new JPanel();
        depositPanel.setPreferredSize(new Dimension(800, 400));
        depositPanel.setLayout(new BorderLayout());
        getContentPane().add(depositPanel, BorderLayout.SOUTH);
        depositTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 2-4
            }
        };
        String[] columns = {"#","Content"};
        depositTableModel = new DefaultTableModel(columns, 0);
        // add "new row" entry if there's no content in the table
        if (depositTableModel.getRowCount() == 0) {
            Object[] newRow = {"1", ""};
            depositTableModel.addRow(newRow);
            rowCount++;
        }

        depositTable = new JTable(depositTableModel);
        scrollPane = new JScrollPane(depositTable);
        depositPanel.add(scrollPane, BorderLayout.CENTER);

        addButton = new JButton("Add");
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        copyButton = new JButton("Copy");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(copyButton);
        depositPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(this);
        saveButton.addActionListener(this);
        deleteButton.addActionListener(this);
        copyButton.addActionListener(this);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            //depositTableModel.addRow(new Object[]{"", "", "", ""});
            Object[] newRow = {Integer.toString(rowCount), ""};
            depositTableModel.addRow(newRow);
            rowCount++;
        }if (e.getSource() == addButton) {
            Object[] newRow = {Integer.toString(getNextNumber()), ""};
            depositTableModel.addRow(newRow);
        } else if (e.getSource() == saveButton) {
            // save the content of the selected row
            try {
                FileWriter writer = new FileWriter(logFile);
                for (int i = 0; i < depositTableModel.getRowCount(); i++) {
                    String row = "";
                    for (int j = 0; j < depositTableModel.getColumnCount(); j++) {
                        row += depositTableModel.getValueAt(i, j) + ",";
                    }
                    row = row.substring(0, row.length() - 1);
                    writer.write(row + "\n");
                }
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == deleteButton) {
            // delete the selected row
            int selectedRow = depositTable.getSelectedRow();
            if (selectedRow != -1) {
                depositTableModel.removeRow(selectedRow);
                // update the row numbers
                for (int i = selectedRow; i < depositTableModel.getRowCount(); i++) {
                    depositTableModel.setValueAt(i + 1, i, 0);
                }
            }
        } else if (e.getSource() == copyButton) {
            // copy the content of the selected row
            int selectedRow = depositTable.getSelectedRow();
            if (selectedRow != -1) {
                String content = (String) depositTableModel.getValueAt(selectedRow, 1);
                copyToClipboard(content);
            }
        }
    }
    private static void copyToClipboard(String content) {
        StringSelection stringSelection = new StringSelection(content);
        if (content.length() > 0) {
            stringSelection = new StringSelection(content);
        } else {
            return;
        }
        // verschiebe content in clipboard
        clipboard.setContents(stringSelection, stringSelection);
        // Ausgabe
        Transferable transferable = clipboard.getContents(null);
        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String ausgabe = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                System.out.println(ausgabe);
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private int getNextNumber() {
        int nextNumber = 1;
        for (int i = 0; i < depositTableModel.getRowCount(); i++) {
            int currentNumber = Integer.parseInt((String) depositTableModel.getValueAt(i, 0));
            if (currentNumber >= nextNumber) {
                nextNumber = currentNumber + 1;
            }
        }
        return nextNumber;
    }
}
