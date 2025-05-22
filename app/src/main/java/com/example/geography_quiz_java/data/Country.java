package com.example.geography_quiz_java.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.core.database.CursorKt;
import com.example.geography_quiz_java.data.CountryDatabase;
import com.example.geography_quiz_java.data.Landmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Country {
    private final int id;
    private final String name;
    private final String capital;
    private final String bigCity;
    private final String secondCity;
    private final String thirdCity;
    private final String continent;
    private final String region;
    private final List<String> languages;
    private final String currency;
    private final int population;
    private final int area;
    private final String category;
    private final String countryCode;
    private final List<Landmark> landmarks;
    private final int difficulty;
    private final List<String> flagColors;
    private final List<String> flagEmblem;
    // Translation fields
    private final String translatedName;
    private final String translatedCapital;
    private final String translatedBigCity;
    private final String translatedSecondCity;
    private final String translatedThirdCity;
    private final String translatedContinent;
    private final String translatedRegion;
    private final String translatedCurrency;
    private final List<String> translatedLanguages;
    private final List<Landmark> translatedLandmarks;

    public Country(int id, String name, String capital, String bigCity, String secondCity,
                   String thirdCity, String continent, String region, List<String> languages,
                   String currency, int population, int area, String category, String countryCode,
                   List<Landmark> landmarks, int difficulty, List<String> flagColors,
                   List<String> flagEmblem, String translatedName, String translatedCapital,
                   String translatedBigCity, String translatedSecondCity, String translatedThirdCity,
                   String translatedContinent, String translatedRegion, String translatedCurrency,
                   List<String> translatedLanguages, List<Landmark> translatedLandmarks) {
        this.id = id;
        this.name = name;
        this.capital = capital;
        this.bigCity = bigCity;
        this.secondCity = secondCity;
        this.thirdCity = thirdCity;
        this.continent = continent;
        this.region = region;
        this.languages = languages != null ? languages : new ArrayList<>();
        this.currency = currency;
        this.population = population;
        this.area = area;
        this.category = category;
        this.countryCode = countryCode;
        this.landmarks = landmarks != null ? landmarks : new ArrayList<>();
        this.difficulty = difficulty;
        this.flagColors = flagColors != null ? flagColors : new ArrayList<>();
        this.flagEmblem = flagEmblem != null ? flagEmblem : new ArrayList<>();
        this.translatedName = translatedName != null ? translatedName : name;
        this.translatedCapital = translatedCapital != null ? translatedCapital : capital;
        this.translatedBigCity = translatedBigCity != null ? translatedBigCity : bigCity;
        this.translatedSecondCity = translatedSecondCity;
        this.translatedThirdCity = translatedThirdCity;
        this.translatedContinent = translatedContinent != null ? translatedContinent : continent;
        this.translatedRegion = translatedRegion != null ? translatedRegion : region;
        this.translatedCurrency = translatedCurrency != null ? translatedCurrency : currency;
        this.translatedLanguages = translatedLanguages != null ? translatedLanguages : this.languages;
        this.translatedLandmarks = translatedLandmarks != null ? translatedLandmarks : this.landmarks;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getBigCity() { return bigCity; }
    public String getSecondCity() { return secondCity; }
    public String getThirdCity() { return thirdCity; }
    public String getContinent() { return continent; }
    public String getRegion() { return region; }
    public List<String> getLanguages() { return languages; }
    public String getCurrency() { return currency; }
    public int getPopulation() { return population; }
    public int getArea() { return area; }
    public String getCategory() { return category; }
    public String getCountryCode() { return countryCode; }
    public List<Landmark> getLandmarks() { return landmarks; }
    public int getDifficulty() { return difficulty; }
    public List<String> getFlagColors() { return flagColors; }
    public List<String> getFlagEmblem() { return flagEmblem; }
    public String getTranslatedName() { return translatedName; }
    public String getTranslatedCapital() { return translatedCapital; }
    public String getTranslatedBigCity() { return translatedBigCity; }
    public String getTranslatedSecondCity() { return translatedSecondCity; }
    public String getTranslatedThirdCity() { return translatedThirdCity; }
    public String getTranslatedContinent() { return translatedContinent; }
    public String getTranslatedRegion() { return translatedRegion; }
    public String getTranslatedCurrency() { return translatedCurrency; }
    public List<String> getTranslatedLanguages() { return translatedLanguages; }
    public List<Landmark> getTranslatedLandmarks() { return translatedLandmarks; }

    // Factory method to create from cursor
    public static Country fromCursor(Cursor cursor) {
        String languagesString = cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_LANG));
        List<String> languagesList = Arrays.asList(languagesString.split(",\\s*"));
        String colorsString = CursorKt.getStringOrNull(cursor, cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_FLAG_COLORS));
        String emblemsString = CursorKt.getStringOrNull(cursor, cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_FLAG_EMBLEM));

        return new Country(
                cursor.getInt(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_CAPITAL)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_BIGGEST_CITY)),
                CursorKt.getStringOrNull(cursor, cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_SECOND_CITY)),
                CursorKt.getStringOrNull(cursor, cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_THIRD_CITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_CONTINENT)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_REGION)),
                languagesList,
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_CURRENCY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_POPULATION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_AREA)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_CODE)),
                new ArrayList<>(), // landmarks
                cursor.getInt(cursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_DIFFICULTY)),
                colorsString != null ? Arrays.asList(colorsString.split(",\\s*")) : new ArrayList<>(),
                emblemsString != null ? Arrays.asList(emblemsString.split(",\\s*")) : new ArrayList<>(),
                null, null, null, null, null, null, null, null, null, null
        );
    }

    // Method to create with translations
    public static Country withTranslations(Country base, Cursor translationCursor) {
        String translatedLangsString = translationCursor.getString(
                translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_LANG));
        List<String> translatedLangsList = Arrays.asList(translatedLangsString.split(",\\s*"));

        return new Country(
                base.id,
                base.name,
                base.capital,
                base.bigCity,
                base.secondCity,
                base.thirdCity,
                base.continent,
                base.region,
                base.languages,
                base.currency,
                base.population,
                base.area,
                base.category,
                base.countryCode,
                base.landmarks,
                base.difficulty,
                base.flagColors,
                base.flagEmblem,
                translationCursor.getString(
                        translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_NAME)),
                translationCursor.getString(
                        translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_CAPITAL)),
                translationCursor.getString(
                        translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_CITY1)),
                CursorKt.getStringOrNull(
                        translationCursor, translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_CITY2)),
                CursorKt.getStringOrNull(
                        translationCursor, translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_CITY3)),
                translationCursor.getString(
                        translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_CONTINENT)),
                translationCursor.getString(
                        translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_REGION)),
                translationCursor.getString(
                        translationCursor.getColumnIndexOrThrow(CountryDatabase.COLUMN_TRANSLATED_CURRENCY)),
                translatedLangsList,
                base.translatedLandmarks
        );
    }

    // Helper methods
    public String getDisplayName(String languageCode) {
        return !"en".equals(languageCode) && translatedName != null && !translatedName.isEmpty()
                ? translatedName : name;
    }

    public String getDisplayCapital(String languageCode) {
        return !"en".equals(languageCode) && translatedCapital != null && !translatedCapital.isEmpty()
                ? translatedCapital : capital;
    }

    public String getDisplayLanguages(String languageCode) {
        List<String> langsToUse = !"en".equals(languageCode) && translatedLanguages != null && !translatedLanguages.isEmpty()
                ? translatedLanguages : languages;
        return String.join(", ", langsToUse);
    }

    public List<Landmark> getDisplayLandmarks(String languageCode) {
        return !"en".equals(languageCode) && translatedLandmarks != null && !translatedLandmarks.isEmpty()
                ? translatedLandmarks : landmarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return id == country.id &&
                population == country.population &&
                area == country.area &&
                difficulty == country.difficulty &&
                Objects.equals(name, country.name) &&
                Objects.equals(capital, country.capital) &&
                Objects.equals(bigCity, country.bigCity) &&
                Objects.equals(secondCity, country.secondCity) &&
                Objects.equals(thirdCity, country.thirdCity) &&
                Objects.equals(continent, country.continent) &&
                Objects.equals(region, country.region) &&
                Objects.equals(languages, country.languages) &&
                Objects.equals(currency, country.currency) &&
                Objects.equals(category, country.category) &&
                Objects.equals(countryCode, country.countryCode) &&
                Objects.equals(landmarks, country.landmarks) &&
                Objects.equals(flagColors, country.flagColors) &&
                Objects.equals(flagEmblem, country.flagEmblem) &&
                Objects.equals(translatedName, country.translatedName) &&
                Objects.equals(translatedCapital, country.translatedCapital) &&
                Objects.equals(translatedBigCity, country.translatedBigCity) &&
                Objects.equals(translatedSecondCity, country.translatedSecondCity) &&
                Objects.equals(translatedThirdCity, country.translatedThirdCity) &&
                Objects.equals(translatedContinent, country.translatedContinent) &&
                Objects.equals(translatedRegion, country.translatedRegion) &&
                Objects.equals(translatedCurrency, country.translatedCurrency) &&
                Objects.equals(translatedLanguages, country.translatedLanguages) &&
                Objects.equals(translatedLandmarks, country.translatedLandmarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capital, bigCity, secondCity, thirdCity, continent, region,
                languages, currency, population, area, category, countryCode, landmarks, difficulty,
                flagColors, flagEmblem, translatedName, translatedCapital, translatedBigCity,
                translatedSecondCity, translatedThirdCity, translatedContinent, translatedRegion,
                translatedCurrency, translatedLanguages, translatedLandmarks);
    }

    public Country copyWithLandmarks(List<Landmark> newLandmarks) {
        return new Country(
                this.id,
                this.name,
                this.capital,
                this.bigCity,
                this.secondCity,
                this.thirdCity,
                this.continent,
                this.region,
                this.languages,
                this.currency,
                this.population,
                this.area,
                this.category,
                this.countryCode,
                newLandmarks,
                this.difficulty,
                this.flagColors,
                this.flagEmblem,
                this.translatedName,
                this.translatedCapital,
                this.translatedBigCity,
                this.translatedSecondCity,
                this.translatedThirdCity,
                this.translatedContinent,
                this.translatedRegion,
                this.translatedCurrency,
                this.translatedLanguages,
                this.translatedLandmarks
        );
    }

    @NonNull
    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capital='" + capital + '\'' +
                ", bigCity='" + bigCity + '\'' +
                ", secondCity='" + secondCity + '\'' +
                ", thirdCity='" + thirdCity + '\'' +
                ", continent='" + continent + '\'' +
                ", region='" + region + '\'' +
                ", languages=" + languages +
                ", currency='" + currency + '\'' +
                ", population=" + population +
                ", area=" + area +
                ", category='" + category + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", landmarks=" + landmarks +
                ", difficulty=" + difficulty +
                ", flagColors=" + flagColors +
                ", flagEmblem=" + flagEmblem +
                ", translatedName='" + translatedName + '\'' +
                ", translatedCapital='" + translatedCapital + '\'' +
                ", translatedBigCity='" + translatedBigCity + '\'' +
                ", translatedSecondCity='" + translatedSecondCity + '\'' +
                ", translatedThirdCity='" + translatedThirdCity + '\'' +
                ", translatedContinent='" + translatedContinent + '\'' +
                ", translatedRegion='" + translatedRegion + '\'' +
                ", translatedCurrency='" + translatedCurrency + '\'' +
                ", translatedLanguages=" + translatedLanguages +
                ", translatedLandmarks=" + translatedLandmarks +
                '}';
    }
}