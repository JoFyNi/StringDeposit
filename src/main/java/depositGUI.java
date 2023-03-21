import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;

public class depositGUI extends JFrame implements ActionListener {
    public JPanel depositPanel;
    public JTable depositTable;
    public DefaultTableModel depositTableModel;
    public JScrollPane scrollPane;
    public JButton addButton;
    public JButton saveButton;
    public JButton deleteButton;
    public JButton copyButton;
    public JButton openButton;
    public File logFile = new File("log.txt");
    static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private int rowCount = 1;
    String version = System.getProperty("java.version");
    public depositGUI() {
        setTitle("DEPO      Version: " + version);
        setSize(640, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        depositPanel = new JPanel();
        depositPanel.setPreferredSize(new Dimension(640, 400));
        depositPanel.setLayout(new BorderLayout());
        getContentPane().add(depositPanel, BorderLayout.CENTER);
        depositTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 2
            }
        };
        String[] columns = {"#", "Name", "Link"};
        // Step 2: Check if the log.txt file exists
        if (!logFile.exists()) {
            // If it doesn't, create an empty depositTableModel
            depositTableModel = new DefaultTableModel(columns, 0);
            if (depositTableModel.getRowCount() == 0) {
                Object[] newRow = {"1", "", ""};
                depositTableModel.addRow(newRow);
                rowCount++;
            }
        } else {
            try {
                // Step 3: Read the data from the log.txt file and create a new depositTableModel
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                ArrayList<String[]> dataList = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    dataList.add(rowData);
                }
                reader.close();
                depositTableModel = new DefaultTableModel(dataList.toArray(new Object[0][0]), columns);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Step 4: Set the depositTableModel as the model of the JTable
        depositTable = new JTable(depositTableModel);
        depositTable.setModel(depositTableModel);
        scrollPane = new JScrollPane(depositTable);
        depositPanel.add(scrollPane, BorderLayout.CENTER);
        // AUTO_RESIZE_COLUMNS = off so that the table can be resized -> 20 / 400
        depositTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        depositTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        depositTable.getColumnModel().getColumn(0).setWidth(20);
        depositTable.getColumnModel().getColumn(1).setWidth(150);
        depositTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        depositTable.getColumnModel().getColumn(2).setWidth(150);
        depositTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        // buttons
        addButton = new JButton("Add");
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        copyButton = new JButton("Copy");
        openButton = new JButton("Open");
        // add section (add things to panels)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(copyButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(openButton);
        depositPanel.add(buttonPanel, BorderLayout.SOUTH);
        // link to the actionPerformance
        addButton.addActionListener(this);
        saveButton.addActionListener(this);
        deleteButton.addActionListener(this);
        copyButton.addActionListener(this);
        openButton.addActionListener(this);
        // designe for the frame
        getContentPane().setFont(new Font("Times New Roman", Font.PLAIN, 20));
        setLocationRelativeTo(null);
        setVisible(true);
        buttons();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            int selectedRow = depositTable.getSelectedRow();
            // add a new row at the end of the table if no row is selected
            if (selectedRow == -1) {
                Object[] newRow = {Integer.toString(depositTableModel.getRowCount() + 1), ""};
                depositTableModel.addRow(newRow);
                rowCount++;
                // update the row numbers
                for (int i = selectedRow; i < depositTableModel.getRowCount(); i++) {
                    depositTableModel.setValueAt(i + 1, i, 0);
                }
            } else {
                // insert a new row below the selected row
                Object[] newRow = {Integer.toString(selectedRow + 2), ""};
                depositTableModel.insertRow(selectedRow + 1, newRow);
                rowCount++;
                // update the row numbers
                for (int i = selectedRow; i < depositTableModel.getRowCount(); i++) {
                    depositTableModel.setValueAt(i + 1, i, 0);
                }
            }
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
                String content = (String) depositTableModel.getValueAt(selectedRow, 2);
                copyToClipboard(content);
            }
        } else if (e.getSource() == openButton) {
            // copy the content of the selected row
            int selectedRow = depositTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedURL = (String) depositTable.getValueAt(selectedRow, 2);
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        System.out.println(selectedURL);
                        URI uri = new URI(selectedURL);
                        desktop.browse(uri);
                    } catch (Exception browserException) {
                        browserException.printStackTrace();
                    }
                }
            }
        }
    }
    void buttons() {
        depositTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // create and show the JPopupMenu
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem copyItem = new JMenuItem("Copy");
                    JMenuItem openItem = new JMenuItem("Open");

                    int selectedRow = depositTable.getSelectedRow();
                    Object selectedPath = depositTable.getValueAt(selectedRow, 2);

                    // add action listeners to the menu items
                    copyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            copyToClipboard(selectedPath.toString());
                        }
                    });
                    openItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Desktop desktop = Desktop.getDesktop();
                                URI uri = new URI(selectedPath.toString());
                                desktop.browse(uri);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    menu.add(copyItem);
                    menu.add(openItem);
                    menu.show(depositTable, e.getX(), e.getY());
                }
            }
        });
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
            int currentNumber = Integer.parseInt(depositTableModel.getValueAt(i, 0).toString());
            if (currentNumber >= nextNumber) {
                nextNumber = currentNumber + 1;
            }
        }
        return nextNumber;
    }
}
