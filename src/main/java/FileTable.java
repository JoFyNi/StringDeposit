import java.io.File;
import java.io.IOException;
import javax.swing.JTable;

public class FileTable extends JTable {

    public FileTable(File file) throws IOException {
        super(new FileTableModel(file));
    }
}