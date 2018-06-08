package ourteam.gradproject.com.taskworklogin_usingfacebook.HelpClasses;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by yasser ahmed on 6/7/2018.
 */

public class MylocationListener implements LocationListener {
    Location retlocation;
    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){
retlocation=location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public Location getcurrentlocation(){
        return  retlocation;
    }
}
