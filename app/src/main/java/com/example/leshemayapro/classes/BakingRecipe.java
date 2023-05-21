package com.example.leshemayapro.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class BakingRecipe extends Recipe
{
     private String time, temp;

    public BakingRecipe() {}

    public BakingRecipe(String id, String uid, String title, String description, ArrayList<String> ingredients, ArrayList<String> tags, String directions, String makingTime, String numberOfPeople, String time, String temp)
    {
        super(id, uid, title, description, ingredients, tags, directions, makingTime, numberOfPeople);
        this.time = time;
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "BakingRecipe{" +
                "time='" + time + '\'' +
                ", temp='" + temp + '\'' +
                '}';
    }
}
