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
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class UploadActivity extends AppCompatActivity {

    // Declare a DatabaseHelper field to interact with the database
    private DatabaseHelper dbHelper;
    private QuestionAdapter questionAdapter;
    private ListView listView;
    private int currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        dbHelper = new DatabaseHelper(this);


        List<Question> questions = dbHelper.getQuestionsForTopic(currentTopicId);
        questionAdapter = new QuestionAdapter(this, questions);
        listView = findViewById(R.id.topics_list);
        listView.setAdapter(questionAdapter);


        currentTopicId = 1; // hardcoded
        refreshListView();



        loadTopics();
        setupTopicsList();

        Button uploadFileButton = findViewById(R.id.upload_file_button);
        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }
    void refreshListView() {
        List<Question> questions = dbHelper.getQuestionsForTopic(currentTopicId);
        questionAdapter.clear();
        questionAdapter.addAll(questions);
        questionAdapter.notifyDataSetChanged();
    }
    // ActivityResultLauncher to handle the result of file search
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("UPLOAD_ACTIVITY", "in resultlauncher");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            Log.d("UploadActivity", "File Uri: " + uri.toString());
                            dbHelper.importQuestionsFromCSV(UploadActivity.this, uri, new DatabaseHelper.ImportCallback() {
                                @Override
                                public void onImportSuccess() {
                                    refreshListView();
                                }
                            });
                        }
                    }
                }
            });

    // Method to launch the file search and allow user to select a CSV file
    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"text/csv", "text/plain", "text/comma-separated-values"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        resultLauncher.launch(intent);
    }

    // Inflate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle menu item clicks
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

    // Method to read the topics from the database and fill the ListView with them
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

        // Create a new intent to start the MainActivity with the selected questions
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("questions", (Serializable) questions);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}