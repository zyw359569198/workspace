package com.reign.util.buffer.test;

import com.reign.util.buffer.*;
import java.text.*;
import java.util.*;

public class Company
{
    private int id;
    private String name;
    private float f;
    private double d;
    private boolean in;
    private String info;
    private List<Employee> employeeList;
    
    public Company(final int id, final String name, final float f, final double d) {
        this.id = id;
        this.name = name;
        this.f = f;
        this.d = d;
        this.employeeList = new ArrayList<Employee>();
        this.in = false;
        this.info = "\u8d8a\u91ce\u662f\u5e05\u54e5!abc123!!!!!";
    }
    
    public Company(final IChannelBuffer buffer) {
        this.id = buffer.readInt();
        this.name = buffer.readString(20);
        this.f = buffer.readFloat();
        this.d = buffer.readDouble();
        this.employeeList = new ArrayList<Employee>();
        for (int employeeNum = buffer.readInt(), i = 0; i < employeeNum; ++i) {
            this.employeeList.add(new Employee(buffer));
        }
        this.in = buffer.readBoolean();
        this.info = buffer.readString();
    }
    
    public void addEmployee(final Employee employee) {
        this.employeeList.add(employee);
    }
    
    public void print() {
        System.out.println(MessageFormat.format("id={0},name={1},f={2},d={3},in={4},info={5}", this.id, this.name.trim(), this.f, this.d, this.in, this.info));
        System.out.println("employeeList");
        for (final Employee employee : this.employeeList) {
            employee.print();
        }
        System.out.println("-----------------");
    }
    
    public void writeBuffer(final IChannelBuffer buffer) {
        buffer.writeInt(this.id);
        buffer.writeString(this.name, 20);
        buffer.writeFloat(this.f);
        buffer.writeDouble(this.d);
        buffer.writeInt(this.employeeList.size());
        for (final Employee employee : this.employeeList) {
            employee.writeBuffer(buffer);
        }
        buffer.writeBoolean(this.in);
        buffer.writeString(this.info);
    }
}
