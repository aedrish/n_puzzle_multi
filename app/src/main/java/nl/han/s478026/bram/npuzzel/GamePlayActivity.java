package nl.han.s478026.bram.npuzzel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */

public class GamePlayActivity extends ActionBarActivity {

    private ArrayList<CroppedImage> croppedSolvedImages = new ArrayList<>();
    private ArrayList<CroppedImage> croppedImagesInGame = new ArrayList<>();
    private ArrayList<CroppedImage> enemyImagesInGame = new ArrayList<>();

    private static final int SCOREPERSEC = 10;
    private static final int SCORECOSTPERMOVE = 10;
    private static final int PLAYTIME = 60*1000;
    private static final String MyPREFERENCES = "npuzzel_file";
    private static final String USERNAME = "usernameKey";
    private static final int DIFFICULTY_VERY_EASY = 2;
    private static final int DIFFICULTY_EASY = 3;
    private static final int DIFFICULTY_MEDIUM = 4;
    private static final int DIFFICULTY_HARD = 5;

    private int timeLeft;
    private int minutes;
    private int numberOfTiles = 0;
    private int usedSteps = 0;
    private int width;
    private int height;
    private int resourceId;
    private int score;

    private SharedPreferences sharedpreferences;
    private Firebase myFirebaseRef, isDone, enemyScore;

    private boolean isPlaying = false;

    private String enemyUser;
    private String userName;

    private GridView layout, layout2;
    private ValueEventListener eventListener, eventListenerWin, enemyEventListener;
    private CountDownTimer countDown;
    private Boolean enemyIsFinished = false;

    private AlertDialog CountDownTimerAlertDialog;
    private TextView countDownTimerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userName = sharedpreferences.getString(USERNAME, null);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        width = size.x;
        height = size.y;

        Intent intent = getIntent();
        resourceId = intent.getIntExtra("resourceId", 0);
        String difficulty = intent.getStringExtra("difficulty");
        enemyUser = intent.getStringExtra("enemy");

        numberOfTiles = getNumberOfTiles(difficulty);
        start();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void showSolution() {
        final AlertDialog solutionDialog = new AlertDialog.Builder(this).create();
        solutionDialog.setTitle(R.string.solution);
        solutionDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.got_it),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        solutionDialog.dismiss();
                    }
                });
        ImageView iv = new ImageView(this);
        iv.setImageResource(resourceId);
        solutionDialog.setView(iv);
        solutionDialog.show();
        solutionDialog.setCanceledOnTouchOutside(true);
    }

    private void addPopUp() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.change_difficulty);
        LinearLayout dv = new LinearLayout(this);

        ListView list = new ListView(this);
        final ArrayList<String> difficulty = new ArrayList<>();
        difficulty.add("very easy");
        difficulty.add("easy");
        difficulty.add("medium");
        difficulty.add("hard");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, difficulty);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                numberOfTiles = getNumberOfTiles(difficulty.get(position));
                alertDialog.dismiss();
                start();
            }
        });
        dv.addView(list);
        alertDialog.setView(dv);
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void start() {
        if(countDown != null) {
            countDown.cancel();
        }
        removeDataFromFirebase();

        layout = (GridView)findViewById(R.id.player);
        layout.setNumColumns(numberOfTiles);
        usedSteps = 0;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        int bmw;
        int bmh;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            bmw = width;
            bmh = (inHeight * width) / inWidth;
        } else if(inHeight > inWidth) {
            bmh = height;
            bmw = (inWidth * height) / inHeight;
        } else {
            bmh = height;
            bmw = width;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, bmw, bmh, false);

        int tileHeight = bitmap.getHeight() / numberOfTiles;
        int tileWidth = bitmap.getWidth() / numberOfTiles;
        createTiles(numberOfTiles, bitmap, tileHeight, tileWidth);

        final CustomPlayerGridViewAdapter imageAdapter = new CustomPlayerGridViewAdapter(this, R.layout.row_grid, croppedSolvedImages);
        layout.setAdapter(imageAdapter);
        final TextView timer = (TextView) findViewById(R.id.timer);
        timer.setText("time remaining: " + ConvertSecondToHHMMString((PLAYTIME * minutes / 1000)));
        countDown = new  CountDownTimer(PLAYTIME * minutes, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLeft = (int) millisUntilFinished / 1000;
                timer.setText("time remaining: " + ConvertSecondToHHMMString((int) (millisUntilFinished / 1000)));
            }

            public void onFinish() {
                timer.setText("done!");
            }
        };


        setEnemy();
        CountDownTimer c = new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
