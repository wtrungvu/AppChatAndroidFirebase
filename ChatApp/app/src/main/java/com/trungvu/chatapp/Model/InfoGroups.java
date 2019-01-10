package com.trungvu.chatapp.Model;

public class InfoGroups {
    private String date_created;
    private String group_name;
    private String time_created;
    private int count_members;

    public InfoGroups() {
    }

    public InfoGroups(String date_created, String group_name, String time_created, int count_members) {
        this.date_created = date_created;
        this.group_name = group_name;
        this.time_created = time_created;
        this.count_members = count_members;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public int getCount_members() {
        return count_members;
    }

    public void setCount_members(int count_members) {
        this.count_members = count_members;
    }
}
