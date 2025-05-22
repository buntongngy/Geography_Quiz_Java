package com.example.geography_quiz_java.data;

import java.util.List;

public class CountryData {
    private final String name;
    private final String capital;
    private final String bigCity;
    private final String secondCity;
    private final String thirdCity;
    private final String continent;
    private final String region;
    private final List<String> languages;
    private final String currency;
    private final long population;
    private final long area;
    private final String category;
    private final String countryCode;
    private final List<CountryDatabase.LandmarkData> landmarks;
    private final int difficulty;
    private final List<String> flagColors;
    private final List<String> flagEmblem;

    // Corrected constructor name from Country to CountryData
    public CountryData(String name, String capital, String bigCity, String secondCity, String thirdCity,
                       String continent, String region, List<String> languages, String currency,
                       long population, long area, String category, String countryCode,
                       List<CountryDatabase.LandmarkData> landmarks, int difficulty, List<String> flagColors,
                       List<String> flagEmblem) {
        this.name = name;
        this.capital = capital;
        this.bigCity = bigCity;
        this.secondCity = secondCity;
        this.thirdCity = thirdCity;
        this.continent = continent;
        this.region = region;
        this.languages = languages;
        this.currency = currency;
        this.population = population;
        this.area = area;
        this.category = category;
        this.countryCode = countryCode;
        this.landmarks = landmarks;
        this.difficulty = difficulty;
        this.flagColors = flagColors;
        this.flagEmblem = flagEmblem;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public String getBigCity() {
        return bigCity;
    }

    public String getSecondCity() {
        return secondCity;
    }

    public String getThirdCity() {
        return thirdCity;
    }

    public String getContinent() {
        return continent;
    }

    public String getRegion() {
        return region;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getCurrency() {
        return currency;
    }

    public long getPopulation() {
        return population;
    }

    public long getArea() {
        return area;
    }

    public String getCategory() {
        return category;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<CountryDatabase.LandmarkData> getLandmarks() {
        return landmarks;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<String> getFlagColors() {
        return flagColors;
    }

    public List<String> getFlagEmblem() {
        return flagEmblem;
    }

    // equals(), hashCode() and toString() would typically be overridden as well
    // but omitted for brevity
}