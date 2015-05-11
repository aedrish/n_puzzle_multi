package nl.han.s478026.bram.npuzzel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import java.lang.reflect.Field;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = (LinearLayout)findViewById(R.id.layout_container);
        Field[] afbeeldingResources = R.drawable.class.getFields(); //of R.drawable.class.getFields();
        for (Field f : afbeeldingResources) {
            if ( f.getName().contains("custom")) {
                try {
                    String name = f.getName();
                    int resourceId = f.getInt(null);
                    addViewToLayout(layout, name, resourceId);
                } catch (Exception e) {
                    Log.e("MAD", "### OOPS", e);
                }
            }
        }
    }

    private void addViewToLayout(LinearLayout layout, String name, final int resourceId) {
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
        button.setText(name.replace("_", " ").replace("custom", ""));


        LinearLayout.LayoutParams bLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, changePixelToDP(60));
        button.setLayoutParams(bLayoutParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup1);
                RadioButton radioButton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
                final String difficulty = (String) radioButton.getTag();

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("resourceId", resourceId);
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });

        l.addView(button);
        layout.addView(l);
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
