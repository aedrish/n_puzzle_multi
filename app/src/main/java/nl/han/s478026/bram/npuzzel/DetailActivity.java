package nl.han.s478026.bram.npuzzel;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class DetailActivity extends ActionBarActivity {


    private ArrayList<CroppedImage> croppedSolvedImages = new ArrayList<>();
    private ArrayList<CroppedImage> croppedImagesInGame = new ArrayList<>();
    private Boolean clicked = false;
    private int pos1;
    private int pos2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Intent intent = getIntent();
        int resourceId = intent.getIntExtra("resourceId", 0);
        String difficulty = intent.getStringExtra("difficulty");

        final int numberOfTiles = getNumberOfTiles(difficulty);

        LinearLayout test = (LinearLayout) findViewById(R.id.GridPreview);

        final GridView layout = (GridView)findViewById(R.id.gridView);
        layout.setNumColumns(numberOfTiles);


        final Bitmap temp = BitmapFactory.decodeResource(getResources(), resourceId);
        final Bitmap bitmap = Bitmap.createScaledBitmap(temp, width, (int) (height * (width * 1.0 / height)), true);
        int tileHeight = bitmap.getHeight() / numberOfTiles;
        int tileWidth = bitmap.getWidth() / numberOfTiles;
        createTiles(numberOfTiles, bitmap, tileHeight, tileWidth);

        final CustomGridViewAdapter imageAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, croppedSolvedImages);
        layout.setAdapter(imageAdapter);
        CountDownTimer c = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Collections.shuffle(croppedImagesInGame);
                imageAdapter.data = croppedImagesInGame;
                layout.setAdapter(imageAdapter);
            }
        };

        c.start();
        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean canBeSwitched = false;
                int pos2 = 0;
                int i = 0;
                if(!croppedImagesInGame.get(position).getLastImage()) {
                    for(CroppedImage item: croppedImagesInGame) {
                        if(item.getLastImage()) {
                            pos2 = i;
                            if (pos2 == position - 1) {
                                canBeSwitched = true;
                            } else if (pos2 == position + 1) {
                                canBeSwitched = true;
                            } else if (pos2 == position + numberOfTiles) {
                                canBeSwitched = true;
                            } else if(pos2 == position - numberOfTiles) {
                                canBeSwitched = true;
                            }
                        }
                        i++;
                    }
                    if(canBeSwitched) {
                        changePositionImage(layout, imageAdapter, position, pos2);
                    }
                }
            }
}       );
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void createTiles(int numberOfTiles, Bitmap bitmap, int tileHeight, int tileWidth) {
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
    }

    public Boolean changePositionImage(GridView layout, CustomGridViewAdapter adapter, int pos1, int pos2) {
        CroppedImage cTemp1 = croppedImagesInGame.get(pos1);
        CroppedImage cTemp2 = croppedImagesInGame.get(pos2);

        croppedImagesInGame.set(pos1, cTemp2);
        croppedImagesInGame.set(pos2, cTemp1);
        adapter.data = croppedImagesInGame;
        layout.setAdapter(adapter);
        return true;


    }

    public int getNumberOfTiles(String difficulty) {
        switch(difficulty) {
            case "easy":
                return 3;
            case "medium":
                return 4;
            case "hard":
                return 5;
            default:
                return 3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    private int changePixelToDP(int input) {
        int pixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, input, getResources().getDisplayMetrics());
        return pixels;
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
        } else if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
