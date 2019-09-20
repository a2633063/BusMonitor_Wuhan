package com.zyc.buslist;

public class BusStation {
    String name;
    int arrive=0;
    int pass=0;

    public BusStation(String s){
        name=s;
    }
    public void setArrive(int arrive) {
        this.arrive = arrive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public int getArrive() {
        return arrive;
    }

    public int getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }
}
