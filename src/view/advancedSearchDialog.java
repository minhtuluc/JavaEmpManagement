package view;

import dao.employeeDAO;
import model.employee;
import utils.IconUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class advancedSearchDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtKeyword, txtMinAge, txtMaxAge, txtMinSalary, txtMaxSalary;
    private JComboBox<String> cbxType, cbxDepartment;
    private transient employeeDAO dao;
    private mainView parentView;

    // Theme colors matching the unified style
    private static final Color BG_FORM = new Color(0xFAFAFA);
    private static final Color LABEL_COLOR = new Color(0x202124);
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xDADCE0);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color SECTION_BG = new Color(0xF0F4FF);

    public advancedSearchDialog(mainView parent) {
        super(parent, "Tìm kiếm nâng cao", true);
        this.parentView = parent;
        this.dao = new employeeDAO();

        setResizable(false);
        IconUtils.setFrameIcon(this);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BG_FORM);

        // ---- Header ----
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel headerTitle = new JLabel("Bộ lọc Tìm kiếm Nâng cao");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);
        outerPanel.add(headerPanel, BorderLayout.NORTH);

        // ---- Form Body ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_FORM);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // 1. Keyword (Mã hoặc Tên)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Từ khóa (Mã/Tên)"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 12, 0);
        txtKeyword = new JTextField(15);
        formPanel.add(createField(txtKeyword), gbc);

        // 2. Type & Department Row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Loại nhân viên"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 12, 12);
        cbxType = new JComboBox<>(new String[]{"Tất cả", "Toàn thời gian", "Bán thời gian", "Quản lý"});
        cbxType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxType.setBackground(FIELD_BG);
        formPanel.add(cbxType, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Phòng ban"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        cbxDepartment = new JComboBox<>();
        cbxDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxDepartment.setBackground(FIELD_BG);
        populateDepartments();
        formPanel.add(cbxDepartment, gbc);

        // 3. Age Range Row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Tuổi từ"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 12, 12);
        txtMinAge = new JTextField(5);
        formPanel.add(createField(txtMinAge), gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Đến"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        txtMaxAge = new JTextField(5);
        formPanel.add(createField(txtMaxAge), gbc);

        // 4. Salary Range Row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Mức lương từ (₫)"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 16, 12);
        txtMinSalary = new JTextField(10);
        formPanel.add(createField(txtMinSalary), gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Đến (₫)"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        txtMaxSalary = new JTextField(10);
        formPanel.add(createField(txtMaxSalary), gbc);

        // ---- Buttons ----
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(BG_FORM);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.setMargin(new Insets(8, 24, 8, 24));
        btnSearch.setPreferredSize(new Dimension(120, 36));

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(new Color(0xF1F3F4));
        btnCancel.setForeground(new Color(0x202124));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setMargin(new Insets(8, 24, 8, 24));
        btnCancel.setPreferredSize(new Dimension(120, 36));

        btnPanel.add(btnSearch);
        btnPanel.add(btnCancel);
        formPanel.add(btnPanel, gbc);

        outerPanel.add(formPanel, BorderLayout.CENTER);
        add(outerPanel);

        // Action Listeners
        btnSearch.addActionListener(e -> performSearch());
        btnCancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnSearch);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(LABEL_COLOR);
        return lbl;
    }

    private JComponent createField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return field;
    }

    private void populateDepartments() {
        cbxDepartment.addItem("Tất cả phòng ban");
        java.util.Set<String> depts = new java.util.TreeSet<>();
        for (employee e : dao.getAllEmployees()) {
            if (e.getDepartment() != null && !e.getDepartment().isEmpty()) {
                depts.add(e.getDepartment());
            }
        }
        for (String d : depts) {
            cbxDepartment.addItem(d);
        }
    }

    private void performSearch() {
        try {
            String keyword = txtKeyword.getText().trim();
            String type = (String) cbxType.getSelectedItem();
            String dept = (String) cbxDepartment.getSelectedItem();

            Integer minAge = null;
            if (!txtMinAge.getText().trim().isEmpty()) {
                minAge = Integer.parseInt(txtMinAge.getText().trim());
                if (minAge < 0) throw new NumberFormatException();
            }

            Integer maxAge = null;
            if (!txtMaxAge.getText().trim().isEmpty()) {
                maxAge = Integer.parseInt(txtMaxAge.getText().trim());
                if (maxAge < 0) throw new NumberFormatException();
            }

            Double minSalary = null;
            if (!txtMinSalary.getText().trim().isEmpty()) {
                minSalary = Double.parseDouble(txtMinSalary.getText().trim());
                if (minSalary < 0) throw new NumberFormatException();
            }

            Double maxSalary = null;
            if (!txtMaxSalary.getText().trim().isEmpty()) {
                maxSalary = Double.parseDouble(txtMaxSalary.getText().trim());
                if (maxSalary < 0) throw new NumberFormatException();
            }

            // Perform dynamic filtering using polymorphism
            List<employee> results = dao.searchAdvanced(keyword, type, dept, minAge, maxAge, minSalary, maxSalary);

            parentView.loadDataToTable(results);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số nguyên dương hợp lệ cho Tuổi và Lương!",
                    "Lỗi nhập liệu",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
