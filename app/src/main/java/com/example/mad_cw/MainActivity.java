package com.example.mad_cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mad_cw.DatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int currentQuestionIndex = 0;
    private List<Question> questions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questions = getQuestionsFromIntent();
        if (!questions.isEmpty()) {
            displayQuestion(questions.get(currentQuestionIndex));
            setupAnswerButtons();
        } else {
            // TODO: Display an empty state or an error message when there are no questions
        }

        dbHelper = new DatabaseHelper(this);
        setupAnswerButtons();
        int desiredTopicId = 1; // Replace '1' with the ID of the desired topic
        Topic desiredTopic = dbHelper.getTopicById(desiredTopicId);
        if (desiredTopic != null) {
            loadTopicQuestions(desiredTopic);
        } else {
            // TODO: Display an error message if the topic is not found
        }
    }

    private List<Question> getQuestionsFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return (List<Question>) bundle.getSerializable("questions");
        } else {
            return new ArrayList<>();
        }
    }

    private void loadTopicQuestions(Topic selectedTopic) {
        List<Question> questions = getQuestionsFromIntent(); // Update this line to use the new method
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("questions", (Serializable) questions);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // show the questions and answer options
    private void displayQuestion(Question question) {
        TextView questionText = findViewById(R.id.question_text);
        Button answerButtonA = findViewById(R.id.answer_button_a);
        Button answerButtonB = findViewById(R.id.answer_button_b);
        Button answerButtonC = findViewById(R.id.answer_button_c);

        questionText.setText(question.getQuestionText());
        answerButtonA.setText(question.getAnswerA());
        answerButtonB.setText(question.getAnswerB());
        answerButtonC.setText(question.getAnswerC());
    }

    private void setupAnswerButtons() {
        // ... (same as before)
    }

    private void checkAnswer(String selectedAnswer) {
        // ... (same as before)
    }

    private Question getCurrentQuestion() {
        if (questions != null && currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }
}