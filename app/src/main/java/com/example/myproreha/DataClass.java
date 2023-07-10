package com.example.myproreha;

public class DataClass {

    private String dataDate;
    private String dataTitle;
    private String dataDuration;
    private String dataNotes;

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataDate() {
        return dataDate;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDuration() {
        return dataDuration;
    }

    public String getDataNotes() {
        return dataNotes;
    }

    public DataClass(String dataDate, String dataTitle, String dataDuration, String dataNotes) {
        this.dataDate = dataDate;
        this.dataTitle = dataTitle;
        this.dataDuration = dataDuration;
        this.dataNotes = dataNotes;
    }

    public DataClass(){

    }

}
