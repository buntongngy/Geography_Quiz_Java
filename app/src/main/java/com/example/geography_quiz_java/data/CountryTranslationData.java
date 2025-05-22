package com.example.geography_quiz_java.data;

import androidx.annotation.NonNull;
import java.util.Objects;

public class CountryTranslationData {
    private final String originalName;
    private final String translatedName;
    private final String translatedCapital;
    private final String translatedCity1;
    private final String translatedCity2;
    private final String translatedCity3;
    private final String translatedContinent;
    private final String translatedRegion;
    private final String translatedCurrency;
    private final String translatedLanguages;

    public CountryTranslationData(String originalName, String translatedName,
                                  String translatedCapital, String translatedCity1,
                                  String translatedCity2, String translatedCity3,
                                  String translatedContinent, String translatedRegion,
                                  String translatedCurrency, String translatedLanguages) {
        this.originalName = originalName;
        this.translatedName = translatedName;
        this.translatedCapital = translatedCapital;
        this.translatedCity1 = translatedCity1;
        this.translatedCity2 = translatedCity2;
        this.translatedCity3 = translatedCity3;
        this.translatedContinent = translatedContinent;
        this.translatedRegion = translatedRegion;
        this.translatedCurrency = translatedCurrency;
        this.translatedLanguages = translatedLanguages;
    }

    // Getters
    public String getOriginalName() {
        return originalName;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public String getTranslatedCapital() {
        return translatedCapital;
    }

    public String getTranslatedCity1() {
        return translatedCity1;
    }

    public String getTranslatedCity2() {
        return translatedCity2;
    }

    public String getTranslatedCity3() {
        return translatedCity3;
    }

    public String getTranslatedContinent() {
        return translatedContinent;
    }

    public String getTranslatedRegion() {
        return translatedRegion;
    }

    public String getTranslatedCurrency() {
        return translatedCurrency;
    }

    public String getTranslatedLanguages() {
        return translatedLanguages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryTranslationData that = (CountryTranslationData) o;
        return Objects.equals(originalName, that.originalName) &&
                Objects.equals(translatedName, that.translatedName) &&
                Objects.equals(translatedCapital, that.translatedCapital) &&
                Objects.equals(translatedCity1, that.translatedCity1) &&
                Objects.equals(translatedCity2, that.translatedCity2) &&
                Objects.equals(translatedCity3, that.translatedCity3) &&
                Objects.equals(translatedContinent, that.translatedContinent) &&
                Objects.equals(translatedRegion, that.translatedRegion) &&
                Objects.equals(translatedCurrency, that.translatedCurrency) &&
                Objects.equals(translatedLanguages, that.translatedLanguages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalName, translatedName, translatedCapital, translatedCity1,
                translatedCity2, translatedCity3, translatedContinent, translatedRegion,
                translatedCurrency, translatedLanguages);
    }

    @NonNull
    @Override
    public String toString() {
        return "CountryTranslationData{" +
                "originalName='" + originalName + '\'' +
                ", translatedName='" + translatedName + '\'' +
                ", translatedCapital='" + translatedCapital + '\'' +
                ", translatedCity1='" + translatedCity1 + '\'' +
                ", translatedCity2='" + translatedCity2 + '\'' +
                ", translatedCity3='" + translatedCity3 + '\'' +
                ", translatedContinent='" + translatedContinent + '\'' +
                ", translatedRegion='" + translatedRegion + '\'' +
                ", translatedCurrency='" + translatedCurrency + '\'' +
                ", translatedLanguages='" + translatedLanguages + '\'' +
                '}';
    }
}