package nl.han.s478026.bram.npuzzel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Observable;

/**
 * Created by Gebruiker on 12-6-2015.
 */
public class NoActiveProviderAlert extends Observable{
    public NoActiveProviderAlert(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle("No Location Providers Active");
        alertDialog.setMessage(context.getString(R.string.no_active_location_providers));

        LinearLayout dv = new LinearLayout(context);
        dv.setOrientation(LinearLayout.VERTICAL);

        Button b1 = addDialogButton(alertDialog, context.getString(R.string.location_settings), context);
        dv.addView(b1);

        alertDialog.setView(dv);
        alertDialog.show();
    }

    private Button addDialogButton(final AlertDialog alertDialog, String text, Context context){
        Button b1 = new Button(context);
        b1.setText(text);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                setChanged();
                notifyObservers("no_location_provider");
            }
        });

        return b1;
    }

}
