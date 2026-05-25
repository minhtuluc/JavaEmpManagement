package view;

import dao.employeeDAO;
import model.*;
import utils.IconUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class statisticsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color BG_FORM = new Color(0xFAFAFA);
    private static final Color PRIMARY = new Color(0x1A73E8);
    private static final Color COLOR_FULLTIME = new Color(0x34A853);
    private static final Color COLOR_PARTTIME = new Color(0xFBBC05);
    private static final Color COLOR_MANAGER = new Color(0xEA4335);

    private transient List<employee> allEmployees;

    public statisticsDialog(JFrame parent) {
        super(parent, "Thống kê nhân sự", true);
        setResizable(false);
        IconUtils.setFrameIcon(this);

        employeeDAO dao = new employeeDAO();
        allEmployees = dao.getAllEmployees();

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_FORM);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel headerTitle = new JLabel("Thống kê nhân sự");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);
        header.add(headerTitle, BorderLayout.WEST);
        outer.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_FORM);
        content.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Compute stats
        int total = allEmployees.size();
        int fullTimeCount = 0, partTimeCount = 0, managerCount = 0;
        double totalSalary = 0;

        for (employee e : allEmployees) {
            if (e == null) continue;
            totalSalary += e.calculateSalary();
            String t = e.getType();
            if ("Toàn thời gian".equals(t)) fullTimeCount++;
            else if ("Bán thời gian".equals(t)) partTimeCount++;
            else if ("Quản lý".equals(t)) managerCount++;
        }

        double avgSalary = total > 0 ? totalSalary / total : 0;

        // Count by department
        Map<String, Integer> deptCount = new HashMap<>();
        Map<String, Double> deptSalary = new HashMap<>();
        for (employee e : allEmployees) {
            if (e == null) continue;
            String d = e.getDepartment() != null && !e.getDepartment().isEmpty() ? e.getDepartment() : "Chưa có";
            deptCount.put(d, deptCount.getOrDefault(d, 0) + 1);
            deptSalary.put(d, deptSalary.getOrDefault(d, 0.0) + e.calculateSalary());
        }

        // Stat cards row 1: counts
        JPanel cardsPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        cardsPanel.setBackground(BG_FORM);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        cardsPanel.add(createStatCard("Tổng NV", String.valueOf(total), PRIMARY));
        cardsPanel.add(createStatCard("Toàn thời gian", String.valueOf(fullTimeCount), COLOR_FULLTIME));
        cardsPanel.add(createStatCard("Bán thời gian", String.valueOf(partTimeCount), COLOR_PARTTIME));
        cardsPanel.add(createStatCard("Quản lý", String.valueOf(managerCount), COLOR_MANAGER));

        content.add(cardsPanel);

        // Stat cards row 2: salary
        JPanel salaryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        salaryPanel.setBackground(BG_FORM);
        salaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        salaryPanel.add(createStatCard("Tổng lương", String.format("%,.0f VND", totalSalary), PRIMARY));
        salaryPanel.add(createStatCard("Lương TB", String.format("%,.0f VND", avgSalary), PRIMARY));
        content.add(salaryPanel);

        // Department breakdown
        if (!deptCount.isEmpty()) {
            JLabel deptSection = new JLabel("Thống kê theo phòng ban");
            deptSection.setFont(new Font("Segoe UI", Font.BOLD, 13));
            deptSection.setForeground(PRIMARY);
            deptSection.setAlignmentX(Component.LEFT_ALIGNMENT);
            deptSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            content.add(deptSection);

            for (Map.Entry<String, Integer> entry : deptCount.entrySet()) {
                double deptTotal = deptSalary.getOrDefault(entry.getKey(), 0.0);
                JPanel deptRow = new JPanel(new BorderLayout(8, 0));
                deptRow.setBackground(BG_FORM);
                deptRow.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

                JLabel nameLabel = new JLabel(entry.getKey());
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                nameLabel.setPreferredSize(new Dimension(140, 20));
                deptRow.add(nameLabel, BorderLayout.WEST);

                JLabel countLabel = new JLabel(entry.getValue() + " NV  -  "
                        + String.format("%,.0f VND", deptTotal));
                countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                deptRow.add(countLabel, BorderLayout.EAST);

                content.add(deptRow);
            }
            content.add(Box.createVerticalStrut(12));
        }

        // Bar chart
        JLabel chartLabel = new JLabel("Biểu đồ số lượng theo loại");
        chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chartLabel.setForeground(PRIMARY);
        chartLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        content.add(chartLabel);

        BarChartPanel chart = new BarChartPanel(
            new String[]{"Toàn thời gian", "Bán thời gian", "Quản lý"},
            new int[]{fullTimeCount, partTimeCount, managerCount},
            new Color[]{COLOR_FULLTIME, COLOR_PARTTIME, COLOR_MANAGER}
        );
        chart.setPreferredSize(new Dimension(500, 180));
        chart.setMaximumSize(new Dimension(500, 180));
        chart.setBackground(Color.WHITE);
        chart.setBorder(BorderFactory.createLineBorder(new Color(0xDADCE0), 1, true));
        content.add(chart);

        // Close button
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(PRIMARY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.putClientProperty("JButton.buttonType", "roundRect");
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setMargin(new Insets(8, 24, 8, 24));
        btnClose.putClientProperty("JButton.minimumWidth", 120);
        btnClose.setPreferredSize(new Dimension(btnClose.getPreferredSize().width, 36));
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.addActionListener(e -> dispose());
        content.add(Box.createVerticalStrut(8));
        content.add(btnClose);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_FORM);
        outer.add(scrollPane, BorderLayout.CENTER);

        add(outer);
    }

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDADCE0), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(0x5F6368));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(accent);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private static class BarChartPanel extends JPanel {
        private final String[] labels;
        private final int[] values;
        private final Color[] colors;
        private int maxValue;

        BarChartPanel(String[] labels, int[] values, Color[] colors) {
            this.labels = labels;
            this.values = values;
            this.colors = colors;
            this.maxValue = 1;
            for (int v : values) {
                if (v > maxValue) maxValue = v;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 30;
            int chartW = w - pad * 2;
            int chartH = h - pad * 2 - 20;
            int barCount = values.length;
            int barWidth = chartW / (barCount * 2 + 1);
            int gap = barWidth;

            for (int i = 0; i < barCount; i++) {
                int barH = (int) ((double) values[i] / maxValue * chartH);
                int x = pad + gap + i * (barWidth + gap);
                int y = pad + chartH - barH;

                g2.setColor(colors[i]);
                g2.fill(new RoundRectangle2D.Float(x, y, barWidth, barH, 4, 4));

                g2.setColor(new Color(0x202124));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String label = labels[i];
                int labelW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, x + (barWidth - labelW) / 2, pad + chartH + 14);

                String valStr = String.valueOf(values[i]);
                int valW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - valW) / 2, y - 4);
            }

            g2.dispose();
        }
    }
}
