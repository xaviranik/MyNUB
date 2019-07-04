package com.triamatter.mynub.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.triamatter.mynub.core.ShowBookActivity;
import com.triamatter.mynub.core.ShowNoteActivity;
import com.triamatter.mynub.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends FirestoreRecyclerAdapter<Book, BookAdapter.BookHolder> {

    Context mContext;

    List<String> bookNameList = new ArrayList<String>();
    List<String> bookUrlList = new ArrayList<String>();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookAdapter(@NonNull FirestoreRecyclerOptions<Book> options, Context mContext)
    {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookAdapter.BookHolder holder, final int position, @NonNull Book model)
    {
        holder.textViewCourseCode.setText(model.getCourseCode());
        holder.textViewBookName.setText(model.getBookName());

        bookNameList.add(model.getBookName());
        bookUrlList.add(model.getBookUrl());

        holder.parentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showBook(v, position);
            }
        });
    }

    private void showBook(View view, int position)
    {
        String bookName = bookNameList.get(position);
        String bookUrl = bookUrlList.get(position);

        Intent intent = new Intent(mContext, ShowBookActivity.class);
        intent.putExtra("bookName", bookName);
        intent.putExtra("bookUrl", bookUrl);
        view.getContext().startActivity(intent);
    }

    @NonNull
    @Override
    public BookAdapter.BookHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_book, viewGroup, false);
        return new BookHolder(v);
    }

    class BookHolder extends RecyclerView.ViewHolder {

        TextView textViewCourseCode;
        TextView textViewBookName;
        CardView parentCard;

        public BookHolder(@NonNull View itemView)
        {
            super(itemView);

            textViewBookName = itemView.findViewById(R.id.tv_bookname);
            textViewCourseCode = itemView.findViewById(R.id.tv_courseCode);
            parentCard = itemView.findViewById(R.id.parentBookCard);
        }
    }
}
