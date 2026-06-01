package view;

import dao.employeeDAO;
import model.*;
import utils.IconUtils;

import javax.swing.*;
import java.awt.*;

public class employeeDetailView extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color BG_FORM = new Color(0xFAFAFA);
    private static final Color LABEL_COLOR = new Color(0x202124);
    private static final Color VALUE_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xDADCE0);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color SECTION_BG = new Color(0xF0F4FF);

    public employeeDetailView(JFrame parent, String employeeId) {
        super(parent, "Chi tiết nhân viên", true);
        setResizable(false);
        IconUtils.setFrameIcon(this);

        employeeDAO dao = new employeeDAO();
        employee emp = dao.getEmployeeById(employeeId);
        if (emp == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy nhân viên!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initComponents(emp);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(employee emp) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BG_FORM);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel headerIcon = new JLabel(IconUtils.getEmployeeIcon(24));
        headerIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel headerTitle = new JLabel("Thông tin nhân viên");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);

        JPanel headerInner = new JPanel(new BorderLayout());
        headerInner.setBackground(PRIMARY);
        headerInner.add(headerIcon, BorderLayout.WEST);
        headerInner.add(headerTitle, BorderLayout.CENTER);
        headerPanel.add(headerInner, BorderLayout.WEST);
        outerPanel.add(headerPanel, BorderLayout.NORTH);

        // Body
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_FORM);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Section: Basic info
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(createSectionLabel("Thông tin cơ bản"), gbc);

        addDetailRow(formPanel, gbc, 1, "Mã nhân viên", emp.getId());
        addDetailRow(formPanel, gbc, 2, "Họ và tên", emp.getName());
        addDetailRow(formPanel, gbc, 3, "Tuổi", String.valueOf(emp.getAge()));
        addDetailRow(formPanel, gbc, 4, "Loại nhân viên", emp.getType());
        addDetailRow(formPanel, gbc, 5, "Phòng ban", emp.getDepartment() != null ? emp.getDepartment() : "");

        // Section: Salary details
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 0, 10, 0);
        formPanel.add(createSectionLabel("Chi tiết lương"), gbc);

        int row = 7;
        if (emp instanceof fullTimeEmployee) {
            fullTimeEmployee fte = (fullTimeEmployee) emp;
            addDetailRow(formPanel, gbc, row++, "Lương cơ bản", String.format("%,.0f VND", fte.getBaseSalary()));
            addDetailRow(formPanel, gbc, row++, "Phụ cấp", String.format("%,.0f VND", fte.getAllowance()));
        } else if (emp instanceof partTimeEmployee) {
            partTimeEmployee pte = (partTimeEmployee) emp;
            addDetailRow(formPanel, gbc, row++, "Số giờ làm", String.valueOf(pte.getHoursWorked()) + " giờ");
            addDetailRow(formPanel, gbc, row++, "Lương mỗi giờ", String.format("%,.0f VND", pte.getHourlyRate()));
        } else if (emp instanceof manager) {
            manager mgr = (manager) emp;
            addDetailRow(formPanel, gbc, row++, "Lương cơ bản", String.format("%,.0f VND", mgr.getBaseSalary()));
            addDetailRow(formPanel, gbc, row++, "Tiền thưởng", String.format("%,.0f VND", mgr.getBonus()));
        }

        // Total salary
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        totalPanel.setBackground(BG_FORM);
        JLabel totalLabel = new JLabel("Tổng lương: " + String.format("%,.0f VND", emp.calculateSalary()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY);
        totalPanel.add(totalLabel);
        formPanel.add(totalPanel, gbc);

        // Close button
        gbc.gridy = row + 1;
        gbc.insets = new Insets(16, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(PRIMARY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setMargin(new Insets(8, 24, 8, 24));
        btnClose.setPreferredSize(new Dimension(btnClose.getPreferredSize().width, 36));
        btnClose.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG_FORM);
        btnPanel.add(btnClose);
        formPanel.add(btnPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_FORM);
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        add(outerPanel);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(LABEL_COLOR);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setOpaque(true);
        val.setBackground(VALUE_BG);
        val.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        panel.add(val, gbc);
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
}
