package model;

public class manager extends employee {
    private double bonus;

    public manager(String id, String name, int age, double baseSalary, double bonus, String department) {
        super(id, name, age, baseSalary, department);
        this.bonus = bonus;
    }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }

    @Override
    public double calculateSalary() {
        return baseSalary + bonus;
    }

    @Override
    public String getType() {
        return "Quản lý";
    }
}