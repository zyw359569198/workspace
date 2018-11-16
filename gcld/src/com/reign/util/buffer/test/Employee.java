package com.reign.util.buffer.test;

import com.reign.util.buffer.*;
import java.text.*;

public class Employee
{
    private int id;
    private String name;
    boolean boy;
    boolean cute;
    
    public Employee(final int id, final String name, final boolean boy, final boolean cute) {
        this.id = id;
        this.name = name;
        this.boy = boy;
        this.cute = cute;
    }
    
    public Employee(final IChannelBuffer buffer) {
        this.id = buffer.readInt();
        this.name = buffer.readString();
        this.boy = buffer.readBoolean();
        this.cute = buffer.readBoolean();
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isBoy() {
        return this.boy;
    }
    
    public void setBoy(final boolean boy) {
        this.boy = boy;
    }
    
    public boolean isCute() {
        return this.cute;
    }
    
    public void setCute(final boolean cute) {
        this.cute = cute;
    }
    
    public void print() {
        System.out.println(MessageFormat.format("id={0},name={1},isBoy={2},cute={3}", this.id, this.name, this.boy, this.cute));
    }
    
    public void writeBuffer(final IChannelBuffer buffer) {
        buffer.writeInt(this.id);
        buffer.writeString(this.name);
        buffer.writeBoolean(this.boy);
        buffer.writeBoolean(this.cute);
    }
}
