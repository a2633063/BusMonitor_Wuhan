package com.zyc.busmonitoritem;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class BusLine implements Serializable {
    String lineName;
    String lineId = "Error";
    String lineNo;
//    int direction;
    String startStopName = "起始站";
    String endStopName = "终点站";
    String firstTime = "XX:XX";
    String lastTime = "XX:XX";
    String price="";
    String line2Id = "";

    int selected = -1;
    int stopsNum = 0;


    public BusLine(String lineName, String LineNo, int selected) {
        this.lineName = lineName;
        this.lineNo = LineNo;
        this.selected = selected;
    }


    //region Getter and Setter
    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

//    public int getDirection() {
//        return direction;
//    }
//
//    public void setDirection(int direction) {
//        this.direction = direction;
//    }

    public String getStartStopName() {
        return startStopName;
    }

    public void setStartStopName(String startStopName) {
        this.startStopName = startStopName;
    }

    public String getEndStopName() {
        return endStopName;
    }

    public void setEndStopName(String endStopName) {
        this.endStopName = endStopName;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLine2Id() {
        return line2Id;
    }

    public void setLine2Id(String line2Id) {
        this.line2Id = line2Id;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getStopsNum() {
        return stopsNum;
    }

    public void setStopsNum(int stopsNum) {
        this.stopsNum = stopsNum;
    }
    //endregion


}
