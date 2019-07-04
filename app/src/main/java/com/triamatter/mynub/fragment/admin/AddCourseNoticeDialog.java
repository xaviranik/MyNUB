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

import com.triamatter.mynub.R;

public class AddCourseNoticeDialog extends AppCompatDialogFragment {

    EditText editTextCourseTitle;
    EditText editTextCourseNotice;
    Button createCourseNoticeBtn;

    AddCourseNoticeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_course_notice_layout, null);

        builder.setView(view);

        editTextCourseTitle = (EditText) view.findViewById(R.id.edit_course_title);
        editTextCourseNotice = (EditText) view.findViewById(R.id.et_courseNotice);
        createCourseNoticeBtn = (Button) view.findViewById(R.id.create_notice_btn);

        createCourseNoticeBtn.setEnabled(true);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createCourseNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createCourseNoticeBtn.setEnabled(false);
                String notice = editTextCourseTitle.getText().toString();
                String courseTitle = editTextCourseNotice.getText().toString();
                //Validation
                if(notice.isEmpty())
                {
                    editTextCourseTitle.setError("Note title can't be empty!");
                    editTextCourseTitle.requestFocus();
                    createCourseNoticeBtn.setEnabled(true);
                    return;
                }

                if(courseTitle.isEmpty())
                {
                    editTextCourseNotice.setError("Note URL can't be empty!");
                    editTextCourseNotice.requestFocus();
                    createCourseNoticeBtn.setEnabled(true);
                    return;
                }

                listener.addNotice(notice, courseTitle, dialog);
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try {
            listener = (AddCourseNoticeDialog.AddCourseNoticeDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "must implement AddCourseNoticeDialog");
        }
    }

    public interface AddCourseNoticeDialogListener {
        void addNotice(String notice, String courseTitle, AlertDialog dialog);
    }
}
