package com.example.geography_quiz_java.quiz;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.geography_quiz_java.R;
import com.example.geography_quiz_java.data.Country;
import com.example.geography_quiz_java.data.CountryDatabase;
import com.example.geography_quiz_java.utils.TranslationUtils;


import java.util.*;

public class CityQuiz extends AppCompatActivity {

    private int correctAnswerIndex = 0;
    private CountryDatabase databaseHelper;
    private String currentLanguage = "en";
    private int score = 0;
    private int totalQuestions = 0;
    private int questionsRemaining = 0;
    private List<Country> countries;
    private final Set<Pair<String, QuestionType>> usedCountryQuestions = new HashSet<>();
    private Country currentCountry;
    private QuestionType currentQuestionType;

    // UI Components
    private TextView questionText;
    private Button option1Btn;
    private Button option2Btn;
    private Button option3Btn;
    private Button option4Btn;
    private TextView feedbackText;
    private TextView scoreText;
    private TextView remainingText;
    private Button nextButton;

    enum QuestionType {
        CAPITAL,
        BIGGEST_CITY,
        CITY_IN_COUNTRY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize UI components
        questionText = findViewById(R.id.questionText);
        option1Btn = findViewById(R.id.option1Button);
        option2Btn = findViewById(R.id.option2Button);
        option3Btn = findViewById(R.id.option3Button);
        option4Btn = findViewById(R.id.option4Button);
        feedbackText = findViewById(R.id.feedbackText);
        scoreText = findViewById(R.id.scoreText);
        remainingText = findViewById(R.id.remainingText);
        nextButton = findViewById(R.id.nextButton);

        SharedPreferences sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentLanguage = sharedPref.getString("app_language", "en");
        if (currentLanguage == null) {
            currentLanguage = "en";
        }
        databaseHelper = new CountryDatabase(this);

        // Initialize quiz
        initializeQuiz();
    }

    private void initializeQuiz() {
        score = 0;
        usedCountryQuestions.clear();
        countries = !currentLanguage.equals("en") ?
                databaseHelper.getTranslatedRandomCountries(85, currentLanguage) :
                databaseHelper.getRandomCountries(85);

        if (countries.size() < 4) {
            showNotEnoughCountriesError();
            return;
        }

        totalQuestions = countries.size() * QuestionType.values().length;
        questionsRemaining = totalQuestions;
        updateScoreAndRemaining();

        loadCityQuestion();
    }

    private void loadCityQuestion() {
        feedbackText.setText("");

        if (questionsRemaining <= 0) {
            showQuizCompleted();
            return;
        }

        // Get a country and question type that hasn't been used yet
        List<Pair<Country, QuestionType>> availableQuestions = new ArrayList<>();
        for (Country country : countries) {
            for (QuestionType type : QuestionType.values()) {
                if (!usedCountryQuestions.contains(new Pair<>(country.getName(), type))) {
                    availableQuestions.add(new Pair<>(country, type));
                }
            }
        }

        if (availableQuestions.isEmpty()) {
            showQuizCompleted();
            return;
        }

        Random random = new Random();
        Pair<Country, QuestionType> randomPair = availableQuestions.get(random.nextInt(availableQuestions.size()));
        currentCountry = randomPair.first;
        currentQuestionType = randomPair.second;
        usedCountryQuestions.add(new Pair<>(currentCountry.getName(), currentQuestionType));
        questionsRemaining--;
        updateScoreAndRemaining();

        Triple<String, String, List<String>> questionData;
        switch (currentQuestionType) {
            case CAPITAL:
                questionData = generateCapitalQuestion(currentCountry);
                break;
            case BIGGEST_CITY:
                questionData = generateBiggestCityQuestion(currentCountry);
                break;
            case CITY_IN_COUNTRY:
                questionData = generateCityInCountryQuestion(currentCountry);
                break;
            default:
                throw new IllegalStateException("Unknown question type");
        }

        questionText.setText(questionData.first);
        option1Btn.setText(questionData.third.get(0));
        option2Btn.setText(questionData.third.get(1));
        option3Btn.setText(questionData.third.get(2));
        option4Btn.setText(questionData.third.get(3));

        // Make sure all buttons are visible
        option1Btn.setVisibility(View.VISIBLE);
        option2Btn.setVisibility(View.VISIBLE);
        option3Btn.setVisibility(View.VISIBLE);
        option4Btn.setVisibility(View.VISIBLE);

        correctAnswerIndex = questionData.third.indexOf(questionData.second) + 1;
        if (correctAnswerIndex == 0) { // Shouldn't happen as correct answer is always included
            correctAnswerIndex = 1;
        }

        option1Btn.setOnClickListener(v -> checkAnswer(1));
        option2Btn.setOnClickListener(v -> checkAnswer(2));
        option3Btn.setOnClickListener(v -> checkAnswer(3));
        option4Btn.setOnClickListener(v -> checkAnswer(4));

        nextButton.setOnClickListener(v -> loadCityQuestion());
    }

