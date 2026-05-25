package model;

public abstract class employee implements iManageable {
    protected String id;
    protected String name;
    protected int age;
    protected double baseSalary;
    protected String department;

    public employee(String id, String name, int age, double baseSalary, String department) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.baseSalary = baseSalary;
        this.department = department;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public abstract double calculateSalary();

    @Override
    public abstract String getType();
}