package com.triamatter.mynub.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.triamatter.mynub.R;
import com.triamatter.mynub.core.ShowNoteActivity;
import com.triamatter.mynub.model.admin.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder> {

    Context mContext;
    String noteTitle;
    String noteUrl;

    List<String> noteTitleList = new ArrayList<String>();
    List<String> noteUrlList = new ArrayList<String>();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context mContext)
    {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, final int position, @NonNull Note model)
    {
        noteTitle = model.getNoteTitle();
        noteTitleList.add(noteTitle);

        noteUrl = model.getNoteUrl();
        noteUrlList.add(noteUrl);

        holder.noteNameTextview.setText(noteTitle);

        final Date noteDate = model.getTimestamp().toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String date = dateFormat.format(noteDate);
        holder.noteDateTextview.setText(date);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showNote(v, position);
            }
        });
    }

    private void showNote(View v, int position)
    {
        noteTitle = noteTitleList.get(position);
        noteUrl = noteUrlList.get(position);

        Intent intent = new Intent(mContext, ShowNoteActivity.class);
        intent.putExtra("noteTitle", noteTitle);
        intent.putExtra("noteUrl", noteUrl);
        v.getContext().startActivity(intent);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note, viewGroup, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder {

        TextView noteNameTextview;
        TextView noteDateTextview;
        CardView parentLayout;

        public NoteHolder(@NonNull View itemView)
        {
            super(itemView);
            noteNameTextview = itemView.findViewById(R.id.tv_noteName);
            noteDateTextview = itemView.findViewById(R.id.tv_noteDate);
            parentLayout = itemView.findViewById(R.id.noteCardview);
        }
    }
}
