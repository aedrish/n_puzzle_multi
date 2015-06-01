package nl.han.s478026.bram.npuzzel;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

import java.util.List;


public class SelectDifficultyActivity extends ActionBarActivity {
    private Firebase myFirebaseRef;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final int TIME_INTERVAL_FOR_LOCATION_UPDATE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);
        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setLocationAndButton();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
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

    private void setLocationAndButton() {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String locationProvider = locationManager.getBestProvider(criteria,true);
        Log.d("The best provider is ", "" + locationProvider);
        Intent intent = getIntent();

        final String userName = intent.getStringExtra("username");



        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                GeoFire geoFire = new GeoFire(new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/" + userName));

                if (location == null) {
                    Toast.makeText(SelectDifficultyActivity.this, "failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectDifficultyActivity.this, "Setting location: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
//                    geoFire.setLocation("firebase-hq", new GeoLocation(location.getLatitude(), location.getLongitude()));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(locationProvider, TIME_INTERVAL_FOR_LOCATION_UPDATE, 0 , locationListener);

        Button b = (Button) findViewById(R.id.buttonFindOpponent);
//        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectDifficultyActivity.this, GameStartActivity.class);
                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
                RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
                final String difficulty = (String) radioButton.getTag();

                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
                finish();
            }
        });
    }
}
