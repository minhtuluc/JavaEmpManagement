package view;

import dao.userDAO;
import model.user;
import utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class userManagementDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private transient userDAO dao;
    private mainView parentView;

    // Styling constants
    private static final Color BG_FORM = new Color(0xFAFAFA);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color LABEL_COLOR = new Color(0x202124);
    private static final Color BORDER_COLOR = new Color(0xDADCE0);
    private static final Color ROW_ALT = new Color(0xF8F9FA);

    public userManagementDialog(mainView parent) {
        super(parent, "Quản lý Tài khoản", true);
        this.parentView = parent;
        this.dao = new userDAO();

        setResizable(false);
        IconUtils.setFrameIcon(this);
        initComponents();
        loadDataToTable();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_FORM);
        outer.setPreferredSize(new Dimension(550, 400));

        // ---- Header ----
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel headerTitle = new JLabel("Quản lý Tài khoản người dùng", IconUtils.getPersonIcon(20), SwingConstants.LEFT);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setIconTextGap(10);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        outer.add(headerPanel, BorderLayout.NORTH);

        // ---- Content Panel (Table & Actions) ----
        JPanel contentPanel = new JPanel(new BorderLayout(0, 12));
        contentPanel.setBackground(BG_FORM);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 1. Table
        String[] columnNames = {"Tên tài khoản", "Vai trò"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xE8F0FE));
        table.setSelectionForeground(LABEL_COLOR);

        DefaultTableCellRenderer stripeRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(stripeRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF1F3F4));
        header.setForeground(new Color(0x475569));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 32));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // 2. Buttons toolbar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnPanel.setBackground(BG_FORM);

        JButton btnAdd = createDialogButton("Thêm tài khoản", PRIMARY, Color.WHITE);
        JButton btnEdit = createDialogButton("Sửa đổi", new Color(0xDCFCE7), new Color(0x15803D));
        JButton btnDelete = createDialogButton("Xóa", new Color(0xFEE2E2), new Color(0xB91C1C));
        JButton btnClose = createDialogButton("Đóng", new Color(0xF1F3F4), new Color(0x202124));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClose);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        outer.add(contentPanel, BorderLayout.CENTER);
        add(outer);

        // ---- Button Listeners ----
        btnAdd.addActionListener(e -> {
            boolean success = showUserForm("Thêm tài khoản mới", null, false);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
                loadDataToTable();
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = tableModel.getValueAt(row, 0).toString();
            List<user> all = dao.getAllUsers();
            user selectedUser = null;
            for (user u : all) {
                if (u.getUsername().equals(username)) {
                    selectedUser = u;
                    break;
                }
            }

            if (selectedUser != null) {
                boolean success = showUserForm("Sửa thông tin tài khoản", selectedUser, true);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                    loadDataToTable();
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = tableModel.getValueAt(row, 0).toString();

            // Safety check: Cannot delete own account
            if (username.equals(parentView.getCurrentUser().getUsername())) {
                JOptionPane.showMessageDialog(this, "Bạn không thể xóa chính tài khoản đang đăng nhập của mình!", "Không được phép", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa tài khoản '" + username + "'?",
                    "Xác nhận xóa tài khoản",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deleteUser(username)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa tài khoản thành công!");
                    loadDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnClose.addActionListener(e -> dispose());
    }

    private JButton createDialogButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 16, 6, 16));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 32));
        return btn;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<user> list = dao.getAllUsers();
        for (user u : list) {
            Object[] row = {
                u.getUsername(),
                u.getRole() == 1 ? "Quản trị viên (Admin)" : "Người dùng (User)"
            };
            tableModel.addRow(row);
        }
    }

    private boolean showUserForm(String title, user u, boolean isEdit) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_FORM);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 6, 6, 6);

        JTextField txtUser = new JTextField(15);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComboBox<String> cbxRole = new JComboBox<>(new String[]{"Người dùng (User)", "Quản trị viên (Admin)"});
        cbxRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (isEdit && u != null) {
            txtUser.setText(u.getUsername());
            txtUser.setEditable(false);
            txtPass.setText(u.getPassword());
            cbxRole.setSelectedIndex(u.getRole() == 1 ? 1 : 0);
        }

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Tài khoản:"), g);
        g.gridx = 1;
        panel.add(txtUser, g);

        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), g);
        g.gridx = 1;
        panel.add(txtPass, g);

        g.gridx = 0; g.gridy = 2;
        panel.add(new JLabel("Vai trò:"), g);
        g.gridx = 1;
        panel.add(cbxRole, g);

        int result = JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            int role = cbxRole.getSelectedIndex(); // 0 = User, 1 = Admin

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tài khoản và mật khẩu không được bỏ trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Safety check: Cannot demote oneself
            if (isEdit && u != null && u.getUsername().equals(parentView.getCurrentUser().getUsername())) {
                if (role == 0) {
                    JOptionPane.showMessageDialog(this, "Bạn không thể tự hạ quyền (Quản trị -> Người dùng) của tài khoản đang đăng nhập!", "Không được phép", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            if (isEdit && u != null) {
                u.setPassword(password);
                u.setRole(role);
                return dao.updateUser(u);
            } else {
                // Check duplicate username for new users
                List<user> all = dao.getAllUsers();
                for (user existing : all) {
                    if (existing.getUsername().equalsIgnoreCase(username)) {
                        JOptionPane.showMessageDialog(this, "Tên tài khoản '" + username + "' đã tồn tại!", "Trùng tài khoản", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                user newUser = new user(username, password, role);
                return dao.addUser(newUser);
            }
        }
        return false;
    }
}
