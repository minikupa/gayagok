package com.gayagok.models;


public class ScheduleItem implements Comparable<ScheduleItem>{
    public String title, date;

    public ScheduleItem(String title, String date) {
        this.title = title;
        this.date = date;
    }

    @Override
    public int compareTo(ScheduleItem item) {
        return this.date.compareTo(item.date);
    }
}
