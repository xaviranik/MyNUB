package com.triamatter.mynub.adapter;

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
import com.triamatter.mynub.model.Notice;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeAdapter extends FirestoreRecyclerAdapter<Notice, NoticeAdapter.NoticeHolder> {

    Context mContext;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoticeAdapter(@NonNull FirestoreRecyclerOptions<Notice> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoticeHolder holder, int position, @NonNull final Notice model) {
        holder.textViewNotice.setText(model.getNotice());

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
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notice, viewGroup, false);
        return new NoticeHolder(v);
    }

    class NoticeHolder extends RecyclerView.ViewHolder {
        TextView textViewNotice;
        TextView textViewDate;
        ConstraintLayout parentLayout;

        public NoticeHolder(@NonNull final View itemView) {
            super(itemView);

            textViewNotice = itemView.findViewById(R.id.notice);
            textViewDate = itemView.findViewById(R.id.dateText);

            parentLayout = itemView.findViewById(R.id.parentNotice);
        }
    }
}
