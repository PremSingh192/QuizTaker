package com.prem.quiztaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.prem.quiztaker.other.Constants;

public class QuizOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_option);


        CardView cvGeography = findViewById(R.id.cvGeography);
        CardView cvLiterature = findViewById(R.id.cvLiterature);

        findViewById(R.id.imageViewQuizOption).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        cvGeography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizOptionActivity.this, GeographyOrLiteratureQuizActivity.class);
                intent.putExtra(Constants.SUBJECT,getString(R.string.geography));
                startActivity(intent);
            }
        });

        cvLiterature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizOptionActivity.this, GeographyOrLiteratureQuizActivity.class);
                intent.putExtra(Constants.SUBJECT,getString(R.string.literature));
                startActivity(intent);
            }
        });

    }
}