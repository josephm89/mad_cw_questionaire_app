package com.example.mad_cw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    // read the topics from the database and fill the list
    private void loadTopics() {
        List<Topic> topics = getTopicsFromDatabase(); // Implement this method to read topics from the SQLite database
        ArrayAdapter<Topic> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics);
        ListView topicsList = findViewById(R.id.topics_list);
        topicsList.setAdapter(adapter);
    }

    private void setupTopicsList() {
        ListView topicsList = findViewById(R.id.topics_list);
        topicsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic selectedTopic = (Topic) parent.getItemAtPosition(position);
                loadTopicQuestions(selectedTopic); // Implement this method to load the questions for the selected topic
            }
        });
    }

}