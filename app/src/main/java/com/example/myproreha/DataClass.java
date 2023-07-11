package com.example.myproreha;

public class DataClass {


    private String dataFullname;
    private String dataEmail;
    private String dataPassword;

    public String getDataFullname() {
        return dataFullname;
    }

    public String getDataEmail() {
        return dataEmail;
    }

    public String getDataPassword() {
        return dataPassword;
    }

    public DataClass(String dataDate, String dataTitle, String dataDuration) {
        this.dataFullname = dataDate;
        this.dataEmail = dataTitle;
        this.dataPassword = dataDuration;
    }



    public DataClass(){

    }

}
