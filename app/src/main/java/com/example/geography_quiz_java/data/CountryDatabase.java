package com.example.geography_quiz_java.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryDatabase extends SQLiteOpenHelper {
    private final Context appContext;
    private final Gson gson = new Gson();

    // Database constants
    private static final String TAG = "CountryDatabase";
    private static final String DATABASE_NAME = "world_countries.db";
    private static final int DATABASE_VERSION = 3;

    // Countries table
    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CAPITAL = "capital";
    public static final String COLUMN_BIGGEST_CITY = "city1";
    public static final String COLUMN_SECOND_CITY = "city2";
    public static final String COLUMN_THIRD_CITY = "city3";
    public static final String COLUMN_CONTINENT = "continent";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_LANG = "languages";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_POPULATION = "population";
    public static final String COLUMN_AREA = "area";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_FLAG_COLORS = "flag_colors";
    public static final String COLUMN_FLAG_EMBLEM = "flag_emblem";

    // Translation table
    public static final String TABLE_COUNTRY_TRANSLATION = "country_translation";
    public static final String COLUMN_COUNTRY_ID = "country_id";
    public static final String COLUMN_LANGUAGE_CODE = "language_code";
    public static final String COLUMN_TRANSLATED_NAME = "translated_name";
    public static final String COLUMN_TRANSLATED_CAPITAL = "translated_capital";
    public static final String COLUMN_TRANSLATED_CITY1 = "translated_city1";
    public static final String COLUMN_TRANSLATED_CITY2 = "translated_city2";
    public static final String COLUMN_TRANSLATED_CITY3 = "translated_city3";
    public static final String COLUMN_TRANSLATED_CONTINENT = "translated_continent";
    public static final String COLUMN_TRANSLATED_REGION = "translated_region";
    public static final String COLUMN_TRANSLATED_CURRENCY = "translated_currency";
    public static final String COLUMN_TRANSLATED_LANG = "translated_languages";

    // Landmark table
    public static final String TABLE_LANDMARKS = "landmarks";
    public static final String COLUMN_LANDMARK_ID = "landmark_id";
    public static final String COLUMN_LANDMARK_NAME = "landmark_name";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Landmark translation table
    public static final String TABLE_LANDMARK_TRANSLATIONS = "landmark_translations";
    public static final String COLUMN_TRANSLATED_LANDMARK_NAME = "translated_landmark_name";

    public CountryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createTables(db);
            createIndexes(db);
            refreshAllCountries(db);
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY_TRANSLATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANDMARKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANDMARK_TRANSLATIONS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
            throw e;
        }
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COUNTRIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL UNIQUE," +
                COLUMN_CAPITAL + " TEXT NOT NULL," +
                COLUMN_BIGGEST_CITY + " TEXT NOT NULL," +
                COLUMN_SECOND_CITY + " TEXT," +
                COLUMN_THIRD_CITY + " TEXT," +
                COLUMN_CONTINENT + " TEXT NOT NULL," +
                COLUMN_REGION + " TEXT NOT NULL," +
                COLUMN_LANG + " TEXT NOT NULL," +
                COLUMN_CURRENCY + " TEXT NOT NULL," +
                COLUMN_POPULATION + " INTEGER NOT NULL," +
                COLUMN_AREA + " INTEGER NOT NULL," +
                COLUMN_CATEGORY + " TEXT NOT NULL," +
                COLUMN_CODE + " TEXT NOT NULL," +
                COLUMN_DIFFICULTY + " INTEGER NOT NULL," +
                COLUMN_FLAG_COLORS + " TEXT," +
                COLUMN_FLAG_EMBLEM + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COUNTRY_TRANSLATION + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_COUNTRY_ID + " INTEGER NOT NULL," +
                COLUMN_LANGUAGE_CODE + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_NAME + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_CAPITAL + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_CITY1 + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_CITY2 + " TEXT," +
                COLUMN_TRANSLATED_CITY3 + " TEXT," +
                COLUMN_TRANSLATED_CONTINENT + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_REGION + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_CURRENCY + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_LANG + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_COUNTRY_ID + ") REFERENCES " + TABLE_COUNTRIES + "(" + COLUMN_ID + ")," +
                "UNIQUE(" + COLUMN_COUNTRY_ID + ", " + COLUMN_LANGUAGE_CODE + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LANDMARKS + " (" +
                COLUMN_LANDMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_COUNTRY_ID + " INTEGER NOT NULL," +
                COLUMN_LANDMARK_NAME + " TEXT NOT NULL," +
                COLUMN_IMAGE_PATH + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_COUNTRY_ID + ") REFERENCES " + TABLE_COUNTRIES + "(" + COLUMN_ID + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LANDMARK_TRANSLATIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LANDMARK_ID + " INTEGER NOT NULL," +
                COLUMN_LANGUAGE_CODE + " TEXT NOT NULL," +
                COLUMN_TRANSLATED_LANDMARK_NAME + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_LANDMARK_ID + ") REFERENCES " + TABLE_LANDMARKS + "(" + COLUMN_LANDMARK_ID + ")," +
                "UNIQUE(" + COLUMN_LANDMARK_ID + ", " + COLUMN_LANGUAGE_CODE + "))");
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_country_name ON " + TABLE_COUNTRIES + "(" + COLUMN_NAME + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_country_continent ON " + TABLE_COUNTRIES + "(" + COLUMN_CONTINENT + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_country_region ON " + TABLE_COUNTRIES + "(" + COLUMN_REGION + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_translation_country ON " + TABLE_COUNTRY_TRANSLATION + "(" + COLUMN_COUNTRY_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_translation_language ON " + TABLE_COUNTRY_TRANSLATION + "(" + COLUMN_LANGUAGE_CODE + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_landmark_country ON " + TABLE_LANDMARKS + "(" + COLUMN_COUNTRY_ID + ")");
    }

    private void refreshAllCountries(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + TABLE_COUNTRIES);
            db.execSQL("DELETE FROM " + TABLE_COUNTRY_TRANSLATION);
            db.execSQL("DELETE FROM " + TABLE_LANDMARKS);
            db.execSQL("DELETE FROM " + TABLE_LANDMARK_TRANSLATIONS);

            List<CountryData> countries = loadCountriesFromJson();
            Map<String, List<CountryTranslationData>> translations = loadAllTranslations();
            Map<String, List<LandmarkTranslationData>> landmarkTranslations = loadAllLandmarkTranslations();

            for (CountryData country : countries) {
                try {
                    long countryId = insertCountry(db, country);
                    insertTranslations(db, countryId, country.getName(), translations);
                    insertLandmarks(db, countryId, country, landmarkTranslations);
                } catch (Exception e) {
                    Log.e(TAG, "Error inserting country " + country.getName(), e);
                }
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Successfully loaded " + countries.size() + " countries with landmarks");
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing countries", e);
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    private long insertCountry(SQLiteDatabase db, CountryData country) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, country.getName());
        values.put(COLUMN_CAPITAL, country.getCapital());
        values.put(COLUMN_BIGGEST_CITY, country.getBigCity());
        values.put(COLUMN_SECOND_CITY, country.getSecondCity());
        values.put(COLUMN_THIRD_CITY, country.getThirdCity());
        values.put(COLUMN_CONTINENT, country.getContinent());
        values.put(COLUMN_REGION, country.getRegion());
        values.put(COLUMN_LANG, country.getLanguages() != null ? String.join(",", country.getLanguages()) : "");
        values.put(COLUMN_CURRENCY, country.getCurrency());
        values.put(COLUMN_POPULATION, country.getPopulation());
        values.put(COLUMN_AREA, country.getArea());
        values.put(COLUMN_CATEGORY, country.getCategory());
        values.put(COLUMN_CODE, country.getCountryCode());
        values.put(COLUMN_DIFFICULTY, country.getDifficulty());
        values.put(COLUMN_FLAG_COLORS, country.getFlagColors() != null ? String.join(",", country.getFlagColors()) : "");
        values.put(COLUMN_FLAG_EMBLEM, country.getFlagEmblem() != null ? String.join(",", country.getFlagEmblem()) : "");

        return db.insertWithOnConflict(TABLE_COUNTRIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void insertTranslations(SQLiteDatabase db, long countryId, String countryName,
                                    Map<String, List<CountryTranslationData>> translations) {
        for (Map.Entry<String, List<CountryTranslationData>> entry : translations.entrySet()) {
            String languageCode = entry.getKey();
            for (CountryTranslationData translation : entry.getValue()) {
                if (translation.getOriginalName().equals(countryName)) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_COUNTRY_ID, countryId);
                    values.put(COLUMN_LANGUAGE_CODE, languageCode);
                    values.put(COLUMN_TRANSLATED_NAME, translation.getTranslatedName());
                    values.put(COLUMN_TRANSLATED_CAPITAL, translation.getTranslatedCapital());
                    values.put(COLUMN_TRANSLATED_CITY1, translation.getTranslatedCity1());
                    values.put(COLUMN_TRANSLATED_CITY2, translation.getTranslatedCity2());
                    values.put(COLUMN_TRANSLATED_CITY3, translation.getTranslatedCity3());
                    values.put(COLUMN_TRANSLATED_CONTINENT, translation.getTranslatedContinent());
                    values.put(COLUMN_TRANSLATED_REGION, translation.getTranslatedRegion());
                    values.put(COLUMN_TRANSLATED_CURRENCY, translation.getTranslatedCurrency());
                    values.put(COLUMN_TRANSLATED_LANG, translation.getTranslatedLanguages());

                    db.insertWithOnConflict(TABLE_COUNTRY_TRANSLATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    break;
                }
            }
        }
    }

    private void insertLandmarks(SQLiteDatabase db, long countryId, CountryData country,
                                 Map<String, List<LandmarkTranslationData>> translations) {
        if (country.getLandmarks() == null) {
            Log.d(TAG, "Landmarks is null for " + country.getName());
            return;
        }

        if (country.getLandmarks().isEmpty()) {
            Log.d(TAG, "No landmarks for " + country.getName());
            return;
        }

        Log.d(TAG, "Inserting " + country.getLandmarks().size() + " landmarks for " + country.getName());

        for (LandmarkData landmark : country.getLandmarks()) {
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_COUNTRY_ID, countryId);
                values.put(COLUMN_LANDMARK_NAME, landmark.getName());
                values.put(COLUMN_IMAGE_PATH, landmark.getImagePath());

                long landmarkId = db.insertWithOnConflict(TABLE_LANDMARKS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "Inserted landmark " + landmark.getName() + " with ID " + landmarkId);

                for (Map.Entry<String, List<LandmarkTranslationData>> entry : translations.entrySet()) {
                    String languageCode = entry.getKey();
                    for (LandmarkTranslationData translation : entry.getValue()) {
                        if (translation.getOriginalName().equals(landmark.getName())) {
                            ContentValues transValues = new ContentValues();
                            transValues.put(COLUMN_LANDMARK_ID, landmarkId);
                            transValues.put(COLUMN_LANGUAGE_CODE, languageCode);
                            transValues.put(COLUMN_TRANSLATED_LANDMARK_NAME, translation.getTranslatedName());

                            db.insertWithOnConflict(TABLE_LANDMARK_TRANSLATIONS, null, transValues, SQLiteDatabase.CONFLICT_REPLACE);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error inserting landmark " + landmark.getName(), e);
            }
        }
    }

    private List<CountryData> loadCountriesFromJson() {
        try {
            InputStreamReader reader = new InputStreamReader(appContext.getAssets().open("countries.json"));
            Type type = new TypeToken<List<CountryData>>(){}.getType();
            List<CountryData> countries = gson.fromJson(reader, type);
            return countries != null ? countries : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading countries.json", e);
            return new ArrayList<>();
        }
    }

    private Map<String, List<CountryTranslationData>> loadAllTranslations() {
        try {
            String[] translationFiles = appContext.getAssets().list("translations");
            Map<String, List<CountryTranslationData>> translations = new HashMap<>();

            if (translationFiles != null) {
                for (String filename : translationFiles) {
                    String languageCode = filename.replace(".json", "");
                    List<CountryTranslationData> languageTranslations = loadCountryTranslationsForLanguage(languageCode);
                    translations.put(languageCode, languageTranslations);
                }
            }
            return translations;
        } catch (IOException e) {
            Log.e(TAG, "Error loading translations", e);
            return new HashMap<>();
        }
    }

    private Map<String, List<LandmarkTranslationData>> loadAllLandmarkTranslations() {
        try {
            String[] translationFiles = appContext.getAssets().list("landmark_translations");
            Map<String, List<LandmarkTranslationData>> translations = new HashMap<>();

            if (translationFiles != null) {
                for (String filename : translationFiles) {
                    String languageCode = filename.replace(".json", "");
                    List<LandmarkTranslationData> languageTranslations = loadLandmarkTranslationsForLanguage(languageCode);
                    translations.put(languageCode, languageTranslations);
                }
            }
            return translations;
        } catch (IOException e) {
            Log.e(TAG, "Error loading landmark translations", e);
            return new HashMap<>();
        }
    }

    private List<CountryTranslationData> loadCountryTranslationsForLanguage(String languageCode) {
        try {
            InputStreamReader reader = new InputStreamReader(appContext.getAssets().open("translations/" + languageCode + ".json"));
            Type type = new TypeToken<List<CountryTranslationData>>(){}.getType();
            List<CountryTranslationData> translations = gson.fromJson(reader, type);
            return translations != null ? translations : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading " + languageCode + " translations", e);
            return new ArrayList<>();
        }
    }

    private List<LandmarkTranslationData> loadLandmarkTranslationsForLanguage(String languageCode) {
        try {
            InputStreamReader reader = new InputStreamReader(appContext.getAssets().open("landmark_translations/" + languageCode + ".json"));
            Type type = new TypeToken<List<LandmarkTranslationData>>(){}.getType();
            List<LandmarkTranslationData> translations = gson.fromJson(reader, type);
            return translations != null ? translations : new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, "Error loading " + languageCode + " landmark translations", e);
            return new ArrayList<>();
        }
    }

    public List<com.example.geography_quiz_java.data.Country> getRandomCountries(int count) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_COUNTRIES + " ORDER BY RANDOM() LIMIT " + count, null);

        List<com.example.geography_quiz_java.data.Country> countries = new ArrayList<>();
        while (cursor.moveToNext()) {
            countries.add(com.example.geography_quiz_java.data.Country.fromCursor(cursor));
        }
        cursor.close();
        return countries;
    }

    public List<com.example.geography_quiz_java.data.Country> getTranslatedRandomCountries(int count, String languageCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.*, t.* FROM " + TABLE_COUNTRIES + " c " +
                        "LEFT JOIN " + TABLE_COUNTRY_TRANSLATION + " t " +
                        "ON c." + COLUMN_ID + " = t." + COLUMN_COUNTRY_ID + " AND t." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "ORDER BY RANDOM() LIMIT " + count,
                new String[]{languageCode});

        List<com.example.geography_quiz_java.data.Country> countries = new ArrayList<>();
        while (cursor.moveToNext()) {
            com.example.geography_quiz_java.data.Country country = com.example.geography_quiz_java.data.Country.fromCursor(cursor);
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATED_NAME))) {
                countries.add(com.example.geography_quiz_java.data.Country.withTranslations(country, cursor));
            } else {
                countries.add(country);
            }
        }
        cursor.close();
        return countries;
    }

    public List<com.example.geography_quiz_java.data.Country> getRandomCountriesWithLandmarks(int count, String languageCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.*, l." + COLUMN_LANDMARK_NAME + " as landmark_name, " +
                        "l." + COLUMN_IMAGE_PATH + " as landmark_path, " +
                        "lt." + COLUMN_TRANSLATED_LANDMARK_NAME + " as translated_landmark_name " +
                        "FROM " + TABLE_COUNTRIES + " c " +
                        "JOIN " + TABLE_LANDMARKS + " l ON c." + COLUMN_ID + " = l." + COLUMN_COUNTRY_ID + " " +
                        "LEFT JOIN " + TABLE_LANDMARK_TRANSLATIONS + " lt ON l." + COLUMN_LANDMARK_ID + " = lt." + COLUMN_LANDMARK_ID + " " +
                        "AND lt." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "ORDER BY RANDOM() LIMIT " + count,
                new String[]{languageCode});

        List<com.example.geography_quiz_java.data.Country> countries = new ArrayList<>();
        while (cursor.moveToNext()) {
            com.example.geography_quiz_java.data.Country country = com.example.geography_quiz_java.data.Country.fromCursor(cursor);
            Landmark landmark = new Landmark(
                    cursor.getString(cursor.getColumnIndexOrThrow("landmark_name")),
                    cursor.isNull(cursor.getColumnIndexOrThrow("translated_landmark_name")) ?
                            cursor.getString(cursor.getColumnIndexOrThrow("landmark_name")) :
                            cursor.getString(cursor.getColumnIndexOrThrow("translated_landmark_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("landmark_path"))
            );
            countries.add(country.copyWithLandmarks(Arrays.asList(landmark)));
        }
        cursor.close();
        return countries;
    }

    public List<com.example.geography_quiz_java.data.Country> getCountriesByDifficulty(int difficulty, int limit, String languageCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.*, t.* FROM " + TABLE_COUNTRIES + " c " +
                        "LEFT JOIN " + TABLE_COUNTRY_TRANSLATION + " t " +
                        "ON c." + COLUMN_ID + " = t." + COLUMN_COUNTRY_ID + " AND t." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "WHERE c." + COLUMN_DIFFICULTY + " = ? " +
                        "ORDER BY RANDOM() LIMIT " + limit,
                new String[]{languageCode, String.valueOf(difficulty)});

        List<com.example.geography_quiz_java.data.Country> countries = new ArrayList<>();
        while (cursor.moveToNext()) {
            com.example.geography_quiz_java.data.Country country = com.example.geography_quiz_java.data.Country.fromCursor(cursor);
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATED_NAME))) {
                countries.add(com.example.geography_quiz_java.data.Country.withTranslations(country, cursor));
            } else {
                countries.add(country);
            }
        }
        cursor.close();
        return countries;
    }

    public List<com.example.geography_quiz_java.data.Country> getCountriesWithLandmarksByDifficulty(int difficulty, int limit, String languageCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.*, t.*, l." + COLUMN_LANDMARK_NAME + " as landmark_name, " +
                        "l." + COLUMN_IMAGE_PATH + " as landmark_path, " +
                        "lt." + COLUMN_TRANSLATED_LANDMARK_NAME + " as translated_landmark_name " +
                        "FROM " + TABLE_COUNTRIES + " c " +
                        "LEFT JOIN " + TABLE_COUNTRY_TRANSLATION + " t ON c." + COLUMN_ID + " = t." + COLUMN_COUNTRY_ID + " " +
                        "AND t." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "JOIN " + TABLE_LANDMARKS + " l ON c." + COLUMN_ID + " = l." + COLUMN_COUNTRY_ID + " " +
                        "LEFT JOIN " + TABLE_LANDMARK_TRANSLATIONS + " lt ON l." + COLUMN_LANDMARK_ID + " = lt." + COLUMN_LANDMARK_ID + " " +
                        "AND lt." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "WHERE c." + COLUMN_DIFFICULTY + " = ? " +
                        "ORDER BY RANDOM() LIMIT " + limit,
                new String[]{languageCode, languageCode, String.valueOf(difficulty)});

        List<com.example.geography_quiz_java.data.Country> countries = new ArrayList<>();
        while (cursor.moveToNext()) {
            com.example.geography_quiz_java.data.Country country = cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATED_NAME)) ?
                    com.example.geography_quiz_java.data.Country.fromCursor(cursor) : com.example.geography_quiz_java.data.Country.withTranslations(com.example.geography_quiz_java.data.Country.fromCursor(cursor), cursor);

            Landmark landmark = new Landmark(
                    cursor.getString(cursor.getColumnIndexOrThrow("landmark_name")),
                    cursor.isNull(cursor.getColumnIndexOrThrow("translated_landmark_name")) ?
                            cursor.getString(cursor.getColumnIndexOrThrow("landmark_name")) :
                            cursor.getString(cursor.getColumnIndexOrThrow("translated_landmark_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("landmark_path"))
            );

            boolean found = false;
            for (int i = 0; i < countries.size(); i++) {
                if (countries.get(i).getId() == country.getId()) {
                    List<Landmark> existingLandmarks = new ArrayList<>(countries.get(i).getLandmarks());
                    existingLandmarks.add(landmark);
                    countries.set(i, country.copyWithLandmarks(existingLandmarks));
                    found = true;
                    break;
                }
            }

            if (!found) {
                countries.add(country.copyWithLandmarks(Arrays.asList(landmark)));
            }
        }
        cursor.close();
        return countries;
    }

    public List<com.example.geography_quiz_java.data.Country> getAllCountriesWithLandmarks(String languageCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.*, t.*, GROUP_CONCAT(l." + COLUMN_LANDMARK_NAME + ", '|') as landmark_names, " +
                        "GROUP_CONCAT(l." + COLUMN_IMAGE_PATH + ", '|') as landmark_paths, " +
                        "GROUP_CONCAT(lt." + COLUMN_TRANSLATED_LANDMARK_NAME + ", '|') as translated_landmark_names " +
                        "FROM " + TABLE_COUNTRIES + " c " +
                        "LEFT JOIN " + TABLE_COUNTRY_TRANSLATION + " t ON c." + COLUMN_ID + " = t." + COLUMN_COUNTRY_ID + " " +
                        "AND t." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "JOIN " + TABLE_LANDMARKS + " l ON c." + COLUMN_ID + " = l." + COLUMN_COUNTRY_ID + " " +
                        "LEFT JOIN " + TABLE_LANDMARK_TRANSLATIONS + " lt ON l." + COLUMN_LANDMARK_ID + " = lt." + COLUMN_LANDMARK_ID + " " +
                        "AND lt." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "GROUP BY c." + COLUMN_ID,
                new String[]{languageCode, languageCode});

        List<com.example.geography_quiz_java.data.Country> countries = new ArrayList<>();
        while (cursor.moveToNext()) {
            com.example.geography_quiz_java.data.Country country = cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATED_NAME)) ?
                    com.example.geography_quiz_java.data.Country.fromCursor(cursor) : com.example.geography_quiz_java.data.Country.withTranslations(com.example.geography_quiz_java.data.Country.fromCursor(cursor), cursor);

            String[] landmarkNames = cursor.getString(cursor.getColumnIndexOrThrow("landmark_names")).split("\\|");
            String[] landmarkPaths = cursor.getString(cursor.getColumnIndexOrThrow("landmark_paths")).split("\\|");
            String[] translatedLandmarkNames = cursor.isNull(cursor.getColumnIndexOrThrow("translated_landmark_names")) ?
                    landmarkNames : cursor.getString(cursor.getColumnIndexOrThrow("translated_landmark_names")).split("\\|");

            List<Landmark> landmarks = new ArrayList<>();
            for (int i = 0; i < landmarkNames.length; i++) {
                landmarks.add(new Landmark(
                        landmarkNames[i],
                        translatedLandmarkNames[i],
                        landmarkPaths[i]
                ));
            }
            countries.add(country.copyWithLandmarks(landmarks));
        }
        cursor.close();
        return countries;
    }

    public List<Landmark> getLandmarksForCountry(int countryId, String languageCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT l." + COLUMN_LANDMARK_NAME + ", l." + COLUMN_IMAGE_PATH + ", " +
                        "lt." + COLUMN_TRANSLATED_LANDMARK_NAME + " " +
                        "FROM " + TABLE_LANDMARKS + " l " +
                        "LEFT JOIN " + TABLE_LANDMARK_TRANSLATIONS + " lt ON l." + COLUMN_LANDMARK_ID + " = lt." + COLUMN_LANDMARK_ID + " " +
                        "AND lt." + COLUMN_LANGUAGE_CODE + " = ? " +
                        "WHERE l." + COLUMN_COUNTRY_ID + " = ?",
                new String[]{languageCode, String.valueOf(countryId)});

        List<Landmark> landmarks = new ArrayList<>();
        while (cursor.moveToNext()) {
            landmarks.add(new Landmark(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANDMARK_NAME)),
                    cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATED_LANDMARK_NAME)) ?
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANDMARK_NAME)) :
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSLATED_LANDMARK_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
            ));
        }
        cursor.close();
        return landmarks;
    }

    public List<String> getAvailableLanguages() {
        try {
            String[] files = appContext.getAssets().list("translations");
            if (files != null) {
                List<String> languages = new ArrayList<>();
                for (String file : files) {
                    languages.add(file.replace(".json", ""));
                }
                return languages;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static class LandmarkData {
        private final String name;
        private final String imagePath;
        private final int difficulty;

        public LandmarkData(String name, String imagePath, int difficulty) {
            this.name = name;
            this.imagePath = imagePath;
            this.difficulty = difficulty;
        }

        public String getName() {
            return name;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getDifficulty() {
            return difficulty;
        }
    }

    private static class LandmarkTranslationData {
        private final String originalName;
        private final String translatedName;

        public LandmarkTranslationData(String originalName, String translatedName) {
            this.originalName = originalName;
            this.translatedName = translatedName;
        }

        public String getOriginalName() {
            return originalName;
        }

        public String getTranslatedName() {
            return translatedName;
        }
    }
}