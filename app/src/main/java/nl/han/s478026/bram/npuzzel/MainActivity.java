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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */
public class MainActivity extends ActionBarActivity {

    ArrayList<ImageItem> imageList = new ArrayList<>();
    private static int ROWS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //LinearLayout layout = (LinearLayout)findViewById(R.id.layout_container);


        GridView layout = (GridView)findViewById(R.id.gridView2);
        layout.setNumColumns(ROWS);
        layout.setScrollingCacheEnabled(false);

        Field[] afbeeldingResources = R.drawable.class.getFields(); //of R.drawable.class.getFields();
        for (Field f : afbeeldingResources) {
            if ( !f.getName().contains("abc")) {
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
                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup1);
                RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
                final String difficulty = (String) radioButton.getTag();

                Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);
                intent.putExtra("resourceId", imageList.get(position).getResourceId());
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
