package com.dalda.ksrct;

public class Circular {

    public String namE , url, date;
    public Circular(){

    }

    public Circular(String namE, String url,String date) {
        this.namE = namE;
        this.url = url;
        this.date = date;
    }

    public String getNamE(){
        return namE;
    }
    public String getUrl(){
        return url;
    }
    public String getDate(){
        return date;
    }
}
