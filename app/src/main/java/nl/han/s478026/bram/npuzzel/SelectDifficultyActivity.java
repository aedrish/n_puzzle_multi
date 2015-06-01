package nl.han.s478026.bram.npuzzel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import java.util.List;


public class SelectDifficultyActivity extends ActionBarActivity {
    private Firebase myFirebaseRef;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    private ProgressDialog progress;

    private static final int TIME_INTERVAL_FOR_LOCATION_UPDATE = 1000;
    private double radius = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);
        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        setLocation();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
//        if(wentToSettings){
//            Log.d("Selecting", "Setting Location");
            setLocation();

//            wentToSettings = false;

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

    private void setLocation() {
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("username");
        if(checkIfAnyLocationProviderIsActive()) {
            Criteria criteria = new Criteria();
            String locationProvider = locationManager.getBestProvider(criteria, true);
            Log.d("The best provider is ", "" + locationProvider);




            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                    // Called when a new location is found by the network location provider.
                    GeoFire geoFire = new GeoFire(new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/playersWaiting"));
                    geoFire.setLocation(userName, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    progress.dismiss();
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            locationManager.requestLocationUpdates(locationProvider, TIME_INTERVAL_FOR_LOCATION_UPDATE, 0, locationListener);

            createWaitingForLocationDialog();
        }else{
            showNoActiveProviderDialog();
        }
        handleStartGameButton(userName);
    }

    private void createWaitingForLocationDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle(R.string.no_location_available);
        progress.setMessage(getString(R.string.wait_for_location));
        progress.show();
    }


    private void showNoActiveProviderDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No Location Providers Active");
        alertDialog.setMessage(getString(R.string.no_active_location_providers));
        LinearLayout dv = new LinearLayout(this);
        dv.setOrientation(LinearLayout.VERTICAL);

        Button b1 = addDialogButton(alertDialog, getString(R.string.location_settings));
        dv.addView(b1);

        alertDialog.setView(dv);
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }



    private Button addDialogButton(final AlertDialog alertDialog, String text){
        Button b1 = new Button(this);
        b1.setText(text);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });

        return b1;
    }

    private void handleStartGameButton(final String userName) {
        Button b = (Button) findViewById(R.id.buttonFindOpponent);
        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
        final GeoFire geoFire = new GeoFire(new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/playersWaiting"));
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), radius);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        if (!userName.equals(key)) {
                            Intent intent = new Intent(SelectDifficultyActivity.this, GameStartActivity.class);
                            RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
                            RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
                            final String difficulty = (String) radioButton.getTag();

                            intent.putExtra("difficulty", difficulty);
                            intent.putExtra("enemy", key);
                            startActivity(intent);
                            finish();

                            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                        }
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

    private boolean checkIfAnyLocationProviderIsActive(){
        List<String> providers = locationManager.getProviders(true);
        Log.d("Listing providers", providers + "");

        if (providers.size() == 1){
            return false;
        }else{
            return true;
        }
    }
}
