package com.microsoft.todoapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.todoapp.R;
import com.microsoft.todoapp.database.DatabaseHelper;
import com.microsoft.todoapp.exceptions.InvalidValueException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mHelper;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileCenter.start(getApplication(), "4c94f3b6-6695-4ee3-b1b8-62898f185227",
                   Analytics.class, Crashes.class);
        
        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.new_task, null);
                final EditText taskEditText = (EditText) layout.findViewById(R.id.task);
                taskEditText.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.add_task_dialog_title)
                        .setView(layout)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveTask(String.valueOf(taskEditText.getText()));
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Call SDK for event
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        mHelper = new DatabaseHelper(this);
        ((ListView) findViewById(R.id.list_todo)).setAdapter(
                mAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.task_title));

        updateUI();
    }

    private void saveTask(String task) {
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task cannot be null or empty.");
        } else if (task.trim().isEmpty()) {
            throw new InvalidValueException("Task cannot be null or empty.");
        }
        mHelper.saveTask(task);
        updateUI();
    }

    public void deleteTask(View view) {
        TextView taskTextView = (TextView) ((View) view.getParent()).findViewById(R.id.task_title);
        mHelper.deleteTask(String.valueOf(taskTextView.getText()));
        updateUI();
    }

    private void updateUI() {
        ArrayList<String> taskList = mHelper.getTasks();
        mAdapter.clear();
        mAdapter.addAll(taskList);
        mAdapter.notifyDataSetChanged();
    }
}
