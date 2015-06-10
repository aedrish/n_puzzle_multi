package nl.han.s478026.bram.npuzzel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nl.han.s478026.bram.npuzzel.R.string.enemy_points;
import static nl.han.s478026.bram.npuzzel.R.string.your_points;


/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */
public class YouWinActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_win);
        Firebase.setAndroidContext(this);

        TextView yourScoreTextView = (TextView) findViewById(R.id.your_score);
        TextView enemyScoreTextView = (TextView) findViewById(R.id.enemy_score);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("yourUserName");
        int resourceId = intent.getIntExtra("resourceId", 0);
        int yourScore = intent.getIntExtra("yourScore", 0);
        int enemyScore = (int) intent.getLongExtra("enemyScore", 0);
        String enemyUserName = intent.getStringExtra("enemyUser");

        Firebase myFirebaseRef =  new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/" + userName);
        Firebase match = myFirebaseRef.child("/history/" + enemyUserName + "/" + UUID.randomUUID().toString());
        match.child("yourScore").setValue(yourScore);
        match.child("opponentScore").setValue(enemyScore);
        match.child("resourceID").setValue(resourceId);

        TextView tvwl = (TextView) findViewById(R.id.win_lose_text);
        match.child("didWon").setValue(setWinLoseText(yourScore, enemyScore, tvwl));

        yourScoreTextView.setText(getResources().getText(your_points) + "" + yourScore);
        enemyScoreTextView.setText(getResources().getText(enemy_points) + "" + enemyScore);
        ImageView iv = (ImageView) findViewById(R.id.imageViewResult);

        iv.setImageResource(resourceId);
        AlphaAnimation animation1 = new AlphaAnimation(0.f, 1.0f);
        animation1.setDuration(5000);
        animation1.setStartOffset(0);
        animation1.setFillAfter(true);
        iv.startAnimation(animation1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean setWinLoseText(int yourScore, int enemyScore, TextView tvwl) {
        if(yourScore > enemyScore) {
            tvwl.setText(getString(R.string.you_won));
            return true;
        } else {
            tvwl.setText(getString(R.string.you_lost));
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_you_win, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home || id == R.id.new_puzzle) {
            Intent intent = new Intent(YouWinActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
