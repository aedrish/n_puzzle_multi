package nl.han.s478026.bram.npuzzel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends ActionBarActivity {

    public static final String MyPREFERENCES = "npuzzel_file";
    public static final String USERNAME = "usernameKey";
    private SharedPreferences sharedpreferences;
    private Firebase myFirebaseRef;
    TextView username ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = (TextView) findViewById(R.id.username_input);
        if (sharedpreferences.contains(USERNAME))
        {
            username.setText(sharedpreferences.getString(USERNAME, "Default"));
        } else {
            username.setText("Default");
        }

        Button b = (Button) findViewById(R.id.save_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedpreferences.getString(USERNAME, "Default") != "Default") {
                    Firebase childRef = myFirebaseRef.child("users/" + sharedpreferences.getString(USERNAME, "Default"));
                    childRef.child("name").setValue(username.getText().toString());
                    Toast.makeText(SettingsActivity.this, "not  default", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "default", Toast.LENGTH_LONG).show();
                    Firebase userRefs = myFirebaseRef.child("users");
                    Map<String, User> users = new HashMap<String, User>();
                    User user = new User(username.getText().toString());
                    users.put(username.getText().toString(), user);
                    userRefs.setValue(users);
                }

                Editor editor = sharedpreferences.edit();
                editor.putString(USERNAME, username.getText().toString());
                editor.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
