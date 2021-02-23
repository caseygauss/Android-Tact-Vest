package com.bhaptics.bhapticsandroid;

import com.opencsv.bean.CsvBindByName;

public class Phoneme {
    @CsvBindByName
    private String word;

    @CsvBindByName
    private String phoneme;

    private int id;
    private float xCoord;
    private float yCoord;
    private Integer vestDir;

    public String getWord() {
        return this.word;
    }

    public String getPhoneme() {
        return this.phoneme;
    }

    public void setxCoord(float xCoord) {
        this.xCoord = xCoord;
    }

    public float getxCoord() {
        return xCoord;
    }

    public float getyCoord() {
        return yCoord;
    }

    public Integer getVestDir() {
        return vestDir;
    }

    public void setCoordinates(float xAxis, float yAxis, Integer direction){
        this.xCoord = xAxis;
        this.yCoord = yAxis;
        this.vestDir = direction;
    }

    public void setPhoneme(String phoneme) {
        this.phoneme = phoneme;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getID(){
        return id;
    }
}
