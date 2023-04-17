package com.example.mad_cw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class UploadActivity extends AppCompatActivity {

    // Declare a DatabaseHelper field to interact with the database
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize the dbHelper field with a new instance of the DatabaseHelper class
        dbHelper = new DatabaseHelper(this);

        // Load the topics and display them in the ListView
        loadTopics();

        // Set up the click listener for the topics list
        setupTopicsList();
    }

    // Method to read the topics from the database and fill the ListView with them
    private void loadTopics() {
        // Call the getTopicsFromDatabase method to fetch the topics from the database
        List<Topic> topics = getTopicsFromDatabase();

        // Create an ArrayAdapter to display the topics in the ListView
        ArrayAdapter<Topic> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics);

        // Find the ListView in the layout and set its adapter
        ListView topicsList = findViewById(R.id.topics_list);
        topicsList.setAdapter(adapter);
    }

    // Method to fetch topics from the SQLite database using the dbHelper instance
    private List<Topic> getTopicsFromDatabase() {
        return dbHelper.getAllTopics();
    }

    // Method to set up the click listener for the topics ListView
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

        // TODO: Do something with the loaded questions, e.g., pass them to the MainActivity
    }
}