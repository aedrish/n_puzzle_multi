package nl.han.s478026.bram.npuzzel;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;


public class SelectDifficultyActivity extends ActionBarActivity implements Observer {
    private LocationUpdater locationUpdater = null;
    private Location currentLocation = null;
    private boolean hasActiveProvider = false;

    private static final String MyPREFERENCES = "npuzzel_file";
    private static final String USERNAME = "usernameKey";
    private RadioGroup radiogroup;
    private String userName;

    private HistoryFragment historyFragment = new HistoryFragment();
    private WaitingDialog noLocationDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        fillHistoryList();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction  fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.history_frame, historyFragment);
        fragmentTransaction.commit();
    }

    //Maybe write a Handler class for the fragment? This class is rather long
    private void fillHistoryList() {
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userName = sharedpreferences.getString(USERNAME, null);

        final ListView historyList = (ListView) findViewById(R.id.history_list);
        final ArrayList<String> nameList = new ArrayList<>();

        final String url = "https://n-puzzle-bram-daniel.firebaseio.com/users/"+userName+"/history/";
        final Firebase ref = new Firebase(url);
        // Attach an listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    nameList.add(child.getKey());
                }
                historyList.setAdapter(new ArrayAdapter<>(SelectDifficultyActivity.this, android.R.layout.simple_list_item_1, nameList));
                historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String key = nameList.get(position);
                        String urlOfOpponent = url.concat(key);
                        Log.d("The url to use is ", "" + urlOfOpponent);
                        getChildrenListForFragment(key, ref, urlOfOpponent);
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        
        radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
        for (Difficulty item: Difficulty.values()) {
            RadioButton rb = new RadioButton(SelectDifficultyActivity.this);
            rb.setText(String.valueOf(getText(item.getDifficulty())));
            radiogroup.addView(rb);
        }
    }

    private void getChildrenListForFragment(String key, Firebase ref, final String url) {
        ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FragmentTransaction tr = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                historyFragment = new HistoryFragment();
                historyFragment.setArguments(bundle);
                tr.replace(R.id.history_frame, historyFragment);
                tr.commit();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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

    private void setRemoteLocation() {
        locationUpdater = new LocationUpdater(this);
        locationUpdater.addObserver(this);

        handleStartGameButton();
    }

    private void handleStartGameButton() {
        Button b = (Button) findViewById(R.id.buttonFindOpponent);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!hasActiveProvider) {
                    locationUpdater.checkIfAnyLocationProviderIsActive();
                } else if (currentLocation != null) {
                    executeOpponentSearch();
                } else {
                    noLocationDialog = new WaitingDialog(SelectDifficultyActivity.this, getString(R.string.no_location_available), getString(R.string.wait_for_location));
                }
            }
        });
    }

    private void executeOpponentSearch() {
        //TO-DO: Remove date from firebase, dismiss waitingdialog here
        final String difficulty = getDifficulty();
        final Intent intent = new Intent(SelectDifficultyActivity.this, GameStartActivity.class);
        intent.putExtra("difficulty", difficulty);

        Firebase myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users");
        myFirebaseRef.child(userName + "/difficulty").setValue(difficulty);

        handleGeoQuery(intent, difficulty);
    }

    private void handleGeoQuery(final Intent intent, final String difficulty) {
        Log.d("Size of OpponentList", "Handling Query");
        final WaitingDialog waitingDialog = new WaitingDialog(SelectDifficultyActivity.this, getString(R.string.searching_opponent), getString(R.string.locating_opponent));
        final GeoFire geoFire = new GeoFire(new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/playersWaiting"));
        final GeoQuery geoQuery = getGeoQuery(geoFire);
        final ArrayList<String> opponentList = new ArrayList<>();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                if (!userName.equals(key)) {
                    Firebase enemyRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/" + key + "/difficulty");
                    enemyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String enemyDifficulty = (String) dataSnapshot.getValue();
                            if (enemyDifficulty.equals(difficulty)) {
                                if (opponentList.size() == 0) {
                                    opponentList.add(key);
                                    intent.putExtra("enemy", key);
                                    geoQuery.removeAllListeners();
                                    geoFire.removeLocation(userName);
                                    try {
                                        needToPickImage(intent, key, waitingDialog);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
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

    private void needToPickImage(final Intent intent, String opponentName, final WaitingDialog waitingDialog) throws ParseException {
        Firebase firebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

        final String selfDateString = sdf.format(new Date());
        final Date selfDate = sdf.parse(selfDateString);
        firebaseRef.child(userName+"/time_of_search").setValue(selfDateString);

        firebaseRef.child(opponentName+"/time_of_search").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Data of oopponent", "added");
                String opponentDateString = dataSnapshot.getValue().toString();
                Date opponentDate = null;
                try {
                    opponentDate = sdf.parse(opponentDateString);
                    if (opponentDate.before(selfDate)) {
                        intent.putExtra("pickImage", "yes");
                    } else {
                        intent.putExtra("pickImage", "no");
                    }
                    waitingDialog.dismiss();
                    startActivity(intent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private GeoQuery getGeoQuery(GeoFire geoFire) {
        GeoLocation myGeoLocation = new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
        geoFire.setLocation(userName, myGeoLocation);
        Log.d("geoFire", "Radius : " + getRadius());
        return geoFire.queryAtLocation(myGeoLocation, getRadius());
    }

    private double getRadius() {
        EditText radiusText = (EditText) findViewById(R.id.radius);
        return (Double.parseDouble(radiusText.getText().toString())/1000);
        //Source
        //http://stackoverflow.com/questions/10436776/how-to-get-a-numerical-value-from-an-android-edittext
    }

    private String getDifficulty() {
        radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        return (String) radioButton.getText();
    }

    private void resetLocation() {
        locationUpdater.deleteObservers();
        locationUpdater = null;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof Location) {
            try {
                noLocationDialog.dismiss();
            }catch( Exception e){

            }
            currentLocation = (Location) o;
        } else if (o instanceof String) {
            switch (o.toString()) {
                case "no_location_provider":
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    break;
                case "hasActiveProvider":
                    hasActiveProvider = true;
                    break;
                default:
                    break;
            }
        }
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