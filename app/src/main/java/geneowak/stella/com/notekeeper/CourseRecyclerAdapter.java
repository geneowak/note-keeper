package geneowak.stella.com.notekeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Owak
 */
public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context eContext;
    private final List<CourseInfo> eCourses;
    private final LayoutInflater eLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        eContext = context;
        eCourses = courses;
        eLayoutInflater = LayoutInflater.from(eContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = eLayoutInflater.inflate(R.layout.item_course_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CourseInfo course = eCourses.get(position);
        viewHolder.eTextCourse.setText(course.getTitle());
        viewHolder.eCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return eCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView eTextCourse;
        public int eCurrentPosition;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            eTextCourse = itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, eCourses.get(eCurrentPosition).getTitle(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
