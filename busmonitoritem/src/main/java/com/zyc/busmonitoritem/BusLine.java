package com.zyc.busmonitoritem;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class BusLine implements Serializable {
    String lineName;
    String lineNo;
    int direction;
    int selected = -1;
    String startStopName = "起始站";
    String endStopName = "终点站";
    String firstTime = "XX:XX";
    String lastTime = "XX:XX";
    String line2Id = "Error";
    String lineId = "Error";
    int stopsNum = 0;


    public BusLine(String lineName, String LineNo, int direction) {
        this.lineName = lineName;
        this.lineNo = LineNo;
        this.direction = direction;
    }

    public BusLine(String lineName, String LineNo, int direction, int selected) {
        this.lineName = lineName;
        this.lineNo = LineNo;
        this.direction = direction;
        this.selected = selected;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public void setEndStopName(String endStopName) {
        this.endStopName = endStopName;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public void setStartStopName(String startStopName) {
        this.startStopName = startStopName;
    }

    public void setStopsNum(int stopsNum) {
        this.stopsNum = stopsNum;
    }

    public int getDirection() {
        return direction;
    }

    public String getLineName() {
        return lineName;
    }

    public String getLineNo() {
        return lineNo;
    }

    public int getStopsNum() {
        return stopsNum;
    }

    public String getEndStopName() {
        return endStopName;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public String getStartStopName() {
        return startStopName;
    }

    public String getLastTime() {
        return lastTime;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getLine2Id() {
        return line2Id;
    }

    public void setLine2Id(String line2Id) {
        this.line2Id = line2Id;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public void setLine(String lineId, String line2Id) {
        this.lineId = lineId;
        this.line2Id = line2Id;
    }

    public String getLine() {
        if (getDirection() == 0) return getLineId();
        else return getLine2Id();
    }
}
