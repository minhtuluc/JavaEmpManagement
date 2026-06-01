package view;

import dao.employeeDAO;
import model.employee;
import model.fullTimeEmployee;
import model.partTimeEmployee;
import model.manager;
import model.user;
import utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class mainView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private transient TableRowSorter<DefaultTableModel> sorter;
    private transient employeeDAO dao;
    private transient user currentUser;
    private JComboBox<String> cbxDepartmentFilter;
    private JTextField txtSearch;

    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private int totalPages = 0;
    private String currentKeyword = "";
    private JButton btnPrev, btnNext;
    private JLabel pageLabel, statusLabel;

    // Color constants matching the enterprise theme
    private static final Color BG_TOP = new Color(0xFFFFFF);
    private static final Color BORDER_COLOR = new Color(0xDADCE0);
    private static final Color ROW_ALT = new Color(0xF8F9FA);
    private static final Color TABLE_HEADER_BG = new Color(0xF1F3F4);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color STATUS_BG = new Color(0xF5F7FA);

    public mainView(user user) {
        this.currentUser = user;
        this.dao = new employeeDAO();

        setTitle("Quản lý Nhân sự");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        IconUtils.setFrameIcon(this);

        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        boolean isDark = false;
        
        // Main container panel with a clean background
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(isDark ? new Color(0x111827) : new Color(0xF8FAFC)); // Dark Gray vs Slate 50

        // ---- North Panel: Header + Toolbar ----
        JPanel northPanel = new JPanel(new GridBagLayout());
        northPanel.setBackground(isDark ? new Color(0x1F2937) : Color.WHITE); // Dark Gray vs White

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Header (Brand Title & User Info)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(isDark ? new Color(0x1F2937) : Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JLabel brandLabel = new JLabel("HỆ THỐNG QUẢN LÝ NHÂN SỰ", IconUtils.getEmployeeIcon(22), SwingConstants.LEFT);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brandLabel.setForeground(isDark ? new Color(0xF9FAFB) : new Color(0x0F172A)); // White vs Slate 900
        brandLabel.setIconTextGap(10);
        headerPanel.add(brandLabel, BorderLayout.WEST);

        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightInfo.setBackground(isDark ? new Color(0x1F2937) : Color.WHITE);
        rightInfo.setBorder(null);
        
        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(PRIMARY);
        
        JLabel roleLabel = new JLabel(currentUser.getRole() == 1 ? "(Quản trị)" : "(Xem)");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(isDark ? new Color(0x9CA3AF) : new Color(0x64748B)); // Slate 400 vs Slate 500

        rightInfo.add(new JLabel(IconUtils.getPersonIcon(IconUtils.SIZE_SMALL)));
        rightInfo.add(Box.createHorizontalStrut(6));
        rightInfo.add(userLabel);
        rightInfo.add(Box.createHorizontalStrut(6));
        rightInfo.add(roleLabel);
        rightInfo.add(Box.createHorizontalStrut(16));

        if (currentUser.getRole() == 1) {
            JButton btnUserManage = createModernButton("Tài khoản", new Color(0xE8F0FE), PRIMARY);
            btnUserManage.setPreferredSize(new Dimension(100, 32));
            btnUserManage.addActionListener(e -> {
                userManagementDialog dialog = new userManagementDialog(mainView.this);
                dialog.setVisible(true);
            });
            rightInfo.add(btnUserManage);
            rightInfo.add(Box.createHorizontalStrut(6));
        }

        JButton btnLogout = createModernButton("Đăng xuất", new Color(0xFEE2E2), new Color(0xDC2626));
        btnLogout.setPreferredSize(new Dimension(105, 32));
        rightInfo.add(btnLogout);
        headerPanel.add(rightInfo, BorderLayout.EAST);
        
        gbc.gridy = 0;
        northPanel.add(headerPanel, gbc);

        // 2. Thin separator line
        JSeparator headerSep = new JSeparator();
        headerSep.setForeground(isDark ? new Color(0x374151) : new Color(0xE2E8F0)); // Slate 700 vs Slate 200
        gbc.gridy = 1;
        northPanel.add(headerSep, gbc);

        // 3. Toolbar (Actions + Search & Filters)
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBackground(isDark ? new Color(0x1F2937) : Color.WHITE);
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(12, 24, 16, 24));

        // Left Actions: CRUD Buttons
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        crudPanel.setBackground(isDark ? new Color(0x1F2937) : Color.WHITE);

        JButton btnAdd = createModernButton("Thêm mới", PRIMARY, Color.WHITE);
        JButton btnEdit = createModernButton("Chỉnh sửa", new Color(0xDCFCE7), new Color(0x15803D));
        JButton btnDelete = createModernButton("Xóa bỏ", new Color(0xFEE2E2), new Color(0xB91C1C));

        Dimension crudBtnSize = new Dimension(110, 32);
        btnAdd.setPreferredSize(crudBtnSize);
        btnEdit.setPreferredSize(crudBtnSize);
        btnDelete.setPreferredSize(crudBtnSize);

        if (currentUser.getRole() == 0) {
            btnAdd.setVisible(false);
            btnEdit.setVisible(false);
            btnDelete.setVisible(false);
        }
        crudPanel.add(btnAdd);
        crudPanel.add(Box.createHorizontalStrut(8));
        crudPanel.add(btnEdit);
        crudPanel.add(Box.createHorizontalStrut(8));
        crudPanel.add(btnDelete);
        toolbarPanel.add(crudPanel, BorderLayout.WEST);

        // Right Actions: Search & Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterPanel.setBackground(isDark ? new Color(0x1F2937) : Color.WHITE);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setPreferredSize(new Dimension(170, 32));

        JButton btnAdvancedSearch = createModernButton("Lọc nâng cao", new Color(0xF1F5F9), new Color(0x475569));
        btnAdvancedSearch.setPreferredSize(new Dimension(105, 32));

        cbxDepartmentFilter = new JComboBox<>();
        cbxDepartmentFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbxDepartmentFilter.setPreferredSize(new Dimension(125, 32));
        refreshDepartmentFilter();

        JButton btnRefresh = createModernButton("Làm mới", new Color(0xF1F5F9), new Color(0x475569));
        btnRefresh.setPreferredSize(new Dimension(100, 32));
        btnRefresh.setToolTipText("Làm mới danh sách");

        JButton btnStats = createModernButton("Thống kê", new Color(0xEFF6FF), new Color(0x1D4ED8));
        btnStats.setPreferredSize(new Dimension(100, 32));
        
        JButton btnExport = createModernButton("Xuất CSV", new Color(0xD1FAE5), new Color(0x065F46));
        btnExport.setPreferredSize(new Dimension(105, 32));

        filterPanel.add(txtSearch);
        filterPanel.add(Box.createHorizontalStrut(6));
        filterPanel.add(btnAdvancedSearch);
        filterPanel.add(Box.createHorizontalStrut(12));
        filterPanel.add(new JLabel("Phòng: "));
        filterPanel.add(Box.createHorizontalStrut(4));
        filterPanel.add(cbxDepartmentFilter);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(btnRefresh);
        filterPanel.add(Box.createHorizontalStrut(6));
        filterPanel.add(btnStats);
        filterPanel.add(Box.createHorizontalStrut(6));
        filterPanel.add(btnExport);
        toolbarPanel.add(filterPanel, BorderLayout.EAST);

        gbc.gridy = 2;
        northPanel.add(toolbarPanel, gbc);
        mainContainer.add(northPanel, BorderLayout.NORTH);

        // ---- Center Panel: Table inside a Card Wrapper ----
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(isDark ? new Color(0x111827) : new Color(0xF8FAFC));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24)); // Margins around table

        String[] columnNames = {"Mã NV", "Họ và Tên", "Tuổi", "Loại", "Phòng ban", "Tổng Lương (VNĐ)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(38); // More breathing room
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xE8F0FE));
        table.setSelectionForeground(new Color(0x202124));

        // Alternating row colors renderer
        DefaultTableCellRenderer stripeRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(stripeRenderer);
        }

        // Alignments and widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(220);  // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(60);   // Age
        table.getColumnModel().getColumn(3).setPreferredWidth(110);  // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(140);  // Department
        table.getColumnModel().getColumn(5).setPreferredWidth(130);  // Salary

        // Modern Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(new Color(0x475569)); // Slate 600
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        // Table Sorting
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1)); // Square border
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(tableWrapper, BorderLayout.CENTER);

        // ---- South Panel: Pagination + Status ----
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(isDark ? new Color(0x111827) : new Color(0xF8FAFC));

        // Pagination Bar
        JPanel paginationBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        paginationBar.setBackground(isDark ? new Color(0x111827) : new Color(0xF8FAFC));
        paginationBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));

        btnPrev = createModernButton("< Trước", new Color(0xF1F5F9), new Color(0x475569));
        btnPrev.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnPrev.setEnabled(false);
        
        btnNext = createModernButton("Sau >", new Color(0xF1F5F9), new Color(0x475569));
        btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnNext.setEnabled(false);

        pageLabel = new JLabel("Trang 1 / 1");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageLabel.setForeground(new Color(0x64748B));

        paginationBar.add(btnPrev);
        paginationBar.add(pageLabel);
        paginationBar.add(btnNext);
        southPanel.add(paginationBar, BorderLayout.NORTH);

        // Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(STATUS_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 24, 6, 24)));

        statusLabel = new JLabel("Hệ thống hoạt động ổn định");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0x64748B));
        statusBar.add(statusLabel, BorderLayout.WEST);
        southPanel.add(statusBar, BorderLayout.SOUTH);

        mainContainer.add(southPanel, BorderLayout.SOUTH);
        add(mainContainer, BorderLayout.CENTER);

        // ---- Listeners & Event Handlers ----
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainView.this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                mainView.this.dispose();
                SwingUtilities.invokeLater(() -> new loginView().setVisible(true));
            }
        });

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            currentKeyword = "";
            if (cbxDepartmentFilter.getItemCount() > 0) {
                cbxDepartmentFilter.setSelectedIndex(0);
            }
            loadDataToTable();
            statusLabel.setText("Danh sách đã được làm mới");
        });

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { triggerSearch(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { triggerSearch(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { triggerSearch(); }
        });

        btnAdvancedSearch.addActionListener(e -> {
            advancedSearchDialog dialog = new advancedSearchDialog(mainView.this);
            dialog.setVisible(true);
        });

        cbxDepartmentFilter.addActionListener(e -> {
            String selected = (String) cbxDepartmentFilter.getSelectedItem();
            if (selected == null || "Tất cả phòng ban".equals(selected)) {
                loadDataToTable();
            } else {
                java.util.List<employee> filtered = new java.util.ArrayList<>();
                for (employee emp : dao.getAllEmployees()) {
                    if (selected.equals(emp.getDepartment())) {
                        filtered.add(emp);
                    }
                }
                loadDataToTable(filtered);
            }
        });

        btnStats.addActionListener(e -> {
            statisticsDialog stats = new statisticsDialog(mainView.this);
            stats.setVisible(true);
        });

        btnExport.addActionListener(e -> exportToCSV());

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhân viên cần xóa!",
                        "Chưa chọn dữ liệu",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);
            String id = tableModel.getValueAt(modelRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa nhân viên mã " + id + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deleteEmployee(id)) {
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                    loadDataToTable();
                    statusLabel.setText("Đã xóa nhân viên mã: " + id);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi xóa nhân viên!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAdd.addActionListener(e -> {
            employeeDialog dialog = new employeeDialog(this, null);
            dialog.setVisible(true);
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhân viên cần sửa!",
                        "Chưa chọn dữ liệu",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String id = tableModel.getValueAt(modelRow, 0).toString();
            employeeDialog dialog = new employeeDialog(this, id);
            dialog.setVisible(true);
        });

        btnPrev.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadCurrentPage();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                loadCurrentPage();
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        String id = tableModel.getValueAt(modelRow, 0).toString();
                        if (currentUser.getRole() != 0) {
                            employeeDialog dialog = new employeeDialog(mainView.this, id);
                            dialog.setVisible(true);
                        } else {
                            employeeDetailView detail = new employeeDetailView(mainView.this, id);
                            detail.setVisible(true);
                        }
                    }
                }
            }
        });
    }

    private void triggerSearch() {
        String keyword = txtSearch.getText().trim();
        currentKeyword = keyword;
        if (!keyword.isEmpty()) {
            int total = dao.getSearchCount(keyword);
            totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;
            currentPage = 0;
            loadCurrentPage();
            statusLabel.setText("Kết quả tìm kiếm: " + keyword);
        } else {
            loadDataToTable();
            statusLabel.setText("Hiển thị tất cả nhân viên");
        }
    }

    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); // Make flat borderless
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 10, 4, 10));
        
        // Force height to 32, keep auto-width
        int width = btn.getPreferredSize().width;
        btn.setPreferredSize(new Dimension(width, 32));
        return btn;
    }

    public void loadDataToTable() {
        currentPage = 0;
        currentKeyword = "";
        int total = dao.getEmployeeCount();
        totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        refreshDepartmentFilter();
        loadCurrentPage();
    }

    public void loadDataToTable(List<employee> list) {
        tableModel.setRowCount(0);
        for (employee emp : list) {
            if (emp == null) continue;
            Object[] row = {
                    emp.getId(),
                    emp.getName(),
                    emp.getAge(),
                    emp.getType(),
                    emp.getDepartment() != null ? emp.getDepartment() : "",
                    String.format("%,.0f ₫", emp.calculateSalary())
            };
            tableModel.addRow(row);
        }
        currentPage = 0;
        totalPages = 1;
        updatePaginationButtons();
    }

    private void loadCurrentPage() {
        java.util.List<employee> list;
        if (currentKeyword.isEmpty()) {
            list = dao.getEmployeesPage(currentPage, PAGE_SIZE);
        } else {
            list = dao.searchEmployeesPage(currentKeyword, currentPage, PAGE_SIZE);
        }
        tableModel.setRowCount(0);
        for (employee emp : list) {
            if (emp == null) continue;
            Object[] row = {
                    emp.getId(),
                    emp.getName(),
                    emp.getAge(),
                    emp.getType(),
                    emp.getDepartment() != null ? emp.getDepartment() : "",
                    String.format("%,.0f ₫", emp.calculateSalary())
            };
            tableModel.addRow(row);
        }
        updatePaginationButtons();
    }

    private void updatePaginationButtons() {
        pageLabel.setText("Trang " + (currentPage + 1) + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
    }

    private void refreshDepartmentFilter() {
        String currentSelection = cbxDepartmentFilter.getSelectedItem() != null
                ? (String) cbxDepartmentFilter.getSelectedItem() : null;
        cbxDepartmentFilter.removeAllItems();
        cbxDepartmentFilter.addItem("Tất cả phòng ban");
        java.util.Set<String> depts = new java.util.TreeSet<>();
        for (employee e : dao.getAllEmployees()) {
            if (e.getDepartment() != null && !e.getDepartment().isEmpty()) {
                depts.add(e.getDepartment());
            }
        }
        for (String d : depts) {
            cbxDepartmentFilter.addItem(d);
        }
        if (currentSelection != null) {
            for (int i = 0; i < cbxDepartmentFilter.getItemCount(); i++) {
                if (cbxDepartmentFilter.getItemAt(i).equals(currentSelection)) {
                    cbxDepartmentFilter.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void exportToCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất danh sách nhân viên ra CSV");
        chooser.setSelectedFile(new java.io.File("danh_sach_nhan_vien.csv"));

        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "CSV files (*.csv)", "csv"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new java.io.File(file.getAbsolutePath() + ".csv");
        }

        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(file),
                java.nio.charset.StandardCharsets.UTF_8))) {

            // Write BOM for Excel compatibility with Vietnamese
            pw.write('\uFEFF');

            // Header row
            pw.println("Mã NV,Họ và Tên,Tuổi,Loại,Phòng ban,Lương cơ bản,Phụ cấp/Số giờ/Lương giờ/Thưởng,Tổng lương");

            java.util.List<employee> list = dao.getAllEmployees();
            for (employee emp : list) {
                if (emp == null) continue;
                StringBuilder line = new StringBuilder();
                line.append(csvEscape(emp.getId())).append(",");
                line.append(csvEscape(emp.getName())).append(",");
                line.append(emp.getAge()).append(",");
                line.append(csvEscape(emp.getType())).append(",");
                line.append(csvEscape(emp.getDepartment())).append(",");

                String extraField = "";
                if (emp instanceof fullTimeEmployee) {
                    fullTimeEmployee fte = (fullTimeEmployee) emp;
                    line.append(fte.getBaseSalary()).append(",");
                    extraField = "Phụ cấp: " + String.format("%,.0f", fte.getAllowance());
                } else if (emp instanceof partTimeEmployee) {
                    partTimeEmployee pte = (partTimeEmployee) emp;
                    line.append("0,");
                    extraField = "Số giờ: " + pte.getHoursWorked() + ", Lương giờ: " + pte.getHourlyRate();
                } else if (emp instanceof manager) {
                    manager mgr = (manager) emp;
                    line.append(mgr.getBaseSalary()).append(",");
                    extraField = "Thưởng: " + String.format("%,.0f", mgr.getBonus());
                }

                line.append(csvEscape(extraField)).append(",");
                line.append(String.format("%,.0f", emp.calculateSalary()));
                pw.println(line.toString());
            }

            JOptionPane.showMessageDialog(this,
                    "Xuất dữ liệu thành công!\nFile: " + file.getName(),
                    "Xuất CSV",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất CSV:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public user getCurrentUser() {
        return currentUser;
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