    @SuppressLint({"DefaultLocale", "StringFormatMatches"})
    private void updateScoreAndRemaining() {
        scoreText.setText(String.format(getString(R.string.score_format), score, totalQuestions));
        remainingText.setText(String.format(getString(R.string.remaining_format), questionsRemaining));
    }

    private List<Country> getSimilarCountries(Country targetCountry) {
        List<Country> similarCountries = new ArrayList<>();
        for (Country otherCountry : countries) {
            if (!otherCountry.getName().equals(targetCountry.getName())) {
                similarCountries.add(otherCountry);
            }
        }

        // Create a consistent comparator
        similarCountries.sort((o1, o2) -> {
            // First compare by category match
            boolean o1CategoryMatch = o1.getCategory().equals(targetCountry.getCategory());
            boolean o2CategoryMatch = o2.getCategory().equals(targetCountry.getCategory());
            if (o1CategoryMatch != o2CategoryMatch) {
                return o1CategoryMatch ? -1 : 1;
            }

            // Then compare by region match
            boolean o1RegionMatch = o1.getRegion().equals(targetCountry.getRegion());
            boolean o2RegionMatch = o2.getRegion().equals(targetCountry.getRegion());
            if (o1RegionMatch != o2RegionMatch) {
                return o1RegionMatch ? -1 : 1;
            }

            // Then compare by continent match
            boolean o1ContinentMatch = o1.getContinent().equals(targetCountry.getContinent());
            boolean o2ContinentMatch = o2.getContinent().equals(targetCountry.getContinent());
            if (o1ContinentMatch != o2ContinentMatch) {
                return o1ContinentMatch ? -1 : 1;
            }

            // If all else is equal, maintain original order
            return 0;
        });

        return similarCountries;
    }

    private List<String> getCitiesFromCountry(Country country) {
        List<String> cities = new ArrayList<>();
        String capital = !currentLanguage.equals("en") ? country.getTranslatedCapital() : country.getCapital();
        String bigCity = !currentLanguage.equals("en") ? country.getTranslatedBigCity() : country.getBigCity();
        String secondCity = !currentLanguage.equals("en") ? country.getTranslatedSecondCity() : country.getSecondCity();
        String thirdCity = !currentLanguage.equals("en") ? country.getTranslatedThirdCity() : country.getThirdCity();

        if (capital != null && !capital.isEmpty()) cities.add(capital);
        if (bigCity != null && !bigCity.isEmpty()) cities.add(bigCity);
        if (secondCity != null && !secondCity.isEmpty()) cities.add(secondCity);
        if (thirdCity != null && !thirdCity.isEmpty()) cities.add(thirdCity);

        return cities;
    }

    private Triple<String, String, List<String>> generateBiggestCityQuestion(Country targetCountry) {
        String correctAnswer = !currentLanguage.equals("en") ? targetCountry.getTranslatedBigCity() : targetCountry.getBigCity();
        List<String> targetCities = getCitiesFromCountry(targetCountry);
        List<Country> similarCountries = getSimilarCountries(targetCountry);

        // Create answer options
        List<String> answerOptions = new ArrayList<>();
        answerOptions.add(correctAnswer); // Always include correct answer

        // Get other cities from same country (excluding correct answer)
        List<String> sameCountryOtherCities = new ArrayList<>();
        for (String city : targetCities) {
            if (!city.equals(correctAnswer)) {
                sameCountryOtherCities.add(city);
            }
        }

        Random random = new Random();
        // Second option - 80% chance of being from same country
        if (!sameCountryOtherCities.isEmpty() && random.nextFloat() < 0.8f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCityFromSimilarCountries(similarCountries, answerOptions));
        }

        // Third option - 50% chance of being from same country
        if (!sameCountryOtherCities.isEmpty() && random.nextFloat() < 0.5f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCityFromSimilarCountries(similarCountries, answerOptions));
        }

        // Fourth option - 20% chance of being from same country (if we have enough cities)
        if (sameCountryOtherCities.size() >= 3 && random.nextFloat() < 0.2f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCityFromSimilarCountries(similarCountries, answerOptions));
        }

        String question = TranslationUtils.getTranslatedStringWithFormat(
                this,
                R.string.city_question,
                currentLanguage,
                !currentLanguage.equals("en") ? targetCountry.getTranslatedName() : targetCountry.getName()
        );

        // Shuffle the options
        java.util.Collections.shuffle(answerOptions);
        return new Triple<>(question, correctAnswer, answerOptions);
    }

    private Triple<String, String, List<String>> generateCapitalQuestion(Country targetCountry) {
        String correctAnswer = !currentLanguage.equals("en") ? targetCountry.getTranslatedCapital() : targetCountry.getCapital();
        List<String> targetCities = getCitiesFromCountry(targetCountry);
        List<Country> similarCountries = getSimilarCountries(targetCountry);

        // Create answer options
        List<String> answerOptions = new ArrayList<>();
        answerOptions.add(correctAnswer); // Always include correct answer

        // Get other cities from same country (excluding correct answer)
        List<String> sameCountryOtherCities = new ArrayList<>();
        for (String city : targetCities) {
            if (!city.equals(correctAnswer)) {
                sameCountryOtherCities.add(city);
            }
        }

        Random random = new Random();
        // Second option - 80% chance of being from same country
        if (!sameCountryOtherCities.isEmpty() && random.nextFloat() < 0.8f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCapitalFromSimilarCountries(similarCountries, answerOptions));
        }

        // Third option - 50% chance of being from same country
        if (!sameCountryOtherCities.isEmpty() && random.nextFloat() < 0.5f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCapitalFromSimilarCountries(similarCountries, answerOptions));
        }

        // Fourth option - 20% chance of being from same country (if we have enough cities)
        if (sameCountryOtherCities.size() >= 3 && random.nextFloat() < 0.2f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCapitalFromSimilarCountries(similarCountries, answerOptions));
        }

        String question = TranslationUtils.getTranslatedStringWithFormat(
                this,
                R.string.capital_question,
                currentLanguage,
                !currentLanguage.equals("en") ? targetCountry.getTranslatedName() : targetCountry.getName()
        );

        // Shuffle the options
        java.util.Collections.shuffle(answerOptions);
        return new Triple<>(question, correctAnswer, answerOptions);
    }

    private Triple<String, String, List<String>> generateCityInCountryQuestion(Country targetCountry) {
        List<String> targetCities = getCitiesFromCountry(targetCountry);
        Random random = new Random();
        String correctCity = targetCities.get(random.nextInt(targetCities.size()));
        List<Country> similarCountries = getSimilarCountries(targetCountry);

        // Create answer options
        List<String> answerOptions = new ArrayList<>();
        answerOptions.add(correctCity); // Always include correct answer

        // Get other cities from same country (excluding correct answer)
        List<String> sameCountryOtherCities = new ArrayList<>();
        for (String city : targetCities) {
            if (!city.equals(correctCity)) {
                sameCountryOtherCities.add(city);
            }
        }

        // Second option - 80% chance of being from same country
        if (!sameCountryOtherCities.isEmpty() && random.nextFloat() < 0.8f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCityFromSimilarCountries(similarCountries, answerOptions));
        }

        // Third option - 50% chance of being from same country
        if (!sameCountryOtherCities.isEmpty() && random.nextFloat() < 0.5f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCityFromSimilarCountries(similarCountries, answerOptions));
        }

        // Fourth option - 20% chance of being from same country (if we have enough cities)
        if (sameCountryOtherCities.size() >= 3 && random.nextFloat() < 0.2f) {
            answerOptions.add(sameCountryOtherCities.get(random.nextInt(sameCountryOtherCities.size())));
        } else {
            answerOptions.add(getRandomCityFromSimilarCountries(similarCountries, answerOptions));
        }

        String question = TranslationUtils.getTranslatedStringWithFormat(
                this,
                R.string.city_in_country_question,
                currentLanguage,
                !currentLanguage.equals("en") ? targetCountry.getTranslatedName() : targetCountry.getName()
        );

        // Shuffle the options
        java.util.Collections.shuffle(answerOptions);
        return new Triple<>(question, correctCity, answerOptions);
    }

    private String getRandomCityFromSimilarCountries(List<Country> similarCountries, List<String> exclude) {
        // Try to get cities from similar countries (prioritizing more similar ones)
        List<String> candidates = new ArrayList<>();
        for (Country country : similarCountries) {
            List<String> cities = getCitiesFromCountry(country);
            for (String city : cities) {
                if (!exclude.contains(city)) {
                    candidates.add(city);
                }
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(new Random().nextInt(candidates.size()));
        } else {
            // Fallback to any city not in exclude list
            List<String> allCities = new ArrayList<>();
            for (Country country : countries) {
                List<String> cities = getCitiesFromCountry(country);
                for (String city : cities) {
                    if (!exclude.contains(city)) {
                        allCities.add(city);
                    }
                }
            }
            return allCities.isEmpty() ? "Unknown" : allCities.get(new Random().nextInt(allCities.size()));
        }
    }

    private String getRandomCapitalFromSimilarCountries(List<Country> similarCountries, List<String> exclude) {
        // Try to get capitals from similar countries (prioritizing more similar ones)
        List<String> candidates = new ArrayList<>();
        for (Country country : similarCountries) {
            String capital = !currentLanguage.equals("en") ? country.getTranslatedCapital() : country.getCapital();
            if (capital != null && !capital.isEmpty() && !exclude.contains(capital)) {
                candidates.add(capital);
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(new Random().nextInt(candidates.size()));
        } else {
            // Fallback to any capital not in exclude list
            List<String> allCapitals = new ArrayList<>();
            for (Country country : countries) {
                String capital = !currentLanguage.equals("en") ? country.getTranslatedCapital() : country.getCapital();
                if (capital != null && !capital.isEmpty() && !exclude.contains(capital)) {
                    allCapitals.add(capital);
                }
            }
            return allCapitals.isEmpty() ? "Unknown" : allCapitals.get(new Random().nextInt(allCapitals.size()));
        }
    }

    private void showNotEnoughCountriesError() {
        questionText.setText(TranslationUtils.getTranslatedString(
                this,
                R.string.notEnoughCountry,
                currentLanguage
        ));
        option1Btn.setVisibility(View.GONE);
        option2Btn.setVisibility(View.GONE);
        option3Btn.setVisibility(View.GONE);
        option4Btn.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private void showQuizCompleted() {
        questionText.setText(TranslationUtils.getTranslatedStringWithFormat(
                this,
                R.string.quiz_complete,
                currentLanguage,
                score,
                totalQuestions
        ));
        option1Btn.setVisibility(View.GONE);
        option2Btn.setVisibility(View.GONE);
        option3Btn.setVisibility(View.GONE);
        option4Btn.setVisibility(View.GONE);
        nextButton.setText(getString(R.string.restart_quiz));
        nextButton.setOnClickListener(v -> {
            initializeQuiz();
            nextButton.setText(getString(R.string.next_question));
        });
    }

    private void checkAnswer(int selectedOption) {
        boolean isCorrect = selectedOption == correctAnswerIndex;
        feedbackText.setText(isCorrect ?
                TranslationUtils.getTranslatedString(this, R.string.correct, currentLanguage) :
                TranslationUtils.getTranslatedString(this, R.string.wrong, currentLanguage));
        if (isCorrect) {
            score++;
            updateScoreAndRemaining();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

    // Helper class to replace Kotlin's Pair
    static class Pair<F, S> {
        final F first;
        final S second;

        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(first, pair.first) &&
                    Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }

    // Helper class to replace Kotlin's Triple
    static class Triple<F, S, T> {
        final F first;
        final S second;
        final T third;

        Triple(F first, S second, T third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
}