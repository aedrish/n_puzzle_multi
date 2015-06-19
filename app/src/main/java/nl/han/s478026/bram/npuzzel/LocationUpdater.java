package nl.han.s478026.bram.npuzzel;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Observable;

/**
 * Created by Daniel on 3-6-2015.
 */
public class LocationUpdater extends Observable {
    private static final int TIME_INTERVAL_FOR_LOCATION_UPDATE = 1000;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private SelectDifficultyActivity parent;

    public LocationUpdater(SelectDifficultyActivity parent) {
        locationManager = (LocationManager) parent.getSystemService(Context.LOCATION_SERVICE);

        this.parent = parent;
        checkIfAnyLocationProviderIsActive();
    }

    private void updateLocation() {
        Criteria criteria = new Criteria();
        String locationProvider = locationManager.getBestProvider(criteria, true);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
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
    }

    public void checkIfAnyLocationProviderIsActive(){
        List<String> providers = locationManager.getProviders(true);

        if (providers.size() == 1){
            NoActiveProviderAlert noProviderAlert = new NoActiveProviderAlert(parent);
            noProviderAlert.addObserver(parent);
        }else {
            setChanged();
            notifyObservers("hasActiveProvider");
            updateLocation();
        }
    }

    @Override
    public void deleteObservers(){
        super.deleteObservers();
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
