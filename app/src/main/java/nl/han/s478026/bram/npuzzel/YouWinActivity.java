package nl.han.s478026.bram.npuzzel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */
public class YouWinActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_win);

        Intent intent = getIntent();
        int resourceId = intent.getIntExtra("resourceId", 0);
        int usedSteps = intent.getIntExtra("usedSteps", 0);

        ImageView iv = (ImageView) findViewById(R.id.imageViewResult);
        TextView tv = (TextView) findViewById(R.id.numberOfClicksUsed);
        iv.setImageResource(resourceId);
        tv.setText("U had " + usedSteps + " stappen nodig!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_you_win, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home || id == R.id.new_puzzle) {
            Intent intent = new Intent(YouWinActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
