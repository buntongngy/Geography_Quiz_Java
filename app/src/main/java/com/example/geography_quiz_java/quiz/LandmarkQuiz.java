package com.example.geography_quiz_java.quiz;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.geography_quiz_java.R;
import com.example.geography_quiz_java.data.Country;
import com.example.geography_quiz_java.data.CountryDatabase;
import com.example.geography_quiz_java.data.Landmark;
import com.example.geography_quiz_java.utils.TranslationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LandmarkQuiz extends AppCompatActivity {

    private String currentLanguage = "en";
    private CountryDatabase databaseHelper;
    private ImageView landmarkImageView;
    private int correctAnswerIndex = 0;
    private String currentLandmarkPath;
    private int score = 0;
    private int totalQuestions = 0;
    private int questionsRemaining = 0;
    private List<Country> countriesWithLandmarks;
    private final Set<String> usedLandmarks = new HashSet<>();

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

        // Initialize UI components
        questionText = findViewById(R.id.questionText);
        landmarkImageView = findViewById(R.id.flagImageView);
        option1Btn = findViewById(R.id.option1Button);
        option2Btn = findViewById(R.id.option2Button);
        option3Btn = findViewById(R.id.option3Button);
        option4Btn = findViewById(R.id.option4Button);
        feedbackText = findViewById(R.id.feedbackText);
        scoreText = findViewById(R.id.scoreText);
        remainingText = findViewById(R.id.remainingText);
        nextButton = findViewById(R.id.nextButton);

        currentLanguage = getSharedPreferences("AppSettings", MODE_PRIVATE)
                .getString("app_language", "en");
        if (currentLanguage == null) {
            currentLanguage = "en";
        }
        databaseHelper = new CountryDatabase(this);

        initializeQuiz();
    }

    private void initializeQuiz() {
        score = 0;
        usedLandmarks.clear();
        countriesWithLandmarks = new ArrayList<>();
        List<Country> allCountries = databaseHelper.getAllCountriesWithLandmarks(currentLanguage);
        for (Country country : allCountries) {
            if (!country.getLandmarks().isEmpty()) {
                countriesWithLandmarks.add(country);
            }
        }

        // Reset UI visibility
        option1Btn.setVisibility(View.VISIBLE);
        option2Btn.setVisibility(View.VISIBLE);
        option3Btn.setVisibility(View.VISIBLE);
        option4Btn.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setText(getString(R.string.next_question));

        if (countriesWithLandmarks.size() < 4) {
            showError();
            return;
        }

        totalQuestions = 0;
        for (Country country : countriesWithLandmarks) {
            totalQuestions += country.getLandmarks().size();
        }
        questionsRemaining = Math.min(totalQuestions, 20); // Limit to 20 questions max
        updateScoreAndRemaining();

        loadLandmarkQuestion();
    }

    private void loadLandmarkQuestion() {
        feedbackText.setText("");

        if (questionsRemaining <= 0) {
            showQuizCompleted();
            return;
        }

        // Get all available landmarks that haven't been used yet
        List<Pair> availableLandmarks = new ArrayList<>();
        for (Country country : countriesWithLandmarks) {
            for (Landmark landmark : country.getLandmarks()) {
                String key = country.getName() + "_" + landmark.getImagePath();
                if (!usedLandmarks.contains(key)) {
                    availableLandmarks.add(new Pair(country, landmark));
                }
            }
        }

        if (availableLandmarks.isEmpty()) {
            showQuizCompleted();
            return;
        }

        // Select a random available landmark
        Pair selectedPair = availableLandmarks.get((int) (Math.random() * availableLandmarks.size()));
        Country targetCountry = selectedPair.country;
        Landmark targetLandmark = selectedPair.landmark;
        usedLandmarks.add(targetCountry.getName() + "_" + targetLandmark.getImagePath());
        questionsRemaining--;
        updateScoreAndRemaining();

        currentLandmarkPath = targetLandmark.getImagePath();
        loadLandmarkImage(targetLandmark.getImagePath());

        // Get 3 other random countries (excluding target country)
        List<Country> otherCountries = new ArrayList<>();
        for (Country country : countriesWithLandmarks) {
            if (!country.equals(targetCountry)) {
                otherCountries.add(country);
            }
        }
        // Shuffle and take first 3
        java.util.Collections.shuffle(otherCountries);
        if (otherCountries.size() > 3) {
            otherCountries = otherCountries.subList(0, 3);
        }

        List<Country> answerOptions = new ArrayList<>();
        answerOptions.add(targetCountry);
        answerOptions.addAll(otherCountries);
        java.util.Collections.shuffle(answerOptions);
        correctAnswerIndex = answerOptions.indexOf(targetCountry);

        // Update UI
        questionText.setText(TranslationUtils.getTranslatedString(this, R.string.landmark_question, currentLanguage));
        option1Btn.setText(currentLanguage.equals("en") ? answerOptions.get(0).getName() : answerOptions.get(0).getTranslatedName());
        option2Btn.setText(currentLanguage.equals("en") ? answerOptions.get(1).getName() : answerOptions.get(1).getTranslatedName());
        option3Btn.setText(currentLanguage.equals("en") ? answerOptions.get(2).getName() : answerOptions.get(2).getTranslatedName());
        option4Btn.setText(currentLanguage.equals("en") ? answerOptions.get(3).getName() : answerOptions.get(3).getTranslatedName());

        option1Btn.setOnClickListener(v -> checkAnswer(0));
        option2Btn.setOnClickListener(v -> checkAnswer(1));
        option3Btn.setOnClickListener(v -> checkAnswer(2));
        option4Btn.setOnClickListener(v -> checkAnswer(3));

        nextButton.setOnClickListener(v -> loadLandmarkQuestion());
    }

    @SuppressLint("StringFormatMatches")
    private void updateScoreAndRemaining() {
        scoreText.setText(getString(R.string.score_format, score, totalQuestions));
        remainingText.setText(getString(R.string.remaining_format, questionsRemaining));
    }

    private void loadLandmarkImage(String imagePath) {
        try {
            InputStream inputStream = getAssets().open(imagePath);
            landmarkImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            inputStream.close();
            Log.d("LandmarkQuiz", "Successfully loaded landmark: " + imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LandmarkQuiz", "Error loading landmark: " + e.getMessage());
            landmarkImageView.setImageResource(R.drawable.ic_landmark);
        }
    }

    private void checkAnswer(int selectedIndex) {
        boolean isCorrect = selectedIndex == correctAnswerIndex;
        if (isCorrect) {
            score++;
            updateScoreAndRemaining();
            feedbackText.setText(TranslationUtils.getTranslatedString(this, R.string.correct, currentLanguage));
        } else {
            feedbackText.setText(TranslationUtils.getTranslatedString(this, R.string.wrong, currentLanguage));
        }
    }

    private void showError() {
        questionText.setText(TranslationUtils.getTranslatedString(this, R.string.notLandmark, currentLanguage));
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
        nextButton.setOnClickListener(v -> initializeQuiz());
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

    // Helper class to pair Country and Landmark
    private static class Pair {
        Country country;
        Landmark landmark;

        Pair(Country country, Landmark landmark) {
            this.country = country;
            this.landmark = landmark;
        }
    }
}