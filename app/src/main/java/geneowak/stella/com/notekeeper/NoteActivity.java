package geneowak.stella.com.notekeeper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "geneowak.stella.com.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "geneowak.stella.com.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "geneowak.stella.com.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "geneowak.stella.com.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo eNote;
    private boolean eIsNewNote;
    private Spinner eSpinnerCourses;
    private EditText eTextNoteTitle;
    private EditText eTextNoteText;
    private int eNotePosition;
    private boolean eIsCancelling;
    private String eOriginalCourseId;
    private String eOriginalNoteTitle;
    private String eOriginalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eSpinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        // create adapter to associate list with spinner
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        // associate the resource to be used for the dropdown courses
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // associate adapter with spinner
        eSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalNoteValues(savedInstanceState);

        }

        eTextNoteTitle = findViewById(R.id.text_note_title);
        eTextNoteText = findViewById(R.id.text_note_text);

        if (!eIsNewNote)
            displayNote(eSpinnerCourses, eTextNoteTitle, eTextNoteText);

        Log.d(TAG, "onCreate");
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        eOriginalCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        eOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        eOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if (eIsNewNote)
            return;
        eOriginalCourseId = eNote.getCourse().getCourseId();
        eOriginalNoteTitle = eNote.getTitle();
        eOriginalNoteText = eNote.getText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, eOriginalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, eOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, eOriginalNoteText);
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(eNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(eNote.getTitle());
        textNoteText.setText(eNote.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        eNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        eIsNewNote = eNotePosition == POSITION_NOT_SET;

        if (eIsNewNote) {
            createNewNote();
        }

        eNote = DataManager.getInstance().getNotes().get(eNotePosition);

        Log.i(TAG, "eNotePosition: " + eNotePosition);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        eNotePosition = dm.createNewNote();
//        eNote = dm.getNotes().get(eNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            eIsCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eIsCancelling) {
            Log.i(TAG, "Cancelling eNote at position: " + eNotePosition);
            if (eIsNewNote) {
                DataManager.getInstance().removeNote(eNotePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }

        Log.d(TAG, "onPause");
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(eOriginalCourseId);
        eNote.setCourse(course);
        eNote.setTitle(eOriginalNoteTitle);
        eNote.setText(eOriginalNoteText);
    }

    private void saveNote() {
        eNote.setCourse((CourseInfo) eSpinnerCourses.getSelectedItem());
        eNote.setTitle(eTextNoteTitle.getText().toString());
        eNote.setText(eTextNoteText.getText().toString());
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) eSpinnerCourses.getSelectedItem();
        String subject = eTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course\"" +
                course.getTitle() + "\" \n " + eTextNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setType("message/rfc2822");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        // only start activity if the system has apps that can handle it
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
}
