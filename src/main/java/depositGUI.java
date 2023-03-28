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
import java.util.List;

public class depositGUI extends JFrame implements ActionListener {
    Icon Icon = new ImageIcon("DEPO_ICON.png");
    public JPanel depositPanel;
    public JTabbedPane tabbedPane;
    public JTable depositTable;
    public JTable depositTableTwo;
    public JTable depositTableThree;
    public DefaultTableModel depositTableModel;
    public DefaultTableModel depositTableModelTwo;
    public DefaultTableModel depositTableModelThree;
    public JButton addButton;
    public JButton saveButton;
    public JButton deleteButton;
    public JButton copyButton;
    public JButton openButton;
    public File logFileOne = new File("log1.txt");
    public File logFileTwo = new File("log2.txt");
    public File logFileThree = new File("log3.txt");
    public File tabsFile = new File("tabLog.txt");
    static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private int rowCount = 0;
    String version = System.getProperty("java.version");
    public depositGUI() {
        setTitle("DEPO      Version: " + version);
        setSize(640, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        depositPanel = new JPanel();
        depositPanel.setPreferredSize(new Dimension(640, 400));
        depositPanel.setLayout(new BorderLayout());
        // Create the tabbed pane
        tabbedPane = new JTabbedPane();

        getContentPane().add(depositPanel, BorderLayout.CENTER);
        depositTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 2
            }
        };
        depositTableModelTwo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 2
            }
        };
        depositTableModelThree = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 2
            }
        };
        // Thread safe initialization for safety tables checking
        Thread thread1 = new Thread(() -> {
            loadTableModelFromFile(logFileOne, depositTableModel);
        });
        Thread thread2 = new Thread(() -> {
            loadTableModelFromFile(logFileTwo, depositTableModelTwo);
        });
        Thread thread3 = new Thread(() -> {
            loadTableModelFromFile(logFileThree, depositTableModelThree);
        });
        thread1.start();
        thread2.start();
        thread3.start();
        try {
            // Warten Sie auf das Ende aller Threads, bevor Sie fortfahren
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Step 4: Set the depositTableModels as the model of the JTables
        // first Table
        depositTable = new JTable(depositTableModel);
        depositTable.setModel(depositTableModel);
        // AUTO_RESIZE_COLUMNS = off so that the table can be resized -> 20 / 400
        depositTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        depositTable.getColumnModel().getColumn(0).setWidth(20);
        depositTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        depositTable.getColumnModel().getColumn(1).setWidth(150);
        depositTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        depositTable.getColumnModel().getColumn(2).setWidth(150);
        depositTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        //
        // second Table
        depositTableTwo = new JTable(depositTableModelTwo);
        depositTableTwo.setModel(depositTableModelTwo);
        // AUTO_RESIZE_COLUMNS = off so that the table can be resized -> 20 / 400
        depositTableTwo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        depositTableTwo.getColumnModel().getColumn(0).setWidth(20);
        depositTableTwo.getColumnModel().getColumn(0).setPreferredWidth(20);
        depositTableTwo.getColumnModel().getColumn(1).setWidth(150);
        depositTableTwo.getColumnModel().getColumn(1).setPreferredWidth(300);
        depositTableTwo.getColumnModel().getColumn(2).setWidth(150);
        depositTableTwo.getColumnModel().getColumn(2).setPreferredWidth(300);
        //
        // third Table
        depositTableThree = new JTable(depositTableModelThree);
        depositTableThree.setModel(depositTableModelThree);
        // AUTO_RESIZE_COLUMNS = off so that the table can be resized -> 20 / 400
        depositTableThree.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        depositTableThree.getColumnModel().getColumn(0).setWidth(20);
        depositTableThree.getColumnModel().getColumn(0).setPreferredWidth(20);
        depositTableThree.getColumnModel().getColumn(1).setWidth(150);
        depositTableThree.getColumnModel().getColumn(1).setPreferredWidth(300);
        depositTableThree.getColumnModel().getColumn(2).setWidth(150);
        depositTableThree.getColumnModel().getColumn(2).setPreferredWidth(300);
        // add Table to Tab
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("One", new JScrollPane(depositTable));
        tabbedPane.addTab("Two", new JScrollPane(depositTableTwo));
        tabbedPane.addTab("Three", new JScrollPane(depositTableThree));
        depositPanel.add(tabbedPane);
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
    /*
    loading Tabs and content from existing logs...
     */
    public void loadTableModelFromFile(File logFile, DefaultTableModel tableModel) {
        String[] columns = {"#", "Name", "Link"};
        Object[] newRow = {"1", "", ""};
        if (!logFile.exists()) {
            // If it doesn't, create an empty depositTableModel
            try {
                logFile.createNewFile();
                tableModel = new DefaultTableModel(columns, 0);
                if (tableModel.getRowCount() == 0) {
                    tableModel.addRow(newRow);
                    rowCount++;
                    tableModel.fireTableDataChanged();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                tableModel.addRow(newRow);
                tableModel.setDataVector(dataList.toArray(new Object[0][0]), columns);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        if (e.getSource() == addButton) {
            if (selectedTabIndex == 0) {
                add(depositTable, depositTableModel);
            } else if (selectedTabIndex == 1) {
                add(depositTableTwo, depositTableModelTwo);
            } else if (selectedTabIndex == 2) {
                add(depositTableThree, depositTableModelThree);
            }
        } else if (e.getSource() == saveButton) {
            saveTableModelToLogFile(depositTableModel, logFileOne);
            saveTableModelToLogFile(depositTableModelTwo, logFileTwo);
            saveTableModelToLogFile(depositTableModelThree, logFileThree);
        } else if (e.getSource() == deleteButton) {
            if (selectedTabIndex == 0) {
                delete(depositTable, depositTableModel);
            } else if (selectedTabIndex == 1) {
                delete(depositTableTwo, depositTableModelTwo);
            } else if (selectedTabIndex == 2) {
                delete(depositTableThree, depositTableModelThree);
            }
        } else if (e.getSource() == copyButton) {
            if (selectedTabIndex == 0) {
                copy(depositTable, depositTableModel);
            } else if (selectedTabIndex == 1) {
                copy(depositTableTwo, depositTableModelTwo);
            } else if (selectedTabIndex == 2) {
                copy(depositTableThree, depositTableModelThree);
            }
        } else if (e.getSource() == openButton) {
            if (selectedTabIndex == 0) {
                open(depositTable);
            } else if (selectedTabIndex == 1) {
                open(depositTableTwo);
            } else if (selectedTabIndex == 2) {
                open(depositTableThree);
            }
        }
    }

    private void add(JTable table, DefaultTableModel tableMode) {
        int selectedRow = table.getSelectedRow();
        int rowToInsert = selectedRow == -1 ? tableMode.getRowCount() : selectedRow + 1;
        tableMode.insertRow(rowToInsert, new Object[]{rowToInsert + 1, ""});
        rowCount++;
        updateRowNumbers(tableMode);
    }
    private void updateRowNumbers(DefaultTableModel tableModel) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }
    }
    private void delete(JTable table, DefaultTableModel tableMode) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableMode.removeRow(selectedRow);
            updateRowNumbers(tableMode);
        }
    }
    private void saveTableModelToLogFile(DefaultTableModel tableModel, File logFile) {
        try (FileWriter writer = new FileWriter(logFile)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String row = "";
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row += tableModel.getValueAt(i, j) + ",";
                }
                row = row.substring(0, row.length() - 1);
                writer.write(row + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void copy(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String content = (String) tableModel.getValueAt(selectedRow, 2);
            copyToClipboard(content);
        }
    }
    private void open(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String selectedURL = (String) table.getValueAt(selectedRow, 2);
            openURL(selectedURL);
        }
    }
    private void openURL(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
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
                    // add action listeners to the menu items
                    copyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            copy(depositTable, depositTableModel);
                        }
                    });
                    openItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            open(depositTable);
                        }
                    });
                    menu.add(copyItem);
                    menu.add(openItem);
                    menu.show(depositTable, e.getX(), e.getY());
                }
            }
        });
        depositTableTwo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // create and show the JPopupMenu
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem copyItem = new JMenuItem("Copy");
                    JMenuItem openItem = new JMenuItem("Open");
                    // add action listeners to the menu items
                    copyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            copy(depositTableTwo, depositTableModelTwo);
                        }
                    });
                    openItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            open(depositTableTwo);
                        }
                    });
                    menu.add(copyItem);
                    menu.add(openItem);
                    menu.show(depositTableTwo, e.getX(), e.getY());
                }
            }
        });
        depositTableThree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // create and show the JPopupMenu
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem copyItem = new JMenuItem("Copy");
                    JMenuItem openItem = new JMenuItem("Open");
                    // add action listeners to the menu items
                    copyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            copy(depositTableThree, depositTableModelThree);
                        }
                    });
                    openItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            open(depositTableThree);
                        }
                    });
                    menu.add(copyItem);
                    menu.add(openItem);
                    menu.show(depositTableThree, e.getX(), e.getY());
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
}
