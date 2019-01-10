package com.trungvu.chatapp.Model;


public class AllUsers {
    private String user_name;
    private String user_image;
    private String user_status;
    private String user_thumb_image;
    private String user_email;
    private String user_sex;
    private String user_phone_number;
    private String user_birthday;
    private String user_name_lowercase;
    private String user_join_date;
    private String user_join_time;

    public AllUsers() {
    }

    public AllUsers(String user_name, String user_image, String user_status, String user_thumb_image, String user_email, String user_sex, String user_phone_number, String user_birthday, String user_name_lowercase, String user_join_date, String user_join_time) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_status = user_status;
        this.user_thumb_image = user_thumb_image;
        this.user_email = user_email;
        this.user_sex = user_sex;
        this.user_phone_number = user_phone_number;
        this.user_birthday = user_birthday;
        this.user_name_lowercase = user_name_lowercase;
        this.user_join_date = user_join_date;
        this.user_join_time = user_join_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getUser_name_lowercase() {
        return user_name_lowercase;
    }

    public void setUser_name_lowercase(String user_name_lowercase) {
        this.user_name_lowercase = user_name_lowercase;
    }

    public String getUser_join_date() {
        return user_join_date;
    }

    public void setUser_join_date(String user_join_date) {
        this.user_join_date = user_join_date;
    }

    public String getUser_join_time() {
        return user_join_time;
    }

    public void setUser_join_time(String user_join_time) {
        this.user_join_time = user_join_time;
    }
}
