package com.zyc.buslist;

public class BusStation {
//    String stopId;      //车站编号
//    int stopOrder=0;    //车站在此线路序号
//    String metro;       //地铁信息
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

//    public String getStopId() {
//        return stopId;
//    }
//
//    public void setStopId(String stopId) {
//        this.stopId = stopId;
//    }
//
//    public int getStopOrder() {
//        return stopOrder;
//    }
//
//    public void setStopOrder(int stopOrder) {
//        this.stopOrder = stopOrder;
//    }
//
//    public String getMetro() {
//        return metro;
//    }
//
//    public void setMetro(String metro) {
//        this.metro = metro;
//    }
}