//                Toast.makeText(GamePlayActivity.this, "Start in: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
                StartCountdownTimer("" + ( millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                imageAdapter.setData(croppedImagesInGame);
                layout.setAdapter(imageAdapter);
                isPlaying = true;
                countDown.start();
                CountDownTimerAlertDialog.dismiss();

            }
        };

        c.start();
        setItemClickListenerOnGridView(numberOfTiles, layout, imageAdapter, resourceId);
    }

    private void StartCountdownTimer(String time) {
        if(countDownTimerTextView == null) {
            countDownTimerTextView = new TextView(this);
            countDownTimerTextView.setGravity(Gravity.CENTER);
            countDownTimerTextView.setTextSize(25);
        }
        countDownTimerTextView.setText(time);
        if(CountDownTimerAlertDialog != null) {
            CountDownTimerAlertDialog.setView(countDownTimerTextView);
        } else {
            CountDownTimerAlertDialog = new AlertDialog.Builder(this).create();
            CountDownTimerAlertDialog.setTitle(R.string.CountDownTimer);
            CountDownTimerAlertDialog.setView(countDownTimerTextView);

            CountDownTimerAlertDialog.setCanceledOnTouchOutside(false);
            CountDownTimerAlertDialog.show();
        }

    }

    private String ConvertSecondToHHMMString(int secondtTime)
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        df.setTimeZone(tz);
        String time = df.format(new Date(secondtTime * 1000L));

        return time;

    }

    private void setEnemy() {
        layout2 = (GridView)findViewById(R.id.enemy);
        layout2.setNumColumns(numberOfTiles);

        ArrayList<CroppedImage> test = new ArrayList<>();
        for(CroppedImage item: enemyImagesInGame) {
            item.setHoogte(50);
            item.recreateImage();
            test.add(item);
        }
        Collections.copy(enemyImagesInGame, test);
        final CustomPlayerGridViewAdapter imageAdapterEnemy = new CustomPlayerGridViewAdapter(this, R.layout.row_grid, enemyImagesInGame);
        layout2.setAdapter(imageAdapterEnemy);

        Firebase enemy = myFirebaseRef.child("users/" + enemyUser + "/clicked_tile");

            // Attach an listener to read the data at our posts reference
            eventListener = enemy.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                        long test = (long) snapshot.getValue();
                        Pair p = CheckSwitchPosition((int) test, numberOfTiles, enemyImagesInGame);
                        changeImagePosition(enemyImagesInGame, (int) test, (int) p.second);
                        imageAdapterEnemy.setData(enemyImagesInGame);
                        layout2.setAdapter(imageAdapterEnemy);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

    }

    private void setItemClickListenerOnGridView(final int numberOfTiles, final GridView layout, final CustomPlayerGridViewAdapter imageAdapter, final int resourceId) {
        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isPlaying) {
                    Pair p = CheckSwitchPosition(position, numberOfTiles, croppedImagesInGame);
                    if ((boolean) p.first) {
                        if (changePositionImageAndUpdateLayout(layout, imageAdapter, position, (Integer) p.second, isPlaying)) {
                            isPlaying = false;
                            isDone = myFirebaseRef.child("users/" + userName + "/finished");
                            isDone.setValue(true);

                            Firebase setPlayerScore = myFirebaseRef.child("users/" + userName + "/score");
                            score = calculateScore();
                            setPlayerScore.setValue(score);

                            Firebase enemyIsFinishedListener = myFirebaseRef.child("users/"+ enemyUser + "/finished");

                            // Attach an listener to read the data at our posts reference
                            eventListenerWin = enemyIsFinishedListener.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if(snapshot.getValue() != null) {
                                        Boolean isEnemyFinished = (Boolean) snapshot.getValue();
                                        if(!isPlaying && isEnemyFinished) {
                                            Toast.makeText(GamePlayActivity.this, "both stopped!", Toast.LENGTH_LONG).show();
                                            GoToWinScreen();
                                        } else if(isEnemyFinished) {
                                            enemyIsFinished = true;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    System.out.println("The read failed: " + firebaseError.getMessage());
                                }
                            });

                            if(!isPlaying && enemyIsFinished) {
                                Toast.makeText(GamePlayActivity.this, "both stopped but you stopped last!", Toast.LENGTH_LONG).show();
                                isDone.removeEventListener(eventListenerWin);
                                GoToWinScreen();
                            }
                        }
                    }
                }
            }
    });
    }

    private void GoToWinScreen() {
        enemyScore = myFirebaseRef.child("users/" + enemyUser + "/score");
        enemyEventListener = enemyScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((long) dataSnapshot.getValue() > 0) {
                    final Intent intent = new Intent(GamePlayActivity.this, YouWinActivity.class);
                    intent.putExtra("yourScore", score);
                    intent.putExtra("enemyUser", enemyUser);
                    intent.putExtra("resourceId", resourceId);
                    intent.putExtra("enemyScore", (long) dataSnapshot.getValue());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GamePlayActivity.this, "No acces to database", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    private int calculateScore() {
        return (timeLeft * SCOREPERSEC) - usedSteps * SCORECOSTPERMOVE;
    }

    private Pair CheckSwitchPosition(int position, int numberOfTiles, ArrayList<CroppedImage> list) {
        boolean canBeSwitched = false;
        int pos2 = 0;
        int i = 0;
        if (!list.get(position).getLastImage()) {
            for (CroppedImage item : list) {
                if (item.getLastImage()) {
                    pos2 = i;
                    if (pos2 == position - 1 && position % numberOfTiles != 0) {
                        canBeSwitched = true;
                    } else if (pos2 == position + 1 && position % numberOfTiles != numberOfTiles - 1) {
                        canBeSwitched = true;
                    } else if (pos2 == position + numberOfTiles) {
                        canBeSwitched = true;
                    } else if (pos2 == position - numberOfTiles) {
                        canBeSwitched = true;
                    }
                }
                i++;
            }
        }
        return new Pair<>(canBeSwitched,pos2);


    }

    private void createTiles(int numberOfTiles, Bitmap bitmap, int tileHeight, int tileWidth) {
        croppedImagesInGame.clear();
        croppedSolvedImages.clear();
        enemyImagesInGame.clear();
        for (int j = 0; j < numberOfTiles; j++) {
            for(int i = 0; i < numberOfTiles; i++) {
                CroppedImage c1;
                if (i == numberOfTiles - 1 && j == numberOfTiles - 1) {
                    c1 = new CroppedImage(null, i, j, tileWidth, tileHeight, (i + 1) * (j + 1) - 1, true);
                } else {
                    c1 = new CroppedImage(bitmap, i, j, tileWidth, tileHeight, (i + 1) * (j + 1) - 1, false);
                }
                croppedSolvedImages.add(c1);
                croppedImagesInGame.add(c1);
                enemyImagesInGame.add(c1);
            }
        }

        replacefirstTwoOnOddTHenShuffle(croppedImagesInGame);
        replacefirstTwoOnOddTHenShuffle(enemyImagesInGame);
    }

    private void replacefirstTwoOnOddTHenShuffle(ArrayList<CroppedImage> list) {
        CroppedImage last = list.remove(list.size() -1);
        if(((numberOfTiles * numberOfTiles) - 1) % 2 != 0) {
            changeImagePosition(list, 0, 1);
        }
        Collections.reverse(list);
        list.add(last);
    }

    public Boolean changePositionImageAndUpdateLayout(GridView layout, CustomPlayerGridViewAdapter adapter, int pos1,
                                                      int pos2, boolean inGame) {
        if(inGame) {
            usedSteps++;
        }
        changeImagePosition(croppedImagesInGame, pos1, pos2);
        Firebase userFirebaseRef = myFirebaseRef.child("users/" + sharedpreferences.getString(USERNAME, "Default"));
        userFirebaseRef.child("clicked_tile").setValue(pos1);
        userFirebaseRef.child("usedSteps").setValue(usedSteps);
        adapter.setData(croppedImagesInGame);
        adapter.notifyDataSetChanged();
        layout.invalidateViews();
//        layout.setAdapter(adapter);

        checkWinSituation();
        return checkWinSituation();
    }

    private void changeImagePosition(ArrayList<CroppedImage> data, int pos1, int pos2) {
        CroppedImage cTemp1 = data.get(pos1);
        CroppedImage cTemp2 = data.get(pos2);

        data.set(pos1, cTemp2);
        data.set(pos2, cTemp1);
    }

    private boolean checkWinSituation() {
        if(croppedImagesInGame.equals(croppedSolvedImages)) {
            return true;
        } else {
            return false;
        }
    }

    public int getNumberOfTiles(String difficulty) {
        switch(difficulty) {
            case "very easy":
                minutes = 5;
                return DIFFICULTY_VERY_EASY;
            case "easy":
                minutes = 10;
                return DIFFICULTY_EASY;
            case "medium":
                minutes = 15;
                return DIFFICULTY_MEDIUM;
            case "hard":
                minutes = 20;
                return DIFFICULTY_HARD;
            default:
                minutes = 15;
                return DIFFICULTY_MEDIUM;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.change_difficulty) {
            addPopUp();
        } else if(id == R.id.reset_puzzle) {
            start();
        } else if(id == R.id.show_solution) {
            showSolution();
        } else if(id == android.R.id.home || id == R.id.new_puzzle) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeDataFromFirebase();
    }

    private void removeDataFromFirebase() {
        Firebase enemy = myFirebaseRef.child("users/" + userName + "/clicked_tile");
        Firebase finished = myFirebaseRef.child("users/" + userName + "/finished");
        Firebase usedStepsInGame = myFirebaseRef.child("users/" + userName + "/usedSteps");
        Firebase difficulty = myFirebaseRef.child("users/" + userName + "/difficulty");
        difficulty.removeValue();
        if(eventListener != null) {
            enemy.removeEventListener(eventListener);
        }
        if(eventListenerWin != null) {
            isDone.removeEventListener(eventListenerWin);
        }
        if(enemyEventListener != null) {
            enemyScore.removeEventListener(enemyEventListener);
        }
        enemy.removeValue();
        finished.removeValue();
        usedStepsInGame.removeValue();
    }
}
