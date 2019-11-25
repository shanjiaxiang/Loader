package com.xx.loader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectActivity extends AppCompatActivity {

    private ListView listView;
    private String[] states;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getSupportActionBar().setTitle(R.string.select);
        listView = findViewById(R.id.select_list);
        states = this.getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, states);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SelectActivity.this, CollectActivity.class);
                intent.putExtra("type", states[i]);
                startActivity(intent);
                Toast.makeText(SelectActivity.this, states[i] + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}




















