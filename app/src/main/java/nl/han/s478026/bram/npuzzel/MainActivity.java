package nl.han.s478026.bram.npuzzel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


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
            Intent intent = new Intent(MainActivity.this, SelectDifficultyActivity.class);
            startActivity(intent);
            finish();
        } else {
            Button b = (Button) findViewById(R.id.start_screen_save);

            myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
            username = (TextView) findViewById(R.id.start_screen_username_input);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Firebase userRefs = myFirebaseRef.child("users/" + username.getText().toString());
                    userRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                saveUserAndIntent(userRefs);
                            } else {
                                final AlertDialog al = new AlertDialog.Builder(MainActivity.this).create();
                                al.setTitle(getText(R.string.username_already_exists_title));
                                al.setMessage(getText(R.string.username_already_exists_message));
                                al.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.got_it),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                al.dismiss();
                                            }
                                        });
                                al.show();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
//                    saveUserAndIntent(userRefs);
                }
            });
        }
    }

    private void saveUserAndIntent(Firebase userRefs) {
        User user = new User(username.getText().toString());
        userRefs.setValue(user);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERNAME, username.getText().toString());
        editor.apply();
        Intent intent = new Intent(MainActivity.this, SelectDifficultyActivity.class);
        intent.putExtra("username", username.getText().toString());
        startActivity(intent);
        finish();
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
