package nl.han.s478026.bram.npuzzel;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
//import android.widget.Toast;

import java.util.List;
import java.util.Observable;

/**
 * Created by Daniel on 3-6-2015.
 */
public class LocationUpdater extends Observable {
    private static final int TIME_INTERVAL_FOR_LOCATION_UPDATE = 1000;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Context parentContext;

    private WaitingDialog waitingDialog;

    public LocationUpdater(LocationManager locationManager, Context context){
        this.locationManager = locationManager;
        parentContext = context;
        checkIfAnyLocationProviderIsActive();
        setRemoteLocation();
    }
    private void setRemoteLocation() {
        if(!checkIfAnyLocationProviderIsActive()) {
            showNoActiveProviderDialog();
        }else{
            updateLocation();
        }
    }
    
    private void updateLocation() {
        Criteria criteria = new Criteria();
        String locationProvider = locationManager.getBestProvider(criteria, true);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                waitingDialog.dismiss();
//                Toast.makeText(parentContext, "Updated location to " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                setChanged();
                notifyObservers(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(locationProvider, TIME_INTERVAL_FOR_LOCATION_UPDATE, 0, locationListener);

        waitingDialog = new WaitingDialog(parentContext, parentContext.getString(R.string.no_location_available), parentContext.getString(R.string.wait_for_location));
    }

    private void showNoActiveProviderDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(parentContext).create();
        alertDialog.setTitle("No Location Providers Active");
        alertDialog.setMessage(parentContext.getString(R.string.no_active_location_providers));
        LinearLayout dv = new LinearLayout(parentContext);
        dv.setOrientation(LinearLayout.VERTICAL);

        Button b1 = addDialogButton(alertDialog, parentContext.getString(R.string.location_settings));
        dv.addView(b1);

        alertDialog.setView(dv);
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private Button addDialogButton(final AlertDialog alertDialog, String text){
        Button b1 = new Button(parentContext);
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

    private boolean checkIfAnyLocationProviderIsActive(){
        List<String> providers = locationManager.getProviders(true);
        Log.d("Listing providers", providers + "");

        return providers.size() != 1;
    }

    @Override
    public void deleteObservers(){
        super.deleteObservers();
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
