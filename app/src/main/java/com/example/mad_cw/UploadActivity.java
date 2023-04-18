package com.example.mad_cw;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class UploadActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<Topic> topics;
    private ListView topicsListView;
    private ArrayAdapter<String> topicAdapter;
    private EditText newTopicNameEditText;
    private Button addNewTopicButton;

    private int currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        dbHelper = new DatabaseHelper(this);

        //currentTopicId = 1; // hardcoded

        topicsListView = findViewById(R.id.topics_list);

        topics = dbHelper.getAllTopics();

        List<String> topicNames = new ArrayList<>();
        for (Topic topic : topics) {
            topicNames.add(topic.getName());
        }

        topicAdapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_list_item_1, topicNames);
        topicsListView.setAdapter(topicAdapter);



        loadTopics();
        setupTopicsList();

        Button uploadFileButton = findViewById(R.id.upload_file_button);
        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });

        //resetbutton
        Button resetDataButton = findViewById(R.id.reset_data_button);
        // Set the OnClickListener for the reset data button
        resetDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(UploadActivity.this);
                databaseHelper.dropAllTables();
                Toast.makeText(UploadActivity.this, "Data reset successfully!", Toast.LENGTH_SHORT).show();
            }
        });



        newTopicNameEditText = findViewById(R.id.new_topic_name);
        addNewTopicButton = findViewById(R.id.add_new_topic_button);

        addNewTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = newTopicNameEditText.getText().toString();
                if (!topicName.isEmpty()) {
                    dbHelper.insertTopic(topicName);
                    Toast.makeText(UploadActivity.this, "New topic added: " + topicName, Toast.LENGTH_SHORT).show();
                    newTopicNameEditText.setText(""); // Clear the EditText
                } else {
                    Toast.makeText(UploadActivity.this, "Please enter a topic name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ////no need?
//        //List<Question> questions = dbHelper.getQuestionsForTopic(currentTopicId);
//        dbHelper.logAllTopics();
//        //questionAdapter = new QuestionAdapter(this, questions);
//        listView = findViewById(R.id.topics_list);
//        listView.setAdapter(questionAdapter);
//        refreshListView();









    }
    void refreshListView() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        topics = databaseHelper.getAllTopics();

        // Extract topic names from the list of Topic objects
        List<String> topicNames = new ArrayList<>();
        for (Topic topic : topics) {
            topicNames.add(topic.getName());
        }

        topicAdapter.clear();
        topicAdapter.addAll(topicNames);
        topicAdapter.notifyDataSetChanged();
    }
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            dbHelper.importQuestionsFromCSV(UploadActivity.this, this.currentTopicId , uri, () -> refreshListView());
                        }
                    }
                }
            });

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"text/csv", "text/plain", "text/comma-separated-values"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        resultLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(UploadActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTopics() {
        // Call the getTopicsFromDatabase method to fetch the topics from the database
        List<Topic> topics = getTopicsFromDatabase();
        Log.d("UploadActivity", "Topics count: " + topics.size());
        // Create an ArrayAdapter to display the topics in the ListView
        ArrayAdapter<Topic> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics);

        // Find the ListView in the layout and set its adapter
        ListView topicsList = findViewById(R.id.topics_list);
        topicsList.setAdapter(adapter);
    }


    private List<Topic> getTopicsFromDatabase() {
        return dbHelper.getAllTopics();
    }

    private void setupTopicsList() {
        // Find the ListView in the layout
        ListView topicsList = findViewById(R.id.topics_list);

        // Set an OnItemClickListener for the ListView
        topicsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected Topic from the ListView
                Topic selectedTopic = (Topic) parent.getItemAtPosition(position);

                // Load the questions for the selected topic
                loadTopicQuestions(selectedTopic);
            }
        });
    }

    // Method to load the questions for a selected topic
    private void loadTopicQuestions(Topic selectedTopic) {
        // Fetch the questions for the selected topic from the database
        List<Question> questions = dbHelper.getQuestionsForTopic(selectedTopic.getId());

        // Create a new intent to start the MainActivity with the selected questions
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("questions", (Serializable) questions);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}