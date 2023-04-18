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

    private long newTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        dbHelper = new DatabaseHelper(this);
        topics = dbHelper.getAllTopics();
        topicsListView = findViewById(R.id.topics_list);

        List<String> topicNames = new ArrayList<>();
        for (Topic topic : topics) {
            topicNames.add(topic.getName());
        }

        // make the listview and adapter
        topicAdapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_list_item_1, topicNames);
        topicsListView.setAdapter(topicAdapter);

        topicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic selectedTopic = topics.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_topic_id", selectedTopic.getId());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        //reset button
        Button resetDataButton = findViewById(R.id.reset_data_button);
        resetDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.dropAllTables();
                refreshListView();
                Toast.makeText(UploadActivity.this, "Data reset successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // add new topic button
        newTopicNameEditText = findViewById(R.id.new_topic_name);
        addNewTopicButton = findViewById(R.id.add_new_topic_button);

        addNewTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = newTopicNameEditText.getText().toString();
                if (!topicName.isEmpty()) {
                    newTopicId = dbHelper.insertTopic(topicName);
                    Toast.makeText(UploadActivity.this, "New topic added: " + topicName, Toast.LENGTH_SHORT).show();
                    newTopicNameEditText.setText("");// Clear the EditText

                    performFileSearch(); ////sends us to the file manager

                    //refreshListView(); search does it anyway
                } else {
                    Toast.makeText(UploadActivity.this, "Please enter a topic name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshListView();
    }
    ////////////////////////////On Create Ends//////////////////////////////////////////////////////

    ////////////////////////////// File Search /////////////////////////////////////////////////////
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {      //get csv, send the specified topic id from button, refresh callback
                            dbHelper.importQuestionsFromCSV(UploadActivity.this, this.newTopicId , uri, () -> refreshListView());
                        }
                    }
                }
            });

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");  //wouldn't take csv/txt
        String[] mimeTypes = {"text/csv", "text/plain", "text/comma-separated-values"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        resultLauncher.launch(intent);
    }
    /////////////////////////// File Search Ends  //////////////////////////////////////////////////

    void refreshListView() {
        topics = dbHelper.getAllTopics();
        List<String> topicNames = new ArrayList<>();
        for (Topic topic : topics) {
            topicNames.add(topic.getName());
        }
        topicAdapter.clear();
        topicAdapter.addAll(topicNames);
        topicAdapter.notifyDataSetChanged();
        Log.d("UploadActivity", " refresh list called Total Topics: " + topics.size() + ", Topic Names: " + topicNames);

    }

    //////////////////////////////////// Menu /////////////////////////////////////////////////
    // menu setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    // menu nav
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

}