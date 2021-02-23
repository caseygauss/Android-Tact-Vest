package com.bhaptics.bhapticsandroid;

import com.opencsv.bean.CsvBindByName;

public class HapticPattern {

    @CsvBindByName
    private String phone;

    @CsvBindByName
    private Float x;

    @CsvBindByName
    private Float y;

    @CsvBindByName
    private Integer direction;
    //1 = front of vest ; 2 = back of vest

    public String getPhone(){
        return this.phone;
    }

    public Float getxCo() {
        return this.x;
    }

    public Float getyCo() {
        return this.y;
    }

    public Integer getDirection() {
        return this.direction;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDirection(Integer vestDirection) {
        this.direction = vestDirection;
    }

    public void setxCo(Float xCo) {
        this.x = xCo;
    }

    public void setyCo(Float yCo) {
        this.y = yCo;
    }
}
