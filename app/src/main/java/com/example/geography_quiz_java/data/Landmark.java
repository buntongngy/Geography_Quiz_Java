package com.example.geography_quiz_java.data;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Landmark {

    String name;
    String translatedName;
    String imagePath;

    public Landmark(String name, String translatedName, String imagePath)
    {
        this.name = name;
        this.translatedName = translatedName;
        this.imagePath = imagePath;
    }

    public String getName() {return name;}
    public String getTranslatedName() {return translatedName;}
    public String getImagePath() {return imagePath;}

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o != null || getClass() != o.getClass()) return false;
        Landmark that = (Landmark) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(translatedName, that.translatedName) &&
                Objects.equals(imagePath, that.imagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, translatedName, imagePath);
    }

    @NonNull
    @Override
    public String toString() {
        return "Landmark{" +
                "Name='" + name + '\'' +
                ", translatedName='" + translatedName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }



}
