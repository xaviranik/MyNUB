package com.triamatter.mynub.fragment.admin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.triamatter.mynub.R;

public class AddNoteDialog extends AppCompatDialogFragment {

    EditText editTextCourseTitle;
    EditText editTextCourseUrl;
    Button createNoteBtn;

    AddNoteDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_note_layout, null);

        builder.setView(view);

        editTextCourseTitle = (EditText) view.findViewById(R.id.edit_course_title);
        editTextCourseUrl = (EditText) view.findViewById(R.id.edit_course_url);
        createNoteBtn = (Button) view.findViewById(R.id.create_note_btn);

        createNoteBtn.setEnabled(true);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createNoteBtn.setEnabled(false);
                String noteTitle = editTextCourseTitle.getText().toString();
                String noteUrl = editTextCourseUrl.getText().toString();
                //Validation
                if(noteTitle.isEmpty())
                {
                    editTextCourseTitle.setError("Note title can't be empty!");
                    editTextCourseTitle.requestFocus();
                    createNoteBtn.setEnabled(true);
                    return;
                }

                if(noteUrl.isEmpty())
                {
                    editTextCourseUrl.setError("Note URL can't be empty!");
                    editTextCourseUrl.requestFocus();
                    createNoteBtn.setEnabled(true);
                    return;
                }

                if(!noteUrl.startsWith("https://drive.google.com/"))
                {
                    editTextCourseUrl.setError("Only google drive is supported. Make sure your link starts with the prefix: https://drive.google.com/");
                    editTextCourseUrl.requestFocus();
                    createNoteBtn.setEnabled(true);
                    return;
                }
                listener.addNote(noteTitle, noteUrl, dialog);
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (AddNoteDialogListener) context;
        }
        catch (Exception e)
        {
            throw new ClassCastException(context.toString() + "must implement AddNoteDialog");
        }
    }

    public interface AddNoteDialogListener {
        void addNote(String noteTitle, String noteUrl, AlertDialog dialog);
    }
}
