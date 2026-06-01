import utils.dbConnection;
import utils.IconUtils;
import view.loginView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Apply Nimbus LookAndFeel with professional color scheme
        IconUtils.setupLookAndFeel();

        // Ensure Swing runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (dbConnection.getConnection() == null) {
                    JOptionPane.showMessageDialog(null,
                        "Không thể kết nối đến MySQL tại 127.0.0.1:3306.\n"
                        + "Hãy đảm bảo MySQL server đang chạy.\n\n"
                        + "Sau đó khởi động lại ứng dụng.",
                        "Lỗi kết nối CSDL",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                new loginView().setVisible(true);
            }
        });
    }
}
