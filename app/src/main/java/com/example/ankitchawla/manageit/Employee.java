package com.example.ankitchawla.manageit;

import java.io.Serializable;



public class Employee implements Serializable{
    private int eid;
    private String datej;
    private int salary;
    private String name;
    private String address;
    private String Department;

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getEmpImgUrl() {
        return empImgUrl;
    }

    public void setEmpImgUrl(String empImgUrl) {
        this.empImgUrl = empImgUrl;
    }

    String empImgUrl;
    Employee(){}
    Employee(int id,String name,String addresss,int salary,String datej,String empImgUrl,String Department)
    {
        this.eid=id;
        this.name=name;
        this.address=addresss;
        this.salary=salary;
        this.datej=datej;
        this.empImgUrl=empImgUrl;
        this.Department=Department;


    }

    public int geteid() {
        return eid;
    }

    public void seteid(int id) {
        this.eid = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String address) {
        this.address = address;
    }
    public int getsalary() {
        return salary;
    }

    public void setsalary(int salary) {
        this.salary = salary;
    }
    public String getdatej() {
        return datej;
    }

    public void setdatej(String doj) {
        this.datej = doj;
    }


}
