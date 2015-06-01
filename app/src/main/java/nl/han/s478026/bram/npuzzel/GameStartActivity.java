package nl.han.s478026.bram.npuzzel;

import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class GameStartActivity extends ActionBarActivity {

    ArrayList<ImageItem> imageList = new ArrayList<>();
    private static int ROWS = 1;
    private String difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Intent intent = getIntent();
        difficulty = intent.getStringExtra("difficulty");

        GridView layout = (GridView)findViewById(R.id.gridView2);
        layout.setNumColumns(ROWS);
        layout.setScrollingCacheEnabled(false);

        Field[] afbeeldingResources = R.drawable.class.getFields(); //of R.drawable.class.getFields();
        for (Field f : afbeeldingResources) {
            if (f.getName().contains("game_")) {
                try {
                    String name = f.getName();
                    int resourceId = f.getInt(null);

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
                    ImageItem item  = new ImageItem(resourceId, name, bitmap);
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

                Intent intent = new Intent(GameStartActivity.this, GamePlayActivity.class);
                intent.putExtra("resourceId", imageList.get(position).getResourceId());
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });
    }

    /*private void addViewToLayout(LinearLayout layout, String name, final int resourceId) {
        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL);

        l.setLayoutParams(lp);

        ImageView image =  new ImageView(this);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(changePixelToDP(60), changePixelToDP(60));
        image.setLayoutParams(layoutParams);
        image.setImageResource(resourceId);
        image.requestLayout();

        l.addView(image);

        Button button = new Button(this);
        button.setText(name.replace("_", " "));


        LinearLayout.LayoutParams bLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, changePixelToDP(60));
        button.setLayoutParams(bLayoutParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup1);
                RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
                final String difficulty = (String) radioButton.getTag();

                Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);
                intent.putExtra("resourceId", resourceId);
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });

        l.addView(button);
        layout.addView(l);
    }*/

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
