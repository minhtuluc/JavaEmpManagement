package view;

import dao.employeeDAO;
import model.*;
import utils.IconUtils;

import javax.swing.*;
import java.awt.*;

public class employeeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtId, txtName, txtAge, txtBaseSalary, txtAllowance, txtHours, txtRate, txtBonus;
    private JComboBox<String> cbxType;
    private JComboBox<String> cbxDepartment;
    private transient employeeDAO dao;
    private mainView parentView;
    private String editId;

    // Theme colors
    private static final Color BG_FORM = new Color(0xFAFAFA);
    private static final Color LABEL_COLOR = new Color(0x202124);
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xDADCE0);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color SECTION_BG = new Color(0xF0F4FF);

    public employeeDialog(mainView parent, String editId) {
        super(parent, editId == null ? "Thêm Nhân viên Mới" : "Sửa Thông tin Nhân viên", true);
        this.parentView = parent;
        this.dao = new employeeDAO();
        this.editId = editId;

        setResizable(false);
        IconUtils.setFrameIcon(this);
        initComponents();
        pack();
        setLocationRelativeTo(parent);

        if (editId != null) {
            loadEmployeeData(editId);
            txtId.setEditable(false);
        }
    }

    private void initComponents() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BG_FORM);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ---- Header ----
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel headerIcon = new JLabel(IconUtils.getEmployeeIcon(24));
        headerIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel headerTitle = new JLabel(editId == null ? "Thêm Nhân viên Mới" : "Sửa Thông tin Nhân viên");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);

        JPanel headerInner = new JPanel(new BorderLayout());
        headerInner.setBackground(PRIMARY);
        headerInner.add(headerIcon, BorderLayout.WEST);
        headerInner.add(headerTitle, BorderLayout.CENTER);
        headerPanel.add(headerInner, BorderLayout.WEST);

        outerPanel.add(headerPanel, BorderLayout.NORTH);

        // ---- Form Body ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_FORM);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Section: Basic Info
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(createSectionLabel("Thông tin cơ bản"), gbc);

        // ID
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Mã nhân viên"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 12);
        txtId = new JTextField(10);
        formPanel.add(createField(txtId), gbc);

        // Name
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Họ và tên"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtName = new JTextField(12);
        formPanel.add(createField(txtName), gbc);

        // Age
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Tuổi"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 12);
        txtAge = new JTextField(5);
        formPanel.add(createField(txtAge), gbc);

        // Type
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Loại nhân viên"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        cbxType = new JComboBox<>(new String[]{"Toàn thời gian", "Bán thời gian", "Quản lý"});
        cbxType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxType.setBackground(FIELD_BG);
        formPanel.add(cbxType, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Phòng ban"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        cbxDepartment = new JComboBox<>(new String[]{"Phòng Kỹ thuật", "Phòng Kinh doanh", "Phòng Nhân sự", "Phòng Tài chính", "Phòng Marketing"});
        cbxDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxDepartment.setBackground(FIELD_BG);
        cbxDepartment.setEditable(true);
        formPanel.add(cbxDepartment, gbc);

        // Section: Compensation
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 0, 10, 0);
        formPanel.add(createSectionLabel("Chi tiết lương"), gbc);

        // Base Salary
        gbc.gridwidth = 1;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Lương cơ bản"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 12);
        txtBaseSalary = new JTextField(10);
        txtBaseSalary.setText("0");
        formPanel.add(createField(txtBaseSalary), gbc);

        // Allowance (Full-time)
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Phụ cấp"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtAllowance = new JTextField(10);
        txtAllowance.setText("0");
        formPanel.add(createField(txtAllowance), gbc);

        // Hours worked (Part-time)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Số giờ làm"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 12);
        txtHours = new JTextField(5);
        txtHours.setText("0");
        formPanel.add(createField(txtHours), gbc);

        // Hourly Rate (Part-time)
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Lương mỗi giờ"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtRate = new JTextField(10);
        txtRate.setText("0");
        formPanel.add(createField(txtRate), gbc);

        // Bonus (Manager)
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(createLabel("Tiền thưởng"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 10, 12);
        txtBonus = new JTextField(10);
        txtBonus.setText("0");
        formPanel.add(createField(txtBonus), gbc);

        // ---- Buttons ----
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(BG_FORM);

        JButton btnSave = new JButton(editId == null ? "Lưu thông tin" : "Cập nhật");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.putClientProperty("JButton.buttonType", "roundRect");
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.setMargin(new Insets(8, 24, 8, 24));
        btnSave.putClientProperty("JButton.minimumWidth", 120);
        btnSave.setPreferredSize(new Dimension(btnSave.getPreferredSize().width, 36));

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(new Color(0xF1F3F4));
        btnCancel.setForeground(new Color(0x202124));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.putClientProperty("JButton.buttonType", "roundRect");
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setMargin(new Insets(8, 24, 8, 24));
        btnCancel.putClientProperty("JButton.minimumWidth", 120);
        btnCancel.setPreferredSize(new Dimension(btnCancel.getPreferredSize().width, 36));

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        formPanel.add(btnPanel, gbc);

        // Scroll pane for form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_FORM);
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        add(outerPanel);

        // ---- Listeners ----
        cbxType.addActionListener(e -> updateFieldsVisibility());
        updateFieldsVisibility();

        btnSave.addActionListener(e -> saveEmployee());
        btnCancel.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnSave);
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
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return field;
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(PRIMARY);
        lbl.setOpaque(true);
        lbl.setBackground(SECTION_BG);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return lbl;
    }

    private void updateFieldsVisibility() {
        String type = (String) cbxType.getSelectedItem();
        boolean isFullTime = "Toàn thời gian".equals(type);
        boolean isPartTime = "Bán thời gian".equals(type);
        boolean isManager = "Quản lý".equals(type);

        // Base salary, allowance, hours, rate, bonus row labels
        // Update field states based on employee type
        setFieldState(txtBaseSalary, isFullTime || isManager);
        setFieldState(txtAllowance, isFullTime);
        setFieldState(txtHours, isPartTime);
        setFieldState(txtRate, isPartTime);
        setFieldState(txtBonus, isManager);
    }

    private void setFieldState(JTextField field, boolean enabled) {
        field.setEnabled(enabled);
        field.setBackground(enabled ? FIELD_BG : new Color(0xF5F5F5));
    }

    private void loadEmployeeData(String id) {
        employee emp = dao.getEmployeeById(id);
        if (emp == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy nhân viên!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        txtId.setText(emp.getId());
        txtName.setText(emp.getName());
        txtAge.setText(String.valueOf(emp.getAge()));
        cbxType.setSelectedItem(emp.getType());

        // Set department
        if (emp.getDepartment() != null && !emp.getDepartment().isEmpty()) {
            boolean found = false;
            for (int i = 0; i < cbxDepartment.getItemCount(); i++) {
                if (cbxDepartment.getItemAt(i).equals(emp.getDepartment())) {
                    cbxDepartment.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cbxDepartment.setSelectedItem(emp.getDepartment());
            }
        }

        if (emp instanceof fullTimeEmployee) {
            fullTimeEmployee fte = (fullTimeEmployee) emp;
            txtBaseSalary.setText(String.valueOf(fte.getBaseSalary()));
            txtAllowance.setText(String.valueOf(fte.getAllowance()));
        } else if (emp instanceof partTimeEmployee) {
            partTimeEmployee pte = (partTimeEmployee) emp;
            txtHours.setText(String.valueOf(pte.getHoursWorked()));
            txtRate.setText(String.valueOf(pte.getHourlyRate()));
        } else if (emp instanceof manager) {
            manager mgr = (manager) emp;
            txtBaseSalary.setText(String.valueOf(mgr.getBaseSalary()));
            txtBonus.setText(String.valueOf(mgr.getBonus()));
        }

        updateFieldsVisibility();
    }

    private void saveEmployee() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Mã nhân viên và Họ tên không được để trống!",
                        "Lỗi nhập liệu",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int age = Integer.parseInt(txtAge.getText().trim());
            if (age <= 0 || age > 100) {
                JOptionPane.showMessageDialog(this,
                        "Tuổi phải từ 1 đến 100!",
                        "Lỗi nhập liệu",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String type = (String) cbxType.getSelectedItem();
            String dept = (String) cbxDepartment.getSelectedItem();
            if (dept == null) dept = "";
            employee emp = null;

            if ("Toàn thời gian".equals(type)) {
                double base = Double.parseDouble(txtBaseSalary.getText().trim());
                double allow = Double.parseDouble(txtAllowance.getText().trim());
                emp = new fullTimeEmployee(id, name, age, base, allow, dept);
            } else if ("Bán thời gian".equals(type)) {
                int hours = Integer.parseInt(txtHours.getText().trim());
                double rate = Double.parseDouble(txtRate.getText().trim());
                emp = new partTimeEmployee(id, name, age, hours, rate, dept);
            } else if ("Quản lý".equals(type)) {
                double base = Double.parseDouble(txtBaseSalary.getText().trim());
                double bonus = Double.parseDouble(txtBonus.getText().trim());
                emp = new manager(id, name, age, base, bonus, dept);
            }

            boolean success;
            if (editId == null) {
                success = dao.addEmployee(emp);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi lưu (có thể trùng mã nhân viên)!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                success = dao.updateEmployee(emp);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi cập nhật thông tin!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            parentView.loadDataToTable();
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đúng định dạng số cho Tuổi và các trường Lương!",
                    "Lỗi nhập liệu",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
