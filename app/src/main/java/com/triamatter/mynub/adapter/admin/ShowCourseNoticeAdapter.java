package com.triamatter.mynub.adapter.admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.triamatter.mynub.R;
import com.triamatter.mynub.adapter.CourseNoticeAdapter;
import com.triamatter.mynub.model.admin.CourseNotice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowCourseNoticeAdapter extends FirestoreRecyclerAdapter<CourseNotice, ShowCourseNoticeAdapter.ShowCourseNoticeHolder> {

    Context mContext;
    String notice;
    String courseTitle;

    List<String> noticeList = new ArrayList<String>();
    List<String> courseTitleList = new ArrayList<String>();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ShowCourseNoticeAdapter(@NonNull FirestoreRecyclerOptions<CourseNotice> options, Context mContext)
    {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowCourseNoticeAdapter.ShowCourseNoticeHolder holder, int position, @NonNull CourseNotice model)
    {
        holder.textViewNotice.setText(model.getNotice());
        holder.textViewCourseTitle.setText(model.getCourseTitle());

        Date noticeDate = model.getTimestamp().toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String date = dateFormat.format(noticeDate);
        holder.textViewDate.setText(date);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                Toast.makeText(mContext, "Pressed: " + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ShowCourseNoticeAdapter.ShowCourseNoticeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_admin_course_notice, viewGroup, false);
        return new ShowCourseNoticeHolder(v);
    }

    class ShowCourseNoticeHolder extends RecyclerView.ViewHolder {

        TextView textViewNotice;
        TextView textViewCourseTitle;
        TextView textViewDate;
        ConstraintLayout parentLayout;

        public ShowCourseNoticeHolder(@NonNull final View itemView) {
            super(itemView);

            textViewNotice = itemView.findViewById(R.id.notice);
            textViewDate = itemView.findViewById(R.id.dateText);
            textViewCourseTitle = itemView.findViewById(R.id.course_title);

            parentLayout = itemView.findViewById(R.id.parentCourseNotice);
        }
    }
}
