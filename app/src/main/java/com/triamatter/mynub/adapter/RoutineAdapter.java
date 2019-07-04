package com.triamatter.mynub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.triamatter.mynub.R;
import com.triamatter.mynub.model.admin.Routine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RoutineAdapter extends FirestoreRecyclerAdapter<Routine, RoutineAdapter.RoutineHolder> {

    Context mContext;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RoutineAdapter(@NonNull FirestoreRecyclerOptions<Routine> options, Context mContext)
    {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull RoutineHolder holder, int position, @NonNull Routine model)
    {
        try
        {
            holder.courseTitle.setText(model.getCourseTitle());

            if(model.getTimeStart() != null && model.getTimeEnd() != null)
            {
                Date noticeTimeStart = model.getTimeStart().toDate();
                Date noticeTimeEnd = model.getTimeEnd().toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                String timeStart = dateFormat.format(noticeTimeStart);
                String timeEnd = dateFormat.format(noticeTimeEnd);
                holder.timeTextView.setText(timeStart + " - " + timeEnd);
            }

            holder.roomNumTextView.setText("Room: "+ model.getRoomNum());
        }
        catch (Exception e)
        {

        }
    }

    @NonNull
    @Override
    public RoutineHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_routine, viewGroup, false);
        return new RoutineHolder(v);

    }

    class RoutineHolder extends RecyclerView.ViewHolder {

        TextView timeTextView;
        TextView courseTitle;
        TextView roomNumTextView;

        public RoutineHolder(@NonNull View itemView)
        {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.classTimeTextview);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            roomNumTextView = itemView.findViewById(R.id.roomTextView);
        }
    }
}
