package com.example.mad_cw;
import android.util.Log;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import java.io.IOException;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {
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
    // reset database for testing purposes
    public void dropAllTables() { // for testing purposes
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS topics");
        db.execSQL("DROP TABLE IF EXISTS questions");
        onCreate(db);
        db.close();
    }

    // populate the Topics table
    public long insertTopic(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        long topicId = db.insert("topics", null, values);
        db.close();
        return topicId;
    }
    // populate the questions table
    public long insertQuestion(long topicId, String questionText, String answerA, String answerB, String answerC, String correctAnswer, int difficulty) {
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
    // for the upload screen's listview
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
    // after the selected topic is sent to the mainactivity, gets the questions from it
    public List<Question> getQuestionsForTopic(long topicId) {
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
    // to handle the asyncness of the csv parser from the other activity
    public interface ImportCallback {
        void onImportSuccess();
    }
    // is called from main after the file is selected, get a callback so it's run after csv parse is done
    public void importQuestionsFromCSV(Context context, long topicId, Uri uri, ImportCallback callback) {
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

                    String questionText = record.get("question_text");
                    String answerA = record.get("answer_a");
                    String answerB = record.get("answer_b");
                    String answerC = record.get("answer_c");
                    String correctAnswer = record.get("correct_answer");
                    int difficulty = Integer.parseInt(record.get("difficulty"));

                    insertQuestion(topicId, questionText, answerA, answerB, answerC, correctAnswer, difficulty);

                }
                // Close the InputStreamReader and CSVParser
                inputStreamReader.close();
                csvParser.close();
                if (callback != null) {
                    callback.onImportSuccess();
                }
                Log.d("DatabaseHelper", "CSV import successful");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
