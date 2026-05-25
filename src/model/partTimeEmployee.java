package model;

public class partTimeEmployee extends employee {
    private int hoursWorked;
    private double hourlyRate;

    public partTimeEmployee(String id, String name, int age, int hoursWorked, double hourlyRate, String department) {
        super(id, name, age, 0, department); // Part-time khong co luong co ban
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    public int getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(int hoursWorked) { this.hoursWorked = hoursWorked; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }

    @Override
    public String getType() {
        return "Bán thời gian";
    }
}