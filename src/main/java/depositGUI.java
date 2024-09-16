import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class depositGUITwo extends JFrame implements ActionListener {
    private static final String APP_NAME = "DEPO";
    private static final String ADD_BUTTON_TEXT = "Add";
    private static final String SAVE_BUTTON_TEXT = "Save";
    private static final String DELETE_BUTTON_TEXT = "Delete";
    private static final String COPY_BUTTON_TEXT = "Copy";
    private static final String OPEN_BUTTON_TEXT = "Open";
    private static final String ADD_TAB_BUTTON_TEXT = "Add Tab";
    private static final String DELETE_TAB_BUTTON_TEXT = "Delete Tab";
    private static final String DIALOG_TITLE = "Enter a name for the new tab:";
    private static final String LOG_FOLDER = "./Tabs/";
    private static final String[] COLUMNS = {"#", "Name", "Link"};

    private JTabbedPane tabbedPane;
    private JButton addButton;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton copyButton;
    private JButton openButton;
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private File[] listOfFiles;
    public JTable depositTable;
    public DefaultTableModel depositTableModel;

    private JTextArea previewArea;

    public depositGUI() {
        setTitle(APP_NAME);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel depositPanel = new JPanel();
        depositPanel.setPreferredSize(new Dimension(800, 600));
        depositPanel.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        getContentPane().add(depositPanel, BorderLayout.CENTER);

        depositTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // allow editing only in columns 1 and 2
            }
        };
        loadFileList();

        for (File file : listOfFiles) {
            String tabTitle = file.getName();
            JTable depositTable = createTableFromINIFile(file);
            tabbedPane.addTab(tabTitle, new JScrollPane(depositTable));
            loadTableModelFromINI(file, depositTableModel);
            depositTable.setModel(depositTableModel);
        }
        depositPanel.add(tabbedPane, BorderLayout.CENTER);

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        depositPanel.add(new JScrollPane(previewArea), BorderLayout.EAST);

        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < this.listOfFiles.length) {
                File selectedFile = this.listOfFiles[selectedIndex];
                loadTableModelFromINI(selectedFile, depositTableModel);
                JTable selectedTable = createTableFromINIFile(selectedFile);
                selectedTable.setModel(depositTableModel);
                saveTableModel(selectedTable, depositTableModel);
            } else {
                System.err.println("Invalid tab index: " + selectedIndex);
            }
        });

        addButton = new JButton(ADD_BUTTON_TEXT);
        saveButton = new JButton(SAVE_BUTTON_TEXT);
        deleteButton = new JButton(DELETE_BUTTON_TEXT);
        copyButton = new JButton(COPY_BUTTON_TEXT);
        openButton = new JButton(OPEN_BUTTON_TEXT);

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

        addButton.addActionListener(this);
        saveButton.addActionListener(this);
        deleteButton.addActionListener(this);
        copyButton.addActionListener(this);
        openButton.addActionListener(this);

        toolBar(depositPanel);

        setLocationRelativeTo(null);
        setVisible(true);
        buttons();
        autoSave();
    }

    private void toolBar(JPanel depositPanel) {
        JToolBar toolBar = new JToolBar();
        JButton createTabBtn = new JButton(ADD_TAB_BUTTON_TEXT);
        JButton deleteTabButton = new JButton(DELETE_TAB_BUTTON_TEXT);
        toolBar.add(createTabBtn);
        toolBar.add(deleteTabButton);
        depositPanel.add(toolBar, BorderLayout.NORTH);

        createTabBtn.addActionListener(this);
        deleteTabButton.addActionListener(this);
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
        listOfFiles = logFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ini"));
        if (listOfFiles == null || listOfFiles.length == 0) {
            try {
                File firstFile = new File(LOG_FOLDER, "General.ini");
                boolean created = firstFile.createNewFile();
                listOfFiles = new File[]{firstFile};
                if (!created) {
                    System.err.println("Failed to create log file.");
                }
            } catch (IOException e) {
                System.err.println("Failed to create log file: " + e.getMessage());
            }
        }
    }

    private JTable createTableFromINIFile(File file) {
        DefaultTableModel model = new DefaultTableModel(COLUMNS, 0);
        if (file.length() == 0) {
            initializeINIFile(file);
        }
        try (FileInputStream input = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(input);

            int rowCount = 1;
            while (true) {
                String name = props.getProperty("name" + rowCount);
                String link = props.getProperty("link" + rowCount);
                if (name == null || link == null) {
                    break;
                }
                model.addRow(new Object[]{rowCount, name, link});
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JTable(model);
    }

    public void loadTableModelFromINI(File file, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear the existing rows
        if (file.length() == 0) {
            initializeINIFile(file);
        }
        try (FileInputStream input = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(input);

            int rowCount = 1;
            while (true) {
                String name = props.getProperty("name" + rowCount);
                String link = props.getProperty("link" + rowCount);
                if (name == null || link == null) {
                    break;
                }
                tableModel.addRow(new Object[]{rowCount, name, link});
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTable selectedTable = (JTable) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals(ADD_BUTTON_TEXT)) {
            add(selectedTable, depositTableModel);
        } else if (actionCommand.equals(SAVE_BUTTON_TEXT)) {
            saveTableModel(selectedTable, depositTableModel);
        } else if (actionCommand.equals(DELETE_BUTTON_TEXT)) {
            delete(selectedTable, depositTableModel);
        } else if (actionCommand.equals(COPY_BUTTON_TEXT)) {
            copy(selectedTable, depositTableModel);
        } else if (actionCommand.equals(OPEN_BUTTON_TEXT)) {
            open(selectedTable);
        } else if (actionCommand.equals(ADD_TAB_BUTTON_TEXT)) {
            createTab();
        } else if (actionCommand.equals(DELETE_TAB_BUTTON_TEXT)) {
            deleteTab();
        }
    }

    private void createTab() {
        String m = JOptionPane.showInputDialog(DIALOG_TITLE);
        if (m == null || m.isEmpty()) return;
        File f = new File(LOG_FOLDER + m + ".ini");
        if (f.exists()) {
            JOptionPane.showMessageDialog(null, "Tab already exists");
        } else {
            try {
                f.createNewFile();
                initializeINIFile(f);
                listOfFiles = new File(LOG_FOLDER).listFiles((dir, name) -> name.toLowerCase().endsWith(".ini"));
                DefaultTableModel newModel = new DefaultTableModel(COLUMNS, 0);
                tabbedPane.addTab(m, new JScrollPane(new JTable(newModel)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void deleteTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            String title = tabbedPane.getTitleAt(selectedIndex);
            tabbedPane.remove(selectedIndex);
            File f = new File(LOG_FOLDER + title);
            if (f.exists()) f.delete();
        } else {
            JOptionPane.showMessageDialog(null, "No tab selected");
        }
    }

    private void add(JTable table, DefaultTableModel tableModel) {
        int rowToInsert = tableModel.getRowCount(); // Neue Zeile immer am Ende hinzufügen
        tableModel.addRow(new Object[]{rowToInsert + 1, "", ""}); // Füge eine neue Zeile hinzu
        updateRowNumbers(tableModel); // Zeilennummern aktualisieren
    }

    private void delete(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            updateRowNumbers(tableModel);
        }
    }

    private void updateRowNumbers(DefaultTableModel tableModel) {
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }
    }

    private void saveTableModel(JTable table, DefaultTableModel tableModel) {
        if (tableModel.getRowCount() == 0) {
            return; // Keine Zeilen, also nichts zu speichern
        }

        String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        File iniFile = new File(LOG_FOLDER + title);
        try (FileOutputStream output = new FileOutputStream(iniFile)) {
            Properties props = new Properties();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                props.setProperty("name" + (i + 1), tableModel.getValueAt(i, 1).toString());
                props.setProperty("link" + (i + 1), tableModel.getValueAt(i, 2).toString());
            }

            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
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

    private void buttons() {
        int tabCount = tabbedPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            JTable selectedTable = (JTable) ((JScrollPane) tabbedPane.getComponentAt(i)).getViewport().getView();
            selectedTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem copyItem = new JMenuItem(COPY_BUTTON_TEXT);
                        JMenuItem openItem = new JMenuItem(OPEN_BUTTON_TEXT);

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
                    } else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                        int selectedRow = selectedTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String previewText = "Name: " + selectedTable.getValueAt(selectedRow, 1) + "\nLink: " + selectedTable.getValueAt(selectedRow, 2);
                            previewArea.setText(previewText);
                        }
                    }
                }
            });
        }
    }

    private static void copyToClipboard(String content) {
        StringSelection stringSelection = new StringSelection(content);
        clipboard.setContents(stringSelection, stringSelection);
    }

    private void autoSave() {
        Timer timer = new Timer(1000, e -> {
            JTable selectedTable = (JTable) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
            if (selectedTable.getModel().getRowCount() > 0) {
                saveTableModel(selectedTable, depositTableModel);
            }
        });
        timer.start();
    }

    private void initializeINIFile(File file) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            Properties props = new Properties();
            // Hier können grundlegende Einträge hinzugefügt werden, wenn nötig
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(depositGUI::new);
    }
}
