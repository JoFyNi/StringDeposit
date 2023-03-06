import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }
    public static void createGUI() {
        depositGUI GUI = new depositGUI();
        //JPanel root = GUI.getMainPanel();
        //JFrame frame = new JFrame();
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setContentPane(root);
        //frame.pack();
        //frame.setSize(1000, 600);
        //frame.setLocationRelativeTo(null);
        //frame.setVisible(true);
    }
}
