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
import android.widget.GridView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class GameStartActivity extends ActionBarActivity {

    ArrayList<ImageItem> imageList = new ArrayList<>();
    private static int ROWS = 1;
    private String difficulty;
    private String enemyUser;
    public static final String MyPREFERENCES = "npuzzel_file";
    public static final String USERNAME = "usernameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        difficulty = intent.getStringExtra("difficulty");
        enemyUser = intent.getStringExtra("enemy");

        if (intent.getStringExtra("pickImage").equals("yes")) {
            setContentView(R.layout.activity_image_selection);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();

            display.getSize(size);
            int width = size.x;
            int height = size.y;

            GridView layout = (GridView) findViewById(R.id.gridView2);
            layout.setNumColumns(ROWS);
            layout.setScrollingCacheEnabled(false);

            Field[] afbeeldingResources = R.drawable.class.getFields();
            for (Field f : afbeeldingResources) {
                if (f.getName().contains("game_")) {
                    try {
                        String name = f.getName();
                        int resourceId = f.getInt(null);

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
                        ImageItem item = new ImageItem(resourceId, name, bitmap);
                        imageList.add(item);
                    } catch (Exception e) {
                        Log.e("MAD", "### OOPS", e);
                    }
                }
            }

            final MainCustomGridViewAdapter imageAdapter = new MainCustomGridViewAdapter(this, R.layout.row_grid_main, imageList, ROWS, (int) (width * 0.8));
            layout.setAdapter(imageAdapter);
            layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Resource id", "" + imageList.get(position).getResourceId());
                    Intent intent = new Intent(GameStartActivity.this, GamePlayActivity.class);
                    intent.putExtra("resourceId", imageList.get(position).getResourceId());
                    intent.putExtra("difficulty", difficulty);
                    intent.putExtra("enemy", enemyUser);
                    startActivity(intent);
                    finish();
                }
            });
        }else{
            setContentView(R.layout.opponent_picking_image);
            Firebase selectedImageRef = new Firebase("https://n-puzzle-bram-daniel.firebaseio.com/users/"+enemyUser+"/selected_image");
            selectedImageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Image resource id = ", ""+ dataSnapshot.getValue());
                    Intent intent = new Intent(GameStartActivity.this, GamePlayActivity.class);
                    intent.putExtra("resourceId", Integer.parseInt(dataSnapshot.getValue().toString()));
                    intent.putExtra("difficulty", difficulty);
                    intent.putExtra("enemy", enemyUser);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private int changePixelToDP(int input) {
        int pixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        return pixels;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(GameStartActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
