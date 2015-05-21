package nl.han.s478026.bram.npuzzel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */
public class MainActivity extends ActionBarActivity {

    public static final String MyPREFERENCES = "npuzzel_file";
    public static final String USERNAME = "usernameKey";
    private Firebase myFirebaseRef;
    private SharedPreferences sharedpreferences;
    TextView username ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(USERNAME))
        {
            Intent intent = new Intent(MainActivity.this, GameStartActivity.class);
            startActivity(intent);
        } else {
            Button b = (Button) findViewById(R.id.start_screen_save);

            myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
            username = (TextView) findViewById(R.id.start_screen_username_input);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Firebase userRefs = myFirebaseRef.child("users");
                    User user = new User(username.getText().toString());
                    userRefs.child(username.getText().toString()).setValue(user);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(USERNAME, username.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, GameStartActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
