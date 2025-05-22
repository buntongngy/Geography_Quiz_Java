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

public class LanguageQuiz extends AppCompatActivity {

    private int correctAnswerIndex = 0;
    private CountryDatabase databaseHelper;
    private String currentLanguage = "en";
    private int score = 0;
    private int totalQuestions = 0;
    private int questionsRemaining = 0;
    private List<Country> countries;
    private Set<Pair<String, QuestionType>> usedQuestions = new HashSet<>();

    enum QuestionType {
        LANGUAGE,
        LANGUAGE_COUNT
    }

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
        setContentView(R.layout.activity_quiz);

        initializeUI();
        initializeQuiz();
    }

    private void initializeUI() {
        questionText = findViewById(R.id.questionText);
        option1Btn = findViewById(R.id.option1Button);
        option2Btn = findViewById(R.id.option2Button);
        option3Btn = findViewById(R.id.option3Button);
        option4Btn = findViewById(R.id.option4Button);
        feedbackText = findViewById(R.id.feedbackText);
        scoreText = findViewById(R.id.scoreText);
        remainingText = findViewById(R.id.remainingText);
        nextButton = findViewById(R.id.nextButton);
    }

    private void initializeQuiz() {
        score = 0;
        usedQuestions.clear();

        SharedPreferences sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentLanguage = sharedPref.getString("app_language", "en");
        if (currentLanguage == null) {
            currentLanguage = "en";
        }
        databaseHelper = new CountryDatabase(this);

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

        loadLanguageQuestion();
    }

    private void loadLanguageQuestion() {
        feedbackText.setText("");

        if (questionsRemaining <= 0) {
            showQuizCompleted();
            return;
        }

        // Get available questions (country + type combinations not yet used)
        List<Pair<Country, QuestionType>> availableQuestions = new ArrayList<>();
        for (Country country : countries) {
            for (QuestionType type : QuestionType.values()) {
                Pair<String, QuestionType> pair = new Pair<>(country.getName(), type);
                if (!usedQuestions.contains(pair)) {
                    availableQuestions.add(new Pair<>(country, type));
                }
            }
        }

        if (availableQuestions.isEmpty()) {
            showQuizCompleted();
            return;
        }

        // Select a random available question
        Pair<Country, QuestionType> questionPair = availableQuestions.get(
                new Random().nextInt(availableQuestions.size()));
        Country targetCountry = questionPair.first;
        QuestionType questionType = questionPair.second;

        usedQuestions.add(new Pair<>(targetCountry.getName(), questionType));
        questionsRemaining--;
        updateScoreAndRemaining();

        // Generate the question data
        QuestionData questionData;
        switch (questionType) {
            case LANGUAGE:
                questionData = generateLanguageQuestion(targetCountry, countries);
                break;
            case LANGUAGE_COUNT:
                questionData = generateLanguageCountQuestion(targetCountry);
                break;
            default:
                questionData = generateLanguageCountQuestion(targetCountry);
                break;
        }

        // Display the question and answers
        displayQuestion(questionData);
    }

    private void displayQuestion(QuestionData questionData) {
        // Ensure we have exactly 4 answers (pad with empty strings if needed)
        List<String> paddedAnswers = new ArrayList<>(questionData.answers);
        while (paddedAnswers.size() < 4) {
            paddedAnswers.add("");
        }
        paddedAnswers = paddedAnswers.subList(0, 4);

        questionText.setText(questionData.question);
        option1Btn.setText(paddedAnswers.get(0).isEmpty() ?
                getString(R.string.unknownAns) : paddedAnswers.get(0));
        option2Btn.setText(paddedAnswers.get(1).isEmpty() ?
                getString(R.string.unknownAns) : paddedAnswers.get(1));
        option3Btn.setText(paddedAnswers.get(2).isEmpty() ?
                getString(R.string.unknownAns) : paddedAnswers.get(2));
        option4Btn.setText(paddedAnswers.get(3).isEmpty() ?
                getString(R.string.unknownAns) : paddedAnswers.get(3));

        // Hide buttons with empty answers
        option1Btn.setVisibility(paddedAnswers.get(0).isEmpty() ? View.GONE : View.VISIBLE);
        option2Btn.setVisibility(paddedAnswers.get(1).isEmpty() ? View.GONE : View.VISIBLE);
        option3Btn.setVisibility(paddedAnswers.get(2).isEmpty() ? View.GONE : View.VISIBLE);
        option4Btn.setVisibility(paddedAnswers.get(3).isEmpty() ? View.GONE : View.VISIBLE);

        correctAnswerIndex = paddedAnswers.indexOf(questionData.correctAnswer);
        if (correctAnswerIndex == -1) {
            correctAnswerIndex = 0;
        }

        option1Btn.setOnClickListener(v -> checkAnswer(0));
        option2Btn.setOnClickListener(v -> checkAnswer(1));
        option3Btn.setOnClickListener(v -> checkAnswer(2));
        option4Btn.setOnClickListener(v -> checkAnswer(3));

        nextButton.setOnClickListener(v -> loadLanguageQuestion());
    }

    @SuppressLint("StringFormatMatches")
    private void updateScoreAndRemaining() {
        scoreText.setText(getString(R.string.score_format, score, totalQuestions));
        remainingText.setText(getString(R.string.remaining_format, questionsRemaining));
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

    private QuestionData generateLanguageQuestion(Country targetCountry, List<Country> allCountries) {
        List<String> targetLanguages = !currentLanguage.equals("en") &&
                !targetCountry.getTranslatedLanguages().isEmpty() ?
                targetCountry.getTranslatedLanguages() :
                targetCountry.getLanguages();

        if (targetLanguages.isEmpty()) {
            return generateLanguageCountQuestion(targetCountry);
        }

        String correctLanguage = targetLanguages.get(new Random().nextInt(targetLanguages.size()));
        String countryName = getCountryDisplayName(targetCountry);

        List<Country> similarCountries = new ArrayList<>();
        for (Country country : allCountries) {
            if (!country.getName().equals(targetCountry.getName())) {
                similarCountries.add(country);
            }
        }

        Set<String> otherLanguages = new HashSet<>();
        for (Country country : similarCountries) {
            List<String> langs = !currentLanguage.equals("en") ?
                    country.getTranslatedLanguages() : country.getLanguages();
            for (String lang : langs) {
                if (!lang.equals(correctLanguage)) {
                    otherLanguages.add(lang);
                }
            }
        }

        List<String> otherLanguagesList = new ArrayList<>(otherLanguages);
        Collections.shuffle(otherLanguagesList);
        otherLanguagesList = otherLanguagesList.subList(0, Math.min(3, otherLanguagesList.size()));

        List<String> answers = new ArrayList<>();
        answers.add(correctLanguage);
        answers.addAll(otherLanguagesList);
        Collections.shuffle(answers);

        return new QuestionData(
                TranslationUtils.getTranslatedStringWithFormat(
                        this,
                        R.string.languageQuestion,
                        currentLanguage,
                        countryName
                ),
                correctLanguage,
                answers
        );
    }

    private QuestionData generateLanguageCountQuestion(Country targetCountry) {
        int correctCount = targetCountry.getLanguages().size();
        List<Integer> possibleCounts = new ArrayList<>();

        // Generate plausible nearby counts
        if (correctCount == 1) {
            possibleCounts.addAll(Arrays.asList(1, 2, 4, 3));
        } else if (correctCount == 2) {
            possibleCounts.addAll(Arrays.asList(2, 1, 3, 4));
        } else {
            possibleCounts.addAll(Arrays.asList(
                    correctCount,
                    correctCount - 1,
                    correctCount + 1,
                    correctCount > 2 ? correctCount - 2 : correctCount + 2
            ));
        }

        // Ensure all counts are positive and we have exactly 4 unique options
        Set<Integer> uniqueCounts = new LinkedHashSet<>();
        for (int count : possibleCounts) {
            if (count >= (correctCount == 1 ? 1 : 0)) {
                uniqueCounts.add(count);
            }
        }

        List<Integer> uniqueCountsList = new ArrayList<>(uniqueCounts);
        while (uniqueCountsList.size() < 4) {
            int nextNum = uniqueCountsList.isEmpty() ? 1 :
                    Collections.max(uniqueCountsList) + 1;
            if (!uniqueCountsList.contains(nextNum)) {
                uniqueCountsList.add(nextNum);
            } else {
                uniqueCountsList.add(new Random().nextInt(10) + 1);
            }
        }

        Collections.shuffle(uniqueCountsList);
        List<String> answers = new ArrayList<>();
        for (int count : uniqueCountsList) {
            answers.add(String.valueOf(count));
        }

        return new QuestionData(
                TranslationUtils.getTranslatedStringWithFormat(
                        this,
                        R.string.languageCountQuestion,
                        currentLanguage,
                        !currentLanguage.equals("en") ?
                                targetCountry.getTranslatedName() : targetCountry.getName()
                ),
                String.valueOf(correctCount),
                answers
        );
    }

    private String getCountryDisplayName(Country country) {
        return !currentLanguage.equals("en") && !country.getTranslatedName().isEmpty() ?
                country.getTranslatedName() : country.getName();
    }

    private void checkAnswer(int selectedIndex) {
        if (selectedIndex == correctAnswerIndex) {
            score++;
            updateScoreAndRemaining();
            feedbackText.setText(TranslationUtils.getTranslatedString(
                    this, R.string.correct, currentLanguage));
        } else {
            feedbackText.setText(TranslationUtils.getTranslatedString(
                    this, R.string.wrong, currentLanguage));
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

    private static class QuestionData {
        final String question;
        final String correctAnswer;
        final List<String> answers;

        QuestionData(String question, String correctAnswer, List<String> answers) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.answers = answers;
        }
    }

    private static class Pair<A, B> {
        final A first;
        final B second;

        Pair(A first, B second) {
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
}