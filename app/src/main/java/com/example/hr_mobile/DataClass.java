package com.example.hr_mobile;

public class DataClass {
    private String dataFIO;
    private String dataDesc;
    private String dataRole;
    private String dataImage;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public String getDataFIO() {
        return dataFIO;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataRole() {
        return dataRole;
    }

    public String getDataImage() {
        return dataImage;
    }

    public DataClass(String dataFIO, String dataDesc, String dataRole, String dataImage) {
        this.dataFIO = dataFIO;
        this.dataDesc = dataDesc;
        this.dataRole = dataRole;
        this.dataImage = dataImage;
    }

    public DataClass () {}
}
