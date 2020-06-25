package com.example.wordmemorization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//단어장 액티비티
public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
    }
    @Override
    public void onBackPressed(){
        Intent intent= new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }
}
