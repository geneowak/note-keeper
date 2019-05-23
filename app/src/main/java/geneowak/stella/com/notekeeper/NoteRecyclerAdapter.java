package geneowak.stella.com.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import geneowak.stella.com.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

/**
 * Created by Owak
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context eContext;
    private Cursor eCursor;
    private final LayoutInflater eLayoutInflater;
    private int eCoursePos;
    private int eNoteTitlePos;
    private int eIdPos;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        eContext = context;
        eCursor = cursor;
        eLayoutInflater = LayoutInflater.from(eContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (eCursor == null)
            return;
        // Get column indexes from eCursor
        eCoursePos = eCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        eNoteTitlePos = eCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        eIdPos = eCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        // first close previous cursor if it exists
        if (eCursor != null)
            eCursor.close();
        eCursor = cursor;
        // new cursor may not have our columns in the same order the the old cursor did
        populateColumnPositions();
        // notify recycler view that the data has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = eLayoutInflater.inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        eCursor.moveToPosition(position);

        String course = eCursor.getString(eCoursePos);
        String noteTitle = eCursor.getString(eNoteTitlePos);
        int id = eCursor.getInt(eIdPos);

        viewHolder.eTextCourse.setText(course);
        viewHolder.eTextTitle.setText(noteTitle);
        viewHolder.eId = id;
    }

    @Override
    public int getItemCount() {
        return eCursor == null ? 0 : eCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView eTextCourse;
        public final TextView eTextTitle;
        public int eId;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            eTextCourse = itemView.findViewById(R.id.text_course);
            eTextTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(eContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, eId);
                    eContext.startActivity(intent);
                }
            });
        }
    }
}
