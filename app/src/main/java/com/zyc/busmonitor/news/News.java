package com.zyc.busmonitor.news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class News {
    private int serialNo;
    private String topic;
    private String simpleText;
    private Date date;
    private String publishName;
    private String contentUrl;

    public News(int serialNo, String topic, String simpleText, String publishDate,
                String publishName, String contentUrl) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = format.parse(publishDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.serialNo = serialNo;
        this.topic = topic;
        this.simpleText = simpleText;
        this.publishName = publishName;
        this.contentUrl = contentUrl;
    }

    //region get/set
    public int getSerialNo() {
        return serialNo;
    }

    public String getTopic() {
        return topic;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public String getPublishName() {
        return publishName;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public Date getDate() {
        return date;
    }
    public String getDateString(String pattern) {
        SimpleDateFormat format1 = new SimpleDateFormat(pattern);
        return format1.format(date);
    }
    public String getDateString() {
        SimpleDateFormat format1 = new SimpleDateFormat();
        return format1.format(date);
    }
    //endregion
}
