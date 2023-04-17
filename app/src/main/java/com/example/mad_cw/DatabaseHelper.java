package com.example.mad_cw;
import android.util.Log;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.FileReader;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Your DatabaseHelper implementation
    private static final String DATABASE_NAME = "question_database";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the topics table
        String CREATE_TOPICS_TABLE = "CREATE TABLE topics (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";
        db.execSQL(CREATE_TOPICS_TABLE);

        // Create the questions table
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "topic_id INTEGER, " +
                "question_text TEXT NOT NULL, " +
                "answer_a TEXT NOT NULL, " +
                "answer_b TEXT NOT NULL, " +
                "answer_c TEXT NOT NULL, " +
                "correct_answer CHAR(1) NOT NULL, " +
                "difficulty INTEGER NOT NULL, " +
                "FOREIGN KEY(topic_id) REFERENCES topics(id))";
        db.execSQL(CREATE_QUESTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS topics");
        db.execSQL("DROP TABLE IF EXISTS questions");

        // Recreate the tables with the updated schema
        onCreate(db);
    }

    public long insertTopic(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);

        long topicId = db.insert("topics", null, values);
        db.close();
        return topicId;
    }

    public long insertQuestion(int topicId, String questionText, String answerA, String answerB, String answerC, String correctAnswer, int difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("topic_id", topicId);
        values.put("question_text", questionText);
        values.put("answer_a", answerA);
        values.put("answer_b", answerB);
        values.put("answer_c", answerC);
        values.put("correct_answer", correctAnswer);
        values.put("difficulty", difficulty);

        long questionId = db.insert("questions", null, values);
        db.close();
        return questionId;
    }

    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM topics", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                topics.add(new Topic(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return topics;
    }

    public List<Question> getQuestionsForTopic(int topicId) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM questions WHERE topic_id = ?", new String[]{String.valueOf(topicId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String questionText = cursor.getString(cursor.getColumnIndex("question_text"));
                String answerA = cursor.getString(cursor.getColumnIndex("answer_a"));
                String answerB = cursor.getString(cursor.getColumnIndex("answer_b"));
                String answerC = cursor.getString(cursor.getColumnIndex("answer_c"));
                String correctAnswer = cursor.getString(cursor.getColumnIndex("correct_answer"));
                int difficulty = cursor.getInt(cursor.getColumnIndex("difficulty"));

                questions.add(new Question(id, questionText, answerA, answerB, answerC, correctAnswer, difficulty));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return questions;
    }

    public Topic getTopicById(int topicId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM topics WHERE id = ?", new String[]{String.valueOf(topicId)});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));

            cursor.close();
            db.close();

            return new Topic(id, name);
        }

        cursor.close();
        db.close();
        return null;
    }
//    public void importQuestionsFromCSV(AssetManager assetManager, String fileName) {
//        try {
//            InputStream inputStream = assetManager.open(fileName);
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(inputStreamReader);
//
//            for (CSVRecord record : csvParser) {
//                int topicId = Integer.parseInt(record.get("topic_id"));
//                String questionText = record.get("question_text");
//                String answerA = record.get("answer_a");
//                String answerB = record.get("answer_b");
//                String answerC = record.get("answer_c");
//                String correctAnswer = record.get("correct_answer");
//                int difficulty = Integer.parseInt(record.get("difficulty"));
//
//                insertQuestion(topicId, questionText, answerA, answerB, answerC, correctAnswer, difficulty);
//            }
//
//            inputStreamReader.close();
//            csvParser.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void importQuestionsFromCSV(Context context, Uri uri) {
        Log.d("UPLOAD_ACTIVITY", "importQuestionsFromCSV() method called");
        try {
            // Open an InputStream using the Uri
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                // Create an InputStreamReader to read the InputStream
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Create a CSVParser to parse the contents of the InputStream
                CSVParser csvParser = CSVFormat.DEFAULT.withHeader().withQuoteMode(QuoteMode.ALL).parse(inputStreamReader);

                // Iterate through each CSVRecord and insert questions into the database
                for (CSVRecord record : csvParser) {
                    int topicId = Integer.parseInt(record.get("topic_id"));
                    String questionText = record.get("question_text");
                    String answerA = record.get("answer_a");
                    String answerB = record.get("answer_b");
                    String answerC = record.get("answer_c");
                    String correctAnswer = record.get("correct_answer");
                    int difficulty = Integer.parseInt(record.get("difficulty"));

                    insertQuestion(topicId, questionText, answerA, answerB, answerC, correctAnswer, difficulty);
                    Log.d("DatabaseHelper", "Inserted question: " + questionText);
                }

                // Close the InputStreamReader and CSVParser
                inputStreamReader.close();
                csvParser.close();

                Log.d("DatabaseHelper", "CSV import successful");
            } else {
                Log.e("DatabaseHelper", "Failed to open InputStream");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DatabaseHelper", "CSV import failed", e);
        }
    }


      // without csv parser
//    public void insertQuestionsFromCsv(InputStream inputStream) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            String[] fields = line.split(",");
//            if (fields.length == 7) {
//                int topicId = Integer.parseInt(fields[0].trim());
//                String questionText = fields[1].trim();
//                String answerA = fields[2].trim();
//                String answerB = fields[3].trim();
//                String answerC = fields[4].trim();
//                String correctAnswer = fields[5].trim();
//                int difficulty = Integer.parseInt(fields[6].trim());
//                insertQuestion(topicId, questionText, answerA, answerB, answerC, correctAnswer, difficulty);
//            } else {
//                throw new IOException("Invalid CSV format");
//            }
//        }
//    }

    // Parse questions from CSV file and insert them into the database
//    private boolean parseQuestionsFromCSV(Topic topic) {
//        InputStream inputStream = getResources().openRawResource(R.raw.questions);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//        boolean success = false;
//        try {
//            while ((line = reader.readLine()) != null) {
//                String[] tokens = line.split(",");
//                if (tokens.length == 7) {
//                    int topicId = topic.getId();
//                    String questionText = tokens[0].replaceAll("\"", "");
//                    String answerA = tokens[1].replaceAll("\"", "");
//                    String answerB = tokens[2].replaceAll("\"", "");
//                    String answerC = tokens[3].replaceAll("\"", "");
//                    String correctAnswer = tokens[4].replaceAll("\"", "");
//                    int difficulty = Integer.parseInt(tokens[5].replaceAll("\"", ""));
//                    this.insertQuestion(topicId, questionText, answerA, answerB, answerC, correctAnswer, difficulty);
//                    success = true;
//                } else {
//                    Toast.makeText(this, "Invalid CSV file format", Toast.LENGTH_SHORT).show();
//                    success = false;
//                    break;
//                }
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return success;
//    }

}
