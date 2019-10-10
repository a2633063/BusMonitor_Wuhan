package com.zyc.busmonitoritem;

public class BusLine {
    String lineName;
    String lineNo;
    int direction;

    public BusLine(String lineName, String LineNo, int direction) {
        this.lineName = lineName;
        this.lineNo = LineNo;
        this.direction = direction;
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

    public int getDirection() {
        return direction;
    }

    public String getLineName() {
        return lineName;
    }

    public String getLineNo() {
        return lineNo;
    }
}
