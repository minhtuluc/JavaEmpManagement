package model;

public class fullTimeEmployee extends employee {
    private double allowance;

    public fullTimeEmployee(String id, String name, int age, double baseSalary, double allowance, String department) {
        super(id, name, age, baseSalary, department);
        this.allowance = allowance;
    }

    public double getAllowance() { return allowance; }
    public void setAllowance(double allowance) { this.allowance = allowance; }

    @Override
    public double calculateSalary() {
        return baseSalary + allowance;
    }

    @Override
    public String getType() {
        return "Toàn thời gian";
    }
}