package com.example.geography_quiz_java.quiz;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.geography_quiz_java.R;
import com.example.geography_quiz_java.data.Country;
import com.example.geography_quiz_java.data.CountryDatabase;
import com.example.geography_quiz_java.utils.TranslationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FlagQuiz extends AppCompatActivity {

    private String currentLanguage = "en";
    private CountryDatabase databaseHelper;
    private ImageView flagImageView;
    private int correctAnswerIndex = 0;
    private int score = 0;
    private int totalQuestions = 0;
    private int questionsRemaining = 0;
    private List<Country> countries;
    private Set<String> usedCountries = new HashSet<>();
    private Country currentCountry;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_img);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI components
        questionText = findViewById(R.id.questionText);
        flagImageView = findViewById(R.id.flagImageView);
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

        initializeQuiz();
    }

    private void initializeQuiz() {
        score = 0;
        usedCountries.clear();
        countries = !currentLanguage.equals("en") ?
                databaseHelper.getTranslatedRandomCountries(85, currentLanguage) :
                databaseHelper.getRandomCountries(85);

        option1Btn.setVisibility(View.VISIBLE);
        option2Btn.setVisibility(View.VISIBLE);
        option3Btn.setVisibility(View.VISIBLE);
        option4Btn.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setText(getString(R.string.next_question));

        if (countries.size() < 4) {
            showError();
            return;
        }

        totalQuestions = countries.size();
        questionsRemaining = totalQuestions;
        updateScoreAndRemaining();

        loadFlagQuestion();
    }

    private void loadFlagQuestion() {
        feedbackText.setText("");

        if (questionsRemaining <= 0) {
            showQuizCompleted();
            return;
        }

        // Reset used countries if we're running low
        if (usedCountries.size() >= countries.size() - 3) {
            usedCountries.clear();
        }

        // Get available countries with null checks
        List<Country> availableCountries = new ArrayList<>();
        for (Country country : countries) {
            if (!usedCountries.contains(country.getName()) &&
                    !country.getName().isEmpty() &&
                    !country.getCountryCode().isEmpty()) {
                availableCountries.add(country);
            }
        }

        if (availableCountries.isEmpty()) {
            showQuizCompleted();
            return;
        }

        // Select current country
        currentCountry = availableCountries.get(new Random().nextInt(availableCountries.size()));
        usedCountries.add(currentCountry.getName());
        questionsRemaining--;
        updateScoreAndRemaining();

        // Load the flag image
        loadSvgFlag(currentCountry.getCountryCode());

        // Create answer options with weighted probabilities
        List<Country> answerOptions = new ArrayList<>();
        answerOptions.add(currentCountry); // Correct answer

        // Get all other available countries
        List<Country> otherCountries = new ArrayList<>();
        for (Country country : countries) {
            if (!country.getName().equals(currentCountry.getName()) &&
                    !country.getName().isEmpty() &&
                    !country.getCountryCode().isEmpty()) {
                otherCountries.add(country);
            }
        }

        // Categorize countries based on similarity to current country
        List<CountryWithScore> categorizedCountries = new ArrayList<>();
        for (Country country : otherCountries) {
            int similarityScore = 0;

            // 1. Flag similarity (highest weight)
            Set<String> currentColors = new HashSet<>(currentCountry.getFlagColors());
            Set<String> countryColors = new HashSet<>(country.getFlagColors());
            currentColors.retainAll(countryColors);
            int colorMatches = currentColors.size();

            Set<String> currentEmblems = new HashSet<>(currentCountry.getFlagEmblem());
            Set<String> countryEmblems = new HashSet<>(country.getFlagEmblem());
            currentEmblems.retainAll(countryEmblems);
            int emblemMatches = currentEmblems.size();

            similarityScore += (colorMatches * 20) + (emblemMatches * 30);

            // 2. Geographic proximity (medium weight)
            if (country.getContinent().equals(currentCountry.getContinent())) similarityScore += 15;
            if (country.getRegion().equals(currentCountry.getRegion())) similarityScore += 20;
            if (country.getCategory().equals(currentCountry.getCategory())) similarityScore += 25;

            // Categorize based on score
            Category category;
            if (similarityScore >= 70) {
                category = Category.HIGH;
            } else if (similarityScore >= 40) {
                category = Category.MEDIUM;
            } else {
                category = Category.LOW;
            }
            categorizedCountries.add(new CountryWithScore(country, similarityScore, category));
        }

        // Group by category
        List<CountryWithScore> highScoreCountries = new ArrayList<>();
        List<CountryWithScore> mediumScoreCountries = new ArrayList<>();
        List<CountryWithScore> lowScoreCountries = new ArrayList<>();

        for (CountryWithScore cws : categorizedCountries) {
            switch (cws.category) {
                case HIGH:
                    highScoreCountries.add(cws);
                    break;
                case MEDIUM:
                    mediumScoreCountries.add(cws);
                    break;
                case LOW:
                    lowScoreCountries.add(cws);
                    break;
            }
        }

        Log.d("FlagQuiz", "High similarity options: " + highScoreCountries.size());
        Log.d("FlagQuiz", "Medium similarity options: " + mediumScoreCountries.size());
        Log.d("FlagQuiz", "Low similarity options: " + lowScoreCountries.size());

        // Select 3 distractors with weighted probabilities
        Set<Country> selectedDistractors = new HashSet<>();
        Random random = new Random();

        while (selectedDistractors.size() < 3 && selectedDistractors.size() < otherCountries.size()) {
            double rand = random.nextDouble();

            if (rand < 0.8 && !highScoreCountries.isEmpty()) {
                Country country = highScoreCountries.get(random.nextInt(highScoreCountries.size())).country;
                if (!selectedDistractors.contains(country)) {
                    selectedDistractors.add(country);
                }
            } else if (rand < 0.9 && !mediumScoreCountries.isEmpty()) {
                Country country = mediumScoreCountries.get(random.nextInt(mediumScoreCountries.size())).country;
                if (!selectedDistractors.contains(country)) {
                    selectedDistractors.add(country);
                }
            } else if (rand < 1.0 && !lowScoreCountries.isEmpty()) {
                Country country = lowScoreCountries.get(random.nextInt(lowScoreCountries.size())).country;
                if (!selectedDistractors.contains(country)) {
                    selectedDistractors.add(country);
                }
            } else {
                // Fallback to random selection if categories are empty
                List<Country> remaining = new ArrayList<>();
                for (Country country : otherCountries) {
                    if (!selectedDistractors.contains(country)) {
                        remaining.add(country);
                    }
                }
                if (!remaining.isEmpty()) {
                    selectedDistractors.add(remaining.get(random.nextInt(remaining.size())));
                }
            }
        }

        answerOptions.addAll(selectedDistractors);

        // If we still don't have enough options, fill with random
        if (answerOptions.size() < 4) {
            List<Country> remainingOptions = new ArrayList<>();
            for (Country country : otherCountries) {
                if (!answerOptions.contains(country)) {
                    remainingOptions.add(country);
                }
            }
            Collections.shuffle(remainingOptions);
            int needed = 4 - answerOptions.size();
            answerOptions.addAll(remainingOptions.subList(0, Math.min(needed, remainingOptions.size())));
        }

        // Shuffle the options
        Collections.shuffle(answerOptions);

        // Set correct answer index
        correctAnswerIndex = -1;
        for (int i = 0; i < answerOptions.size(); i++) {
            if (answerOptions.get(i).getName().equals(currentCountry.getName())) {
                correctAnswerIndex = i;
                break;
            }
        }

        // Update UI
        questionText.setText(getString(R.string.flag_question));
        option1Btn.setText(answerOptions.get(0).getName());
        option2Btn.setText(answerOptions.get(1).getName());
        option3Btn.setText(answerOptions.get(2).getName());
        option4Btn.setText(answerOptions.get(3).getName());

        // Reset button states
        List<Button> buttons = Arrays.asList(option1Btn, option2Btn, option3Btn, option4Btn);
        for (Button button : buttons) {
            button.setEnabled(true);
            button.setVisibility(View.VISIBLE);
        }

        // Set click listeners
        option1Btn.setOnClickListener(v -> checkAnswer(0));
        option2Btn.setOnClickListener(v -> checkAnswer(1));
        option3Btn.setOnClickListener(v -> checkAnswer(2));
        option4Btn.setOnClickListener(v -> checkAnswer(3));

        nextButton.setOnClickListener(v -> loadFlagQuestion());
    }

    // Supporting classes
    private enum Category { HIGH, MEDIUM, LOW }

    private static class CountryWithScore {
        final Country country;
        final int score;
        final Category category;

        CountryWithScore(Country country, int score, Category category) {
            this.country = country;
            this.score = score;
            this.category = category;
        }
    }

    @SuppressLint("StringFormatMatches")
    private void updateScoreAndRemaining() {
        scoreText.setText(getString(R.string.score_format, score, totalQuestions));
        remainingText.setText(getString(R.string.remaining_format, questionsRemaining));
    }

    private void loadSvgFlag(String countryCode) {
        try {
            String fileName = countryCode.toLowerCase() + ".svg";
            Log.d("FlagQuiz", "Attempting to load: " + fileName);

            InputStream inputStream = getAssets().open("flag/" + fileName);
            SVG svg = SVG.getFromInputStream(inputStream);
            PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
            flagImageView.setImageDrawable(drawable);
            Log.d("FlagQuiz", "Successfully loaded: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FlagQuiz", "Error loading flag: " + e.getMessage());
            flagImageView.setImageResource(R.drawable.ic_flag);
        } catch (SVGParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAnswer(int selectedIndex) {
        boolean isCorrect = selectedIndex == correctAnswerIndex;
        if (isCorrect) {
            score++;
            updateScoreAndRemaining();
            feedbackText.setText(TranslationUtils.getTranslatedString(this, R.string.correct, currentLanguage));
        } else {
            String correctCountryName = !currentLanguage.equals("en") ?
                    currentCountry.getTranslatedName() : currentCountry.getName();
            feedbackText.setText(TranslationUtils.getTranslatedStringWithFormat(
                    this,
                    R.string.wrong,
                    currentLanguage,
                    correctCountryName
            ));
        }
    }

    private void showError() {
        questionText.setText(TranslationUtils.getTranslatedString(this, R.string.notEnoughCountry, currentLanguage));
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

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

    private static class WeightedCountry {
        final Country country;
        final int weight;

        WeightedCountry(Country country, int weight) {
            this.country = country;
            this.weight = weight;
        }
    }
}