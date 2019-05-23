package geneowak.stella.com.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import static geneowak.stella.com.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();
    public static final String NOTE_ID = "geneowak.stella.com.notekeeper.NOTE_ID";
    public static final String ORIGINAL_NOTE_COURSE_ID = "geneowak.stella.com.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "geneowak.stella.com.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "geneowak.stella.com.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private final DataManager sDataManager = DataManager.getInstance();
    private NoteInfo eNote;
    private boolean eIsNewNote;
    private Spinner eSpinnerCourses;
    private EditText eTextNoteTitle;
    private EditText eTextNoteText;
    private int eNoteId;
    private boolean eIsCancelling;
    private String eOriginalCourseId;
    private String eOriginalNoteTitle;
    private String eOriginalNoteText;
    private NoteKeeperOpenHelper eDbOpenHelper;
    private Cursor eNoteCursor;
    private int eNoteTitlePos;
    private int eNoteTextPos;
    private int eCourseIdPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eDbOpenHelper = new NoteKeeperOpenHelper(this);

        eSpinnerCourses = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = sDataManager.getCourses();
        // create adapter to associate list with spinner
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        // associate the resource to be used for the dropdown courses
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // associate adapter with spinner
        eSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();

        eTextNoteTitle = findViewById(R.id.text_note_title);
        eTextNoteText = findViewById(R.id.text_note_text);

        if (!eIsNewNote)
            loadNoteData();

        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalNoteValues(savedInstanceState);

        }

        Log.d(TAG, "onCreate");
    }

    private void loadNoteData() {
        SQLiteDatabase db = eDbOpenHelper.getReadableDatabase();

        String courseId = "android_intents";
        String titleStart = "dynamic";

        String selection = NoteInfoEntry._ID + " = ? ";

        String[] selectionArgs = {Integer.toString(eNoteId)};

        String[] noteColumns = {
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT,
                NoteInfoEntry.COLUMN_COURSE_ID};

        eNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                selection, selectionArgs, null, null, null);

        eNoteTitlePos = eNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        eNoteTextPos = eNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        eCourseIdPos = eNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);

        eNoteCursor.moveToNext();

        displayNote();
    }

    @Override
    protected void onDestroy() {
        eDbOpenHelper.close();
        super.onDestroy();
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

    private void displayNote() {
        String courseId = eNoteCursor.getString(eCourseIdPos);
        String noteTitle = eNoteCursor.getString(eNoteTitlePos);
        String noteText = eNoteCursor.getString(eNoteTextPos);

        List<CourseInfo> courses = sDataManager.getCourses();
        CourseInfo course = sDataManager.getCourse(courseId);
        int courseIndex = courses.indexOf(course);
        eSpinnerCourses.setSelection(courseIndex);
        eTextNoteTitle.setText(noteTitle);
        eTextNoteText.setText(noteText);

        eNote = new NoteInfo(course, noteTitle, noteText);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        eNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        eIsNewNote = eNoteId == ID_NOT_SET;

        if (eIsNewNote) {
            createNewNote();
        }

//        eNote = sDataManager.getNotes().get(eNoteId);

        Log.i(TAG, "eNoteId: " + eNoteId);
    }

    private void createNewNote() {
        eNoteId = sDataManager.createNewNote();
//        eNote = dm.getNotes().get(eNoteId);
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
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int lastIndex = sDataManager.getNotes().size() - 1;

        menuItem.setEnabled(eNoteId < lastIndex);

        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        // first save any changes the user might have made
        saveNote();
        // increment note position
        ++eNoteId;
        eNote = sDataManager.getNotes().get(eNoteId);
        //first save the current state of the note incase the user cancels
        saveOriginalNoteValues();
        displayNote();
        // call onPrepareOptionsMenu to invalidate next menu item if last note has been reached
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eIsCancelling) {
            Log.i(TAG, "Cancelling eNote at position: " + eNoteId);
            if (eIsNewNote) {
                sDataManager.removeNote(eNoteId);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }

        Log.d(TAG, "onPause");
    }

    private void storePreviousNoteValues() {
        CourseInfo course = sDataManager.getCourse(eOriginalCourseId);
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
