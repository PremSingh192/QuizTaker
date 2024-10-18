package com.prem.quiztaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.prem.quiztaker.data.User;
import com.prem.quiztaker.other.SharedPref;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvUsername = findViewById(R.id.tvUsernameHome);
        CardView cvStartQuiz = findViewById(R.id.cvStartQuiz);

        CardView cvHistory = findViewById(R.id.cvHistory);

        CardView cvLogout = findViewById(R.id.cvLogout);
        CardView cvTest = findViewById(R.id.cvTest);


        SharedPref sharedPref = SharedPref.getInstance();
        User user = sharedPref.getUser(this);
        tvUsername.setText("Hello, " + user.getUsername());

        cvStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,QuizOptionActivity.class));
            }
        });



        cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,HistoryActivity.class));
            }
        });


        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.clearSharedPref(MainActivity.this);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,TestActivity.class));
            }
        });

        



    }


}