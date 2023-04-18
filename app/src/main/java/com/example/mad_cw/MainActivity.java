package com.example.mad_cw;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Random;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private List<Question> questions;
    private TextView questionTextView;
    private Button answerAButton;
    private Button answerBButton;
    private Button answerCButton;
    private Question currentQuestion;
    private String correctAnswer;

    private final ActivityResultLauncher<Intent> uploadActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("selected_topic_id")) {
                        long selectedTopicId = data.getLongExtra("selected_topic_id", -1);
                        updateQuestions(selectedTopicId);
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        questionTextView = findViewById(R.id.question_text);
        answerAButton = findViewById(R.id.answer_button_a);
        answerBButton = findViewById(R.id.answer_button_b);
        answerCButton = findViewById(R.id.answer_button_c);

//        answerAButton.setOnClickListener(v -> checkAnswerAndSetNewQuestion(currentQuestion.getAnswerA()));
//        answerBButton.setOnClickListener(v -> checkAnswerAndSetNewQuestion(currentQuestion.getAnswerB()));
//        answerCButton.setOnClickListener(v -> checkAnswerAndSetNewQuestion(currentQuestion.getAnswerC()));
        answerAButton.setOnClickListener(v -> checkAnswer("A"));
        answerBButton.setOnClickListener(v -> checkAnswer("B"));
        answerCButton.setOnClickListener(v -> checkAnswer("C"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent.hasExtra("selected_topic_id")) {
            long selectedTopicId = intent.getLongExtra("selected_topic_id", -1);
            updateQuestions(selectedTopicId);
        }
    }


    private void updateQuestions(long selectedTopicId) {
        if (selectedTopicId != -1) {
            questions = dbHelper.getQuestionsForTopic(selectedTopicId);
            if (!questions.isEmpty()) {
                setRandomQuestion();
            }
        }
    }

    private void setRandomQuestion() {
        Random random = new Random();
        int randomIndex = random.nextInt(questions.size());
        currentQuestion = questions.get(randomIndex);
        correctAnswer = currentQuestion.getCorrectAnswer();

        questionTextView.setText(currentQuestion.getQuestionText());
        answerAButton.setText(currentQuestion.getAnswerA());
        answerBButton.setText(currentQuestion.getAnswerB());
        answerCButton.setText(currentQuestion.getAnswerC());


    }

//    private void setRandomQuestion() {
//        Random random = new Random();
//        int randomIndex = random.nextInt(questions.size());
//        Question question = questions.get(randomIndex);
//
//        questionTextView.setText(question.getQuestionText());
//        answerAButton.setText(question.getAnswerA());
//        answerBButton.setText(question.getAnswerB());
//        answerCButton.setText(question.getAnswerC());
//    }


//    private void checkAnswerAndSetNewQuestion(String selectedAnswer) {
//        if (currentQuestion.getCorrectAnswer().equals(selectedAnswer)) {
//            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
//            setRandomQuestion();
//        } else {
//            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    private void checkAnswer(String selectedAnswer) {
        if (selectedAnswer.equals(correctAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            setRandomQuestion();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
    }












    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            uploadActivityResultLauncher.launch(new Intent(MainActivity.this, UploadActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}