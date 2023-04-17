package main.java;

import javax.naming.spi.DirectoryManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class depositGUI extends JFrame implements ActionListener {
    private static final String APP_NAME = "DEPO";
    private static final String ADD_BUTTON_TEXT = "Add";
    private static final String ADD_TAB_BUTTON_TEXT = "Add Tab";
    private static final String SAVE_BUTTON_TEXT = "Save";
    private static final String DELETE_BUTTON_TEXT = "Delete";
    private static final String DELETE_TAB_BUTTON_TEXT = "Delete Tab";
    private static final String COPY_BUTTON_TEXT = "Copy";
    private static final String OPEN_BUTTON_TEXT = "Open";
    private static final String DIALOG_TITLE = "Enter a name for the new tab:";
    private static final String DEPO_ICON = "DEPO_ICON.png";
    private static final String LOG_FOLDER = "./Tabs/";
    private static File tabFolder = new File(LOG_FOLDER);
    // Liste aller .txt-Dateien im Ordner
    private File[] listOfFiles = tabFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
    private static final String[] COLUMNS = {"#", "Name", "Link"};
    private static final int TABLE_COLUMN_WIDTH = 150;
    private static final int TABLE_PREFERRED_WIDTH = 300;

    private JTabbedPane tabbedPane;
    private final JButton addButton;
    private final JButton saveButton;
    private final JButton deleteButton;
    private final JButton copyButton;
    private final JButton openButton;
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private int rowCount = 0;
    private String version = System.getProperty("java.version");
    public JTable depositTable;
    public DefaultTableModel depositTableModel;

    public JToolBar toolBar;
    public JButton fileFinderBtn;
    public JButton createTabBtn;
    private JButton deleteTabButton;

    public depositGUI() {
        setTitle(APP_NAME + "   JDK: " + version);
        setSize(640, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel depositPanel = new JPanel();
        depositPanel.setPreferredSize(new Dimension(640, 400));
        depositPanel.setLayout(new BorderLayout());
        // Create the tabbed pane
        tabbedPane = new JTabbedPane();
        getContentPane().add(depositPanel, BorderLayout.CENTER);

        depositTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 1 and 2
            }
        };
        loadFileList();
        // TabbedPane mit Tabs für jede .txt-Datei befüllen
        for (File file : listOfFiles) {
            // Tab erstellen und Titel setzen
            String tabTitle = file.getName();
            JTable depositTable = createTableFromTextFile(file);
            //new JTable(depositTableModel);
            tabbedPane.addTab(tabTitle, new JScrollPane(depositTable));
            loadTableModelFromFile(new File(file.getAbsolutePath()), depositTableModel);
            depositTable.setModel(depositTableModel);
            // AUTO_RESIZE_COLUMNS = off so that the table can be resized -> 20 / 400
            depositTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
        // add Table to Tab
        depositPanel.add(tabbedPane);

        // Add a listener to the tabbed pane to listen for tab selection events
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            // Get the index of the selected tab
            int selectedIndex = tabbedPane.getSelectedIndex();
            // Check if the index is within the bounds of the array
            if (selectedIndex >= 0 && selectedIndex < this.listOfFiles.length) {
                File selectedFile = this.listOfFiles[selectedIndex];
                // Get the file associated with the selected tab
                // Load the contents of the selected file into the table model
                loadTableModelFromFile(selectedFile, depositTableModel);
                // Set the table model for the JTable to display the contents of the selected file
                JTable selectedTable = createTableFromTextFile(selectedFile); //(JTable) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
                selectedTable.setModel(depositTableModel);
                saveTableModel(selectedTable, depositTableModel);
            } else {
                // Handle the case where the selected index is invalid
                System.err.println("Invalid tab index: " + selectedIndex);
            }
        });

        // buttons
        addButton = new JButton(ADD_BUTTON_TEXT);
        saveButton = new JButton(SAVE_BUTTON_TEXT);
        deleteButton = new JButton(DELETE_BUTTON_TEXT);
        copyButton = new JButton(COPY_BUTTON_TEXT);
        openButton = new JButton(OPEN_BUTTON_TEXT);
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
        // ToolBar
        toolBar = new JToolBar();
        depositPanel.add(toolBar, BorderLayout.NORTH);
        fileFinderBtn = new JButton("FileFinder");
        createTabBtn = new JButton(ADD_TAB_BUTTON_TEXT);
        deleteTabButton = new JButton(DELETE_TAB_BUTTON_TEXT);
        toolBar.add(fileFinderBtn);
        toolBar.add(createTabBtn);
        toolBar.add(deleteTabButton);
        fileFinderBtn.addActionListener(this);
        createTabBtn.addActionListener(this);
        deleteTabButton.addActionListener(this);
        toolBar.setForeground(Color.WHITE);
        toolBar.setFont(new Font("Serif", Font.PLAIN, 18));
        // designe for the frame
        getContentPane().setFont(new Font("Times New Roman", Font.PLAIN, 20));
        setLocationRelativeTo(null);
        setVisible(true);
        buttons();
    }
    public void loadFileList() {
        File logFolder = new File(LOG_FOLDER);
        if (!logFolder.exists()) {
            boolean created = logFolder.mkdirs();
            if (!created) {
                System.err.println("Failed to create log folder.");
                return;
            }
        }
        File firstFile = new File(LOG_FOLDER, "General.txt");
        if (listOfFiles == null || listOfFiles.length == 0) {
            try {
                boolean created = firstFile.createNewFile();
                listOfFiles = firstFile.listFiles();
                if (!created) {
                    System.err.println("Failed to create log file.");
                }
            } catch (IOException e) {
                System.err.println("Failed to create log file: " + e.getMessage());
            }
        }
    }
    private JTable createTableFromTextFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            ArrayList<String[]> rows = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                rows.add(rowData);
            }

            DefaultTableModel model = new DefaultTableModel(rows.toArray(new Object[0][0]), COLUMNS);
            loadModel();
            return new JTable(model);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void loadTableModelFromFile(File logFile, DefaultTableModel tableModel) {
        Object[] newRow = {"1", "", ""};
        if (!logFile.exists()) {
            // If it doesn't, create an empty depositTableModel
            try {
                logFile.createNewFile();
                tableModel = new DefaultTableModel(COLUMNS, 0);
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
                tableModel.setDataVector(dataList.toArray(new Object[0][0]), COLUMNS);
                loadModel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable selectedTable = (JTable) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
        if (e.getSource() == addButton) {
            add(selectedTable, depositTableModel);
        } else if (e.getSource() == saveButton) {
            saveTableModel(selectedTable, depositTableModel);
        } else if (e.getSource() == deleteButton) {
            delete(selectedTable, depositTableModel);
        } else if (e.getSource() == copyButton) {
            copy(selectedTable, depositTableModel);
        } else if (e.getSource() == openButton) {
            open(selectedTable);
        } else if (e.getSource() == fileFinderBtn) {
            // Laden Sie die FileFinder-Klasse aus dem JAR-File
            try {
                URLClassLoader loader = new URLClassLoader(new URL[]{
                        new File("libs/FileFinder.jar").toURI().toURL()
                });
                Class<?> fileFinderClass = loader.loadClass("componenten.Main");
                // Erstellen Sie eine Instanz der FileFinder-Klasse
                Object fileFinder = fileFinderClass.newInstance();
                // Rufen Sie die main-Methode auf der FileFinder-Instanz auf
                Method mainMethod = fileFinderClass.getMethod("main", String[].class);
                mainMethod.invoke(fileFinder, new Object[]{new String[]{}});
            } catch (ClassNotFoundException | MalformedURLException | InvocationTargetException |
                     InstantiationException | IllegalAccessException | NoSuchMethodException error) {
                throw new RuntimeException(error);
            }
        } else if (e.getSource() == createTabBtn) {
            String m = JOptionPane.showInputDialog("Name you're new Tab");
            File f = new File(tabFolder.getAbsolutePath() + "/" + m + ".txt");
            if (f.exists()) JOptionPane.showMessageDialog(null, "Tab already exists");
            else {
                try {
                    if (m != null) {
                        f.createNewFile();
                        listOfFiles = tabFolder.listFiles(); // aktualisiert listOfFiles
                        tabbedPane.addTab(m, new JScrollPane(new JTable(depositTableModel)));
                        loadTableModelFromFile(new File(f.getAbsolutePath()), depositTableModel);
                        Object[] newRow = {"1", "", ""};
                        BufferedReader reader = new BufferedReader(new FileReader(f));
                        String line;
                        ArrayList<String[]> dataList = new ArrayList<>();
                        while ((line = reader.readLine()) != null) {
                            String[] rowData = line.split(",");
                            dataList.add(rowData);
                        }
                        depositTableModel.addRow(newRow);
                        depositTableModel.setDataVector(dataList.toArray(new Object[0][0]), COLUMNS);
                        loadModel();
                        /*
                        createFile
                        createTable(file); -> load Table
                        createTableModel(file, table); -> load TableModel
                        addTab(file, mewJScrollPne(table));
                        */
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSource() == deleteTabButton) {
            String tabName = JOptionPane.showInputDialog("Enter the Tab name you want to delete");
            int tabCount = tabbedPane.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                String title = tabbedPane.getTitleAt(i);
                if (title.equals(tabName)) {
                    tabbedPane.remove(i);
                    // remove the file based on the tab that got selected by name
                    File f = new File(tabFolder.getAbsolutePath() + "/" + tabName);
                    if (f.exists()) f.delete();
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Tab does not exist");
        }
    }
    private void add(JTable table, DefaultTableModel tableMode) {
        int selectedRow = table.getSelectedRow();
        int rowToInsert = selectedRow == -1 ? tableMode.getRowCount() : selectedRow + 1;
        tableMode.insertRow(rowToInsert, new Object[]{rowToInsert + 1, ""});
        rowCount++;
        updateRowNumbers(tableMode);
        saveTableModel(table, tableMode);
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
            saveTableModel(table, tableMode);
        }
    }

    private void saveTableModel(JTable table, DefaultTableModel tableModel) {
        String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        String filename = title.replaceAll("\\s+", "_");
        File logFile = new File(tabFolder.getAbsolutePath() + "/" + filename);
        try (PrintWriter writer = new PrintWriter(logFile)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    writer.print(tableModel.getValueAt(i, j));
                    if (j < tableModel.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        loadModel();
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

    private void loadModel() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            JScrollPane selectedScrollPane = (JScrollPane) selectedComponent;
            JViewport viewport = selectedScrollPane.getViewport();
            if (viewport != null) {
                Component view = viewport.getView();
                if (view instanceof JTable) {
                    JTable selectedTable = (JTable) view;
                    selectedTable.getColumnModel().getColumn(0).setWidth(20);
                    selectedTable.getColumnModel().getColumn(0).setPreferredWidth(20);
                    selectedTable.getColumnModel().getColumn(1).setWidth(TABLE_COLUMN_WIDTH);
                    selectedTable.getColumnModel().getColumn(1).setPreferredWidth(TABLE_PREFERRED_WIDTH);
                    selectedTable.getColumnModel().getColumn(2).setWidth(TABLE_COLUMN_WIDTH);
                    selectedTable.getColumnModel().getColumn(2).setPreferredWidth(TABLE_PREFERRED_WIDTH);

                    if (selectedTable == null) {
                        selectedTable = new JTable();
                    }
                    selectedTable.setModel(depositTableModel);
                }
            }
        }
    }

    void buttons() {
        int tabCount = tabbedPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            JTable selectedTable = (JTable) ((JScrollPane) tabbedPane.getComponentAt(i)).getViewport().getView();
            selectedTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // create and show the JPopupMenu
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem copyItem = new JMenuItem(COPY_BUTTON_TEXT);
                        JMenuItem openItem = new JMenuItem(OPEN_BUTTON_TEXT);
                        // add action listeners to the menu items
                        copyItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                copy(selectedTable, depositTableModel);
                            }
                        });
                        openItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                open(selectedTable);
                            }
                        });
                        menu.add(copyItem);
                        menu.add(openItem);
                        menu.show(selectedTable, e.getX(), e.getY());
                    }
                }
            });
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
}
