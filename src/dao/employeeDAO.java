package dao;

import model.*;
import utils.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class employeeDAO {

    public List<employee> getAllEmployees() {
        List<employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employee emp = extractEmployeeFromResultSet(rs);
                if (emp != null) list.add(emp); // Bỏ qua bản ghi type không hợp lệ
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<employee> searchEmployees(String keyword) {
        List<employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees WHERE id LIKE ? OR name LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String queryParam = "%" + keyword + "%";
            ps.setString(1, queryParam);
            ps.setString(2, queryParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employee emp = extractEmployeeFromResultSet(rs);
                    if (emp != null) list.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy một nhân viên theo ID — dùng cho chức năng Sửa
    public employee getEmployeeById(String id) {
        String sql = "SELECT * FROM Employees WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addEmployee(employee emp) {
        String sql = "INSERT INTO Employees (id, name, age, type, department, base_salary, allowance, hours_worked, hourly_rate, bonus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getId());
            ps.setString(2, emp.getName());
            ps.setInt(3, emp.getAge());
            ps.setString(4, emp.getType());
            ps.setString(5, emp.getDepartment());
            setEmployeeTypeParams(ps, emp, 6);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployee(employee emp) {
        String sql = "UPDATE Employees SET name=?, age=?, type=?, department=?, base_salary=?, allowance=?, hours_worked=?, hourly_rate=?, bonus=? WHERE id=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setInt(2, emp.getAge());
            ps.setString(3, emp.getType());
            ps.setString(4, emp.getDepartment());
            setEmployeeTypeParams(ps, emp, 5);
            ps.setString(10, emp.getId()); // WHERE id = ?

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEmployee(String id) {
        String sql = "DELETE FROM Employees WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== Pagination ====================

    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM Employees";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSearchCount(String keyword) {
        String sql = "SELECT COUNT(*) FROM Employees WHERE id LIKE ? OR name LIKE ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<employee> getEmployeesPage(int page, int pageSize) {
        List<employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees LIMIT ? OFFSET ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, page * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employee emp = extractEmployeeFromResultSet(rs);
                    if (emp != null) list.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<employee> searchEmployeesPage(String keyword, int page, int pageSize) {
        List<employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees WHERE id LIKE ? OR name LIKE ? LIMIT ? OFFSET ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String queryParam = "%" + keyword + "%";
            ps.setString(1, queryParam);
            ps.setString(2, queryParam);
            ps.setInt(3, pageSize);
            ps.setInt(4, page * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employee emp = extractEmployeeFromResultSet(rs);
                    if (emp != null) list.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== End Pagination ====================

    // Gán các tham số lương theo loại nhân viên, bắt đầu từ startIndex
    private void setEmployeeTypeParams(PreparedStatement ps, employee emp, int startIndex) throws SQLException {
        if (emp instanceof fullTimeEmployee) {
            fullTimeEmployee fte = (fullTimeEmployee) emp;
            ps.setDouble(startIndex,     fte.getBaseSalary());
            ps.setDouble(startIndex + 1, fte.getAllowance());
            ps.setInt   (startIndex + 2, 0);
            ps.setDouble(startIndex + 3, 0);
            ps.setDouble(startIndex + 4, 0);
        } else if (emp instanceof partTimeEmployee) {
            partTimeEmployee pte = (partTimeEmployee) emp;
            ps.setDouble(startIndex,     0);
            ps.setDouble(startIndex + 1, 0);
            ps.setInt   (startIndex + 2, pte.getHoursWorked());
            ps.setDouble(startIndex + 3, pte.getHourlyRate());
            ps.setDouble(startIndex + 4, 0);
        } else if (emp instanceof manager) {
            manager mgr = (manager) emp;
            ps.setDouble(startIndex,     mgr.getBaseSalary());
            ps.setDouble(startIndex + 1, 0);
            ps.setInt   (startIndex + 2, 0);
            ps.setDouble(startIndex + 3, 0);
            ps.setDouble(startIndex + 4, mgr.getBonus());
        }
    }

    private employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        String id   = rs.getString("id");
        String name = rs.getString("name");
        int age     = rs.getInt("age");
        String type = rs.getString("type");

        String department = rs.getString("department");

        switch (type) {
            case "Toàn thời gian":
                return new fullTimeEmployee(id, name, age, rs.getDouble("base_salary"), rs.getDouble("allowance"), department);
            case "Bán thời gian":
                return new partTimeEmployee(id, name, age, rs.getInt("hours_worked"), rs.getDouble("hourly_rate"), department);
            case "Quản lý":
                return new manager(id, name, age, rs.getDouble("base_salary"), rs.getDouble("bonus"), department);
            default:
                System.err.println("Loại nhân viên không hợp lệ trong DB: " + type + " (id=" + id + ")");
                return null;
        }
    }

    public List<employee> searchAdvanced(String keyword, String type, String department, Integer minAge, Integer maxAge, Double minSalary, Double maxSalary) {
        List<employee> all = getAllEmployees();
        List<employee> filtered = new java.util.ArrayList<>();
        for (employee emp : all) {
            if (emp == null) continue;
            // 1. Keyword check
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.toLowerCase().trim();
                boolean matchId = emp.getId().toLowerCase().contains(kw);
                boolean matchName = emp.getName().toLowerCase().contains(kw);
                if (!matchId && !matchName) continue;
            }
            // 2. Type check
            if (type != null && !type.equals("Tất cả")) {
                if (!emp.getType().equals(type)) continue;
            }
            // 3. Department check
            if (department != null && !department.equals("Tất cả phòng ban") && !department.isEmpty()) {
                if (!department.equals(emp.getDepartment())) continue;
            }
            // 4. Age range check
            if (minAge != null && emp.getAge() < minAge) continue;
            if (maxAge != null && emp.getAge() > maxAge) continue;
            
            // 5. Salary range check
            double salary = emp.calculateSalary();
            if (minSalary != null && salary < minSalary) continue;
            if (maxSalary != null && salary > maxSalary) continue;
            
            filtered.add(emp);
        }
        return filtered;
    }
}