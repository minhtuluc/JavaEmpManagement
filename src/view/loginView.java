package view;

import utils.IconUtils;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class loginView extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color BG_PAGE       = new Color(0xF1F5F9);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color TEXT_PRIMARY  = new Color(0x0F172A);
    private static final Color TEXT_SECONDARY= new Color(0x64748B);
    private static final Color FIELD_BORDER  = new Color(0xCBD5E1);
    private static final Color FIELD_FOCUS   = new Color(0x0EA5E9);
    private static final Color BTN_BG        = new Color(0x0369A1);
    private static final Color BTN_HOVER     = new Color(0x0284C7);
    private static final Color BTN_TEXT      = Color.WHITE;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public loginView() {
        setTitle("Đăng nhập - Hệ thống Quản lý Nhân sự");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        IconUtils.setFrameIcon(this);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_PAGE);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(40, 48, 40, 48)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel iconLabel = new JLabel(IconUtils.getPersonIcon(72));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(iconLabel, gbc);

        JLabel titleLabel = new JLabel("Quản lý Nhân sự", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        gbc.gridy = 1;
        card.add(titleLabel, gbc);

        JLabel subLabel = new JLabel("Vui lòng đăng nhập để tiếp tục", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(TEXT_SECONDARY);
        subLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 28, 0));
        gbc.gridy = 2;
        card.add(subLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(TEXT_PRIMARY);
        card.add(lblUser, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        txtUsername = new JTextField(16);
        styleField(txtUsername);
        card.add(txtUsername, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(TEXT_PRIMARY);
        card.add(lblPass, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 32, 0);
        txtPassword = new JPasswordField(16);
        styleField(txtPassword);
        card.add(txtPassword, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(BTN_BG);
        btnLogin.setForeground(BTN_TEXT);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BTN_BG.darker(), 1),
            BorderFactory.createEmptyBorder(12, 36, 12, 36)));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setOpaque(true);
        card.add(btnLogin, gbc);

        getRootPane().setDefaultButton(btnLogin);
        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());

        wrapper.add(card);
        add(wrapper);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(CARD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setCaretColor(FIELD_FOCUS);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_FOCUS, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
        });
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập và mật khẩu!",
                    "Lỗi nhập liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        dao.userDAO userDAO = new dao.userDAO();
        model.user loggedInUser = userDAO.checkLogin(username, password);

        if (loggedInUser != null) {
            dispose();
            JOptionPane.showMessageDialog(null,
                    "Đăng nhập thành công! Xin chào " + loggedInUser.getUsername() + ".",
                    "Đăng nhập",
                    JOptionPane.INFORMATION_MESSAGE);
            mainView main = new mainView(loggedInUser);
            main.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tên đăng nhập hoặc mật khẩu!",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
