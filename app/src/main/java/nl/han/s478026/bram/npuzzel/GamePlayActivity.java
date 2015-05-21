package nl.han.s478026.bram.npuzzel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.AlertDialog;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */

public class GamePlayActivity extends ActionBarActivity {


    public static final int TOAST_DURATION = 100;
    private ArrayList<CroppedImage> croppedSolvedImages = new ArrayList<>();
    private ArrayList<CroppedImage> croppedImagesInGame = new ArrayList<>();

    public static final String MyPREFERENCES = "npuzzel_file";
    public static final String USERNAME = "usernameKey";
    private SharedPreferences sharedpreferences;

    private static int DIFFICULTY_EASY = 3;
    private static int DIFFICULTY_MEDIUM = 4;
    private static int DIFFICULTY_HARD = 5;
    private Boolean clicked = false;
    private int pos1;
    private int pos2;
    private int usedSteps = 0;
    private boolean isPlaying = false;
    private int numberOfSteps = 0;
    private int numberOfTiles = 0;
    private int width;
    private int height;
    private int resourceId;
    private GridView layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_detail);
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

        layout = (GridView)findViewById(R.id.gridView);
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

        final CustomGridViewAdapter imageAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, croppedSolvedImages);
        layout.setAdapter(imageAdapter);
        CountDownTimer c = new CountDownTimer(3000, 1000) {
            int timeTillStart = 3;
            Toast p = Toast.makeText(GamePlayActivity.this, "Start in: " + timeTillStart, Toast.LENGTH_SHORT);
            public void onTick(long millisUntilFinished) {
                p.cancel();
                p = Toast.makeText(GamePlayActivity.this, "Start in: " + timeTillStart, Toast.LENGTH_SHORT);
                p.show();
                timeTillStart--;
            }

            @Override
            public void onFinish() {
                imageAdapter.setData(croppedImagesInGame);
                layout.setAdapter(imageAdapter);
                isPlaying = true;
            }
        };

        c.start();
        setItemClickListenerOnGridView(numberOfTiles, layout, imageAdapter, resourceId);
    }

    private void setItemClickListenerOnGridView(final int numberOfTiles, final GridView layout, final CustomGridViewAdapter imageAdapter, final int resourceId) {
        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isPlaying) {
                    boolean canBeSwitched = false;
                    int pos2 = 0;
                    int i = 0;
                    if (!croppedImagesInGame.get(position).getLastImage()) {
                        for (CroppedImage item : croppedImagesInGame) {
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
                        if (canBeSwitched) {
                            if (changePositionImageAndUpdateLayout(layout, imageAdapter, position, pos2, isPlaying)) {
                                isPlaying = false;
                                Intent intent = new Intent(GamePlayActivity.this, YouWinActivity.class);
                                intent.putExtra("resourceId", resourceId);
                                intent.putExtra("usedSteps", usedSteps);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
    });
    }

    private void createTiles(int numberOfTiles, Bitmap bitmap, int tileHeight, int tileWidth) {
        croppedImagesInGame.clear();
        croppedSolvedImages.clear();
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
            }
        }

        replacefirstTwoOnOddTHenShuffle(croppedImagesInGame);
    }

    private void replacefirstTwoOnOddTHenShuffle(ArrayList<CroppedImage> list) {
        CroppedImage last = list.remove(list.size() -1);
        if(((numberOfTiles * numberOfTiles) - 1) % 2 != 0) {
            changeImagePosition(0, 1);
        }
        Collections.reverse(list);
        list.add(last);
    }

    public Boolean changePositionImageAndUpdateLayout(GridView layout, CustomGridViewAdapter adapter, int pos1,
                                                      int pos2, boolean inGame) {
        changeImagePosition(pos1, pos2);
        Firebase myFirebaseRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/");
        Firebase userFirebaseRef = myFirebaseRef.child("users/" + sharedpreferences.getString(USERNAME, "Default"));
        userFirebaseRef.child("clicked_tile").setValue(pos1);
        adapter.setData(croppedImagesInGame);
        layout.setAdapter(adapter);
        if(inGame) {
            usedSteps++;
        }
        checkWinSituation();
        return checkWinSituation();
    }

    private void changeImagePosition(int pos1, int pos2) {
        CroppedImage cTemp1 = croppedImagesInGame.get(pos1);
        CroppedImage cTemp2 = croppedImagesInGame.get(pos2);

        croppedImagesInGame.set(pos1, cTemp2);
        croppedImagesInGame.set(pos2, cTemp1);
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


}
