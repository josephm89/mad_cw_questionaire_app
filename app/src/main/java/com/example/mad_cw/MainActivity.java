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
    private List<Question> questions;
    private int currentQuestionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        setupAnswerButtons();
        loadTopicQuestions(1); // Replace '1' with the ID of the desired topic
    }

    private List<Question> getQuestionsForTopic(int topicId) {
        return dbHelper.getQuestionsForTopic(topicId);
    }

    private void loadTopicQuestions(int topicId) {
        questions = getQuestionsForTopic(topicId);
        if (questions != null && !questions.isEmpty()) {
            currentQuestionIndex = 0;
            displayQuestion(questions.get(currentQuestionIndex));
        } else {
            Toast.makeText(this, "No questions found for this topic!", Toast.LENGTH_SHORT).show();
        }
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