package nl.han.s478026.bram.npuzzel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import java.util.Observable;
import java.util.Observer;


public class SelectDifficultyActivity extends ActionBarActivity implements Observer {
    private Firebase myFirebaseRef;

    private LocationUpdater locationUpdater = null;
    private Location currentLocation = null;

    private static final String MyPREFERENCES = "npuzzel_file";
    private static final String USERNAME = "usernameKey";

    private double radius = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRemoteLocation();
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

    private void setRemoteLocation() {
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String userName = sharedpreferences.getString(USERNAME, null);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationUpdater = new LocationUpdater(locationManager, this);
        locationUpdater.addObserver(this);

        handleStartGameButton(userName);
    }

    private void handleStartGameButton(final String userName) {
        Button b = (Button) findViewById(R.id.buttonFindOpponent);
        final GeoFire geoFire = new GeoFire(new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/playersWaiting"));
        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users");

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final WaitingDialog waitingDialog = new WaitingDialog(SelectDifficultyActivity.this, getString(R.string.searching_opponent), getString(R.string.locating_opponent));

                geoFire.setLocation(userName, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()));
                final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), radius);

                final String difficulty = getDifficulty();
                final Intent intent = new Intent(SelectDifficultyActivity.this, GameStartActivity.class);
                intent.putExtra("difficulty", difficulty);

                myFirebaseRef.child(userName + "/difficulty").setValue(difficulty);

                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(final String key, GeoLocation location) {
                        Firebase enemyRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/" + key + "/difficulty");
                        enemyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String enemyDifficulty = (String) dataSnapshot.getValue();

                                if (!userName.equals(key) && enemyDifficulty.equals(difficulty)) {
                                    waitingDialog.dismiss();
                                    intent.putExtra("enemy", key);
                                    geoQuery.removeAllListeners();
                                    geoFire.removeLocation(userName);
                                    startActivity(intent);
                                    resetLocation();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }

                    @Override
                    public void onKeyExited(String key) {
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                    }

                    @Override
                    public void onGeoQueryReady() {
                    }

                    @Override
                    public void onGeoQueryError(FirebaseError error) {
                    }
                });
            }
        });
    }

    private String getDifficulty() {
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        return (String) radioButton.getTag();
    }

    private void resetLocation() {
        locationUpdater.deleteObservers();
        locationUpdater = null;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof Location) {
            currentLocation = (Location) o;
        } else if (o instanceof String) {
            if (o.toString().equals("no_location_provider")) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        }
    }
}