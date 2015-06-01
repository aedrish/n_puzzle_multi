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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public static final String MyPREFERENCES = "npuzzel_file";
    public static final String USERNAME = "usernameKey";
    private static final int PLAYTIME = 60*1000*10;
    private SharedPreferences sharedpreferences;
    private Firebase myFirebaseRef;

    private static int DIFFICULTY_EASY = 3;
    private static int DIFFICULTY_MEDIUM = 4;
    private static int DIFFICULTY_HARD = 5;
    private int usedSteps = 0;
    private boolean isPlaying = false;
    private int numberOfTiles = 0;
    private int width;
    private int height;
    private int resourceId;
    private GridView layout, layout2;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_gameplay);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        width = size.x;
        height = size.y;

        Intent intent = getIntent();
        resourceId = intent.getIntExtra("resourceId", 0);
        String difficulty = intent.getStringExtra("difficulty");

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
        dv.setOrientation(LinearLayout.VERTICAL);
        Button b1 = addButton(alertDialog, getResources().getString((R.string.difficulty_easy)), DIFFICULTY_EASY);
        Button b2 = addButton(alertDialog, getResources().getString((R.string.difficulty_medium)), DIFFICULTY_MEDIUM);
        Button b3 = addButton(alertDialog, getResources().getString((R.string.difficulty_hard)), DIFFICULTY_HARD);
        dv.addView(b1);
        dv.addView(b2);
        dv.addView(b3);
        alertDialog.setView(dv);
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private Button addButton(final AlertDialog alertDialog, String bText, final int nTiles) {
        Button b1 = new Button(this);
        b1.setText(bText);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfTiles = nTiles;
                alertDialog.dismiss();
                start();
            }
        });
        return b1;
    }


    private void start() {

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
        timer.setText("time remaining: " + ConvertSecondToHHMMString((int) (PLAYTIME / 1000)));
        final CountDownTimer countDown = new  CountDownTimer(PLAYTIME, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("time remaining: " + ConvertSecondToHHMMString((int) (millisUntilFinished / 1000)));
            }

            public void onFinish() {
                timer.setText("done!");
            }
        };


        setEnemy();
        CountDownTimer c = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                Toast.makeText(GamePlayActivity.this, "Start in: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                imageAdapter.setData(croppedImagesInGame);
                layout.setAdapter(imageAdapter);
                isPlaying = true;
                countDown.start();
                Toast.makeText(GamePlayActivity.this, "GO!", Toast.LENGTH_SHORT).show();

            }
        };

        c.start();
        setItemClickListenerOnGridView(numberOfTiles, layout, imageAdapter, resourceId);
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

        Firebase enemy = myFirebaseRef.child("users/aedrish/clicked_tile");

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
                    boolean canBeSwitched = false;
                    Pair p = CheckSwitchPosition(position, numberOfTiles, croppedImagesInGame);
                    if ((boolean) p.first) {
                        if (changePositionImageAndUpdateLayout(layout, imageAdapter, position, (Integer) p.second, isPlaying)) {
                            isPlaying = false;
                            Intent intent = new Intent(GamePlayActivity.this, YouWinActivity.class);
                            intent.putExtra("resourceId", resourceId);
                            intent.putExtra("usedSteps", usedSteps);
                            startActivity(intent);
                        }
                    }
                }
            }
    });
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
        Pair test =  new Pair<>(canBeSwitched,pos2);

        return test;
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
        layout.setAdapter(adapter);

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
            case "easy":
                return DIFFICULTY_EASY;
            case "medium":
                return DIFFICULTY_MEDIUM;
            case "hard":
                return DIFFICULTY_HARD;
            default:
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

        Firebase enemy = myFirebaseRef.child("users/aedrish/clicked_tile");
        enemy.removeEventListener(eventListener);
        enemy.removeValue();
    }
}
