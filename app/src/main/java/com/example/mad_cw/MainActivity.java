package com.example.mad_cw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Get the questions from sqlite
    private List<Question> getQuestionsFromDatabase() {
        // TODO: Implement the method to read questions from the SQLite database
        return new ArrayList<Question>();
    }

    // show the questions and answer options
    private void displayQuestion(Question question) {
        TextView questionText = findViewById(R.id.question_text);
        Button answerButtonA = findViewById(R.id.answer_button_a);
        Button answerButtonB = findViewById(R.id.answer_button_b);
        Button answerButtonC = findViewById(R.id.answer_button_c);

        questionText.setText(question.getQuestion());
        answerButtonA.setText(question.getAnswerA());
        answerButtonB.setText(question.getAnswerB());
        answerButtonC.setText(question.getAnswerC());
    }

    private void setupAnswerButtons() {
        Button answerButtonA = findViewById(R.id.answer_button_a);
        Button answerButtonB = findViewById(R.id.answer_button_b);
        Button answerButtonC = findViewById(R.id.answer_button_c);

        View.OnClickListener answerButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button clickedButton = (Button) view;
                String selectedAnswer = clickedButton.getText().toString();
                checkAnswer(selectedAnswer);
            }
        };

        answerButtonA.setOnClickListener(answerButtonClickListener);
        answerButtonB.setOnClickListener(answerButtonClickListener);
        answerButtonC.setOnClickListener(answerButtonClickListener);
    }

    private void checkAnswer(String selectedAnswer) {
        Question currentQuestion = getCurrentQuestion(); // Implement this method to get the current question
        String correctAnswer = currentQuestion.getCorrectAnswer();
        if (selectedAnswer.equals(correctAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }
    }
}