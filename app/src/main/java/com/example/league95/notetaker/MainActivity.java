package com.example.league95.notetaker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An app that allows the user to add notes and delete notes.
 * In addition, it also saves notes using SharedPreferences.
 * Finally, the user is also allowed to delete a certain note
 * by simply long holding on a certain item on the list.
 */
public class MainActivity extends AppCompatActivity {

    ArrayList<String> notes;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    EditText editText;
    int index = 0;
    boolean menuPressed = false;
    SharedPreferences sharedPreferences;
    int counter = 0;

    @Override
    public void onBackPressed() {
        if (editText.getVisibility() == View.VISIBLE) {
            if (menuPressed) {
                if (editText.toString().length() > 0 && !editText.toString().equals("") && !editText.toString().isEmpty()) {
                    notes.add(editText.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                }
            } else {
                if (editText.toString().length() > 0 && !editText.toString().equals("") && !editText.toString().isEmpty()) {
                    notes.set(index, editText.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            sharedPreferences = this.getSharedPreferences("com.example.league95.notetaker", Context.MODE_PRIVATE);
            try {
                sharedPreferences.edit().putString("list", ObjectSerializer.serialize(notes)).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
            menuPressed = false;
            enableText(false);
            listView.setVisibility(View.VISIBLE);
        } else {
            counter++;
            Toast.makeText(this, "Tap again to exit!", Toast.LENGTH_SHORT).show();
            if (counter == 2)
            {
                counter = 0;
                finish();
            }

        }
    }

    /**
     * Add our menu here
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuPressed = true;
        if (item.getItemId() == R.id.addNote) {
            enableText(true);
            editText.setText("");
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        notes = new ArrayList<>();


        sharedPreferences = this.getSharedPreferences("com.example.league95.notetaker", Context.MODE_PRIVATE);
        ArrayList<String> savedList = new ArrayList<>();
        notes.clear();
        try {
            notes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("list", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //If we have nothing saved
        if (notes.size() < 1) {
            notes.add("Tap to set this note!");
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(arrayAdapter);

        //When we click on an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
                editText.setText(notes.get(i));
                enableText(true);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Note:")
                        .setMessage("Do you want delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notes.remove(index);
                                arrayAdapter.notifyDataSetChanged();
                                sharedPreferences = MainActivity.this.getSharedPreferences("com.example.league95.notetaker", Context.MODE_PRIVATE);
                                try {
                                    sharedPreferences.edit().putString("list", ObjectSerializer.serialize(notes)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    public void enableText(boolean bool) {
        int vis = bool ? View.VISIBLE : View.INVISIBLE;
        listView.setVisibility(View.INVISIBLE);
        editText.setVisibility(vis);
        editText.setClickable(bool);
        editText.setEnabled(bool);
    }
}
