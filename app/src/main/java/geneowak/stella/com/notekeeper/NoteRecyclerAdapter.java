package geneowak.stella.com.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Owak
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context eContext;
    private final List<NoteInfo> eNotes;
    private final LayoutInflater eLayoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        eContext = context;
        eNotes = notes;
        eLayoutInflater = LayoutInflater.from(eContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = eLayoutInflater.inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        NoteInfo note = eNotes.get(position);
        viewHolder.eTextCourse.setText(note.getCourse().getTitle());
        viewHolder.eTextTitle.setText(note.getTitle());
        viewHolder.eId = note.getId();
    }

    @Override
    public int getItemCount() {
        return eNotes.size();
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
