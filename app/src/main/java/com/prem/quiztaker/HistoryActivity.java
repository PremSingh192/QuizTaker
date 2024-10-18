package com.prem.quiztaker;

import static com.prem.quiztaker.other.Utils.formatDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.prem.quiztaker.adapter.HistoryAdapter;
import com.prem.quiztaker.data.Attempt;
import com.prem.quiztaker.data.UserDatabase;
import com.prem.quiztaker.data.UserDatabaseClient;
import com.prem.quiztaker.data.UserWithAttempts;
import com.prem.quiztaker.other.SharedPref;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private List<UserWithAttempts> userWithAttempts;
    private TextView tvTotalPoints, tvTotalQuestions;
    ArrayList<Attempt> attempts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestionsHistory);
        tvTotalPoints = findViewById(R.id.tvOverAllPointsHistory);

        findViewById(R.id.imageViewHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        String email = SharedPref.getInstance().getUser(this).getEmail();
        GetAllUserAttemptTask getAllUserAttemptTask = new GetAllUserAttemptTask(email);

        getAllUserAttemptTask.execute();


    }




    // Method to share the score and attempt details using an Intent
    private void shareScore() {
        // Convert the attempts list to a readable string format
        StringBuilder attemptsDetails = new StringBuilder();
        for (Attempt attempt : attempts) {
            attemptsDetails.append("Quiz: ").append(attempt.getSubject())
                    .append(" | Score: ").append(attempt.getEarned())
                    .append(" | Date: ").append(formatDate(attempt.getCreatedTime()))
                    .append("\n");
        }

        String message = "Here are my attempts:\n" + attemptsDetails.toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quiz Score");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        // Show the share chooser dialog
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    class GetAllUserAttemptTask extends AsyncTask<Void, Void, Void> {

        private final String email;


        public GetAllUserAttemptTask(String email) {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UserDatabase databaseClient = UserDatabaseClient.getInstance(getApplicationContext());
            attempts = (ArrayList<Attempt>) databaseClient.userDao().getUserAndAttemptsWithSameEmail(email);
            return null;
        }




        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int overallPoints = 0;

            for (Attempt userWithAttempts : attempts) {
                overallPoints += userWithAttempts.getEarned();
            }



            tvTotalQuestions.setText(String.valueOf(attempts.size()));
            tvTotalPoints.setText(String.valueOf(overallPoints));

            Collections.sort(attempts, new AttemptCreatedTimeComparator());

            HistoryAdapter adapter = new HistoryAdapter(attempts);
            rvHistory.setAdapter(adapter);
            findViewById(R.id.shareresult).setOnClickListener(view -> shareScore());

        }
    }

    public class AttemptCreatedTimeComparator implements Comparator<Attempt> {

        @Override
        public int compare(Attempt attempt, Attempt t1) {
            return String.valueOf(t1.getCreatedTime()).compareTo(String.valueOf(attempt.getCreatedTime()));
        }
    }


}