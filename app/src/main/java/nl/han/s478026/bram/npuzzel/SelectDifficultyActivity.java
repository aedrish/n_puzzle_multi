package nl.han.s478026.bram.npuzzel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;


public class SelectDifficultyActivity extends ActionBarActivity {
    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        Button b = (Button) findViewById(R.id.buttonFindOpponent);
        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");

        GeoFire geoFire = new GeoFire(new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/" + userName));
        geoFire.setLocation("firebase-hq", new GeoLocation(37.7853889, -122.4056973));

        b.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectDifficultyActivity.this, GameStartActivity.class);
                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
                RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
                final String difficulty = (String) radioButton.getTag();

                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_difficulty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
