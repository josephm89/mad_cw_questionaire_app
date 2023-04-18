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
    private static final int UPLOAD_REQUEST_CODE = 1;

//    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK) {
//                    Intent data = result.getData();
//                    if (data != null && data.hasExtra("selected_topic_id")) {
//                        long selectedTopicId = data.getLongExtra("selected_topic_id", -1);
//                        updateQuestions(selectedTopicId);
//                    }
//                }
//            });
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


        answerAButton.setOnClickListener(v -> setRandomQuestion());
        answerBButton.setOnClickListener(v -> setRandomQuestion());
        answerCButton.setOnClickListener(v -> setRandomQuestion());
//        Intent intent = getIntent();
//        if (intent.hasExtra("selected_topic_id")) {
//            long selectedTopicId = intent.getLongExtra("selected_topic_id", -1);
//
//            if (selectedTopicId != -1) {
//                questions = dbHelper.getQuestionsForTopic(selectedTopicId);
//                if (!questions.isEmpty()) {
//                    setRandomQuestion();
//                }
//            }
//        }

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





//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == UPLOAD_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (data != null && data.hasExtra("selected_topic_id")) {
//                long selectedTopicId = data.getLongExtra("selected_topic_id", -1);
//                updateQuestions(selectedTopicId);
//            }
//        }
//    }







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
        Question question = questions.get(randomIndex);

        questionTextView.setText(question.getQuestionText());
        answerAButton.setText(question.getAnswerA());
        answerBButton.setText(question.getAnswerB());
        answerCButton.setText(question.getAnswerC());
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

//    private List<Question> getQuestionsFromIntent() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            return (List<Question>) bundle.getSerializable("questions");
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    private void loadTopicQuestions(Topic selectedTopic) {
//        List<Question> questions = getQuestionsFromIntent(); // Update this line to use the new method
//        Intent intent = new Intent(this, MainActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("questions", (Serializable) questions);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//
//    // show the questions and answer options
//    private void displayQuestion(Question question) {
//        TextView questionText = findViewById(R.id.question_text);
//        Button answerButtonA = findViewById(R.id.answer_button_a);
//        Button answerButtonB = findViewById(R.id.answer_button_b);
//        Button answerButtonC = findViewById(R.id.answer_button_c);
//
//        questionText.setText(question.getQuestionText());
//        answerButtonA.setText(question.getAnswerA());
//        answerButtonB.setText(question.getAnswerB());
//        answerButtonC.setText(question.getAnswerC());
//    }
//
//    private void setupAnswerButtons() {
//        // ... (same as before)
//    }
//
//    private void checkAnswer(String selectedAnswer) {
//        // ... (same as before)
//    }
//
//    private Question getCurrentQuestion() {
//        if (questions != null && currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
//            return questions.get(currentQuestionIndex);
//        }
//        return null;
//    }
}