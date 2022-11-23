package com.zyc.buslist;

import java.util.ArrayList;
import java.util.List;

public class BusStation {
    String stopId;      //车站编号
    int stopOrder=0;    //车站在此线路序号
    String metro;       //地铁信息
    String name;
    int arrive=0;
    int pass=0;
    public List<String> busId= new ArrayList<>();//车站编号

    int arrive_double_deck=0;
    int pass_double_deck=0;
    int arrive_air_conditioner=0;
    int pass_air_conditioner=0;

    public int getArriveDoubleDeck() {
        return arrive_double_deck;
    }

    public void setArriveDoubleDeck(int arrive_double_deck) {
        this.arrive_double_deck = arrive_double_deck;
    }

    public int getPassDoubleDeck() {
        return pass_double_deck;
    }

    public void setPassDoubleDeck(int pass_double_deck) {
        this.pass_double_deck = pass_double_deck;
    }

    public int getArriveAirConditioner() {
        return arrive_air_conditioner;
    }

    public void setArriveAirConditioner(int arrive_air_conditioner) {
        this.arrive_air_conditioner = arrive_air_conditioner;
    }

    public int getPassAirConditioner() {
        return pass_air_conditioner;
    }

    public void setPassAirConditioner(int pass_air_conditioner) {
        this.pass_air_conditioner = pass_air_conditioner;
    }

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

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public String getMetro() {
        return metro;
    }

    public void setMetro(String metro) {
        this.metro = metro;
    }
}
