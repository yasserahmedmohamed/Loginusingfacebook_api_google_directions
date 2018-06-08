package ourteam.gradproject.com.taskworklogin_usingfacebook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ourteam.gradproject.com.taskworklogin_usingfacebook.HelpClasses.MylocationListener;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    AlertDialog.Builder alertDialog;
    private List<Polyline> polylines;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0, MY_PERMISSIONS_REQUEST_START_GPS = 1;
    private static final int[] COLORS = new int[]{android.R.color.holo_orange_dark, android.R.color.holo_blue_bright};

    MarkerOptions marker;
    int x=0;
    Location curentlocation;
    MarkerOptions options;
    String longt, latid;
    @BindView(R.id.userimag)
    ImageView userimag;
    @BindView(R.id.username)
    TextView usernametest;
    @BindView(R.id.loc_latitude)
    EditText loc_latitude;
    @BindView(R.id.loc_longitude)
    EditText loc_longitude;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        StatusBarUtil.setTransparent(MapActivity.this);

        ButterKnife.bind(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        polylines = new ArrayList<>();
        inizializeuserProfile();

    }

    //get data of user form intent and set it
    private void inizializeuserProfile() {

        String username = getIntent().getStringExtra("username");
        String Urlimag = getIntent().getStringExtra("img");
        usernametest.setText(username);
        Picasso.get()
                .load("https://graph.facebook.com/" + Urlimag + "/picture?type=large")
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.user_picture)
                .into(userimag);
        Log.e("image url ", "https://graph.facebook.com/" + Urlimag + "/picture?type=large");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions((Activity) this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            CheckGpsStatus();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        CheckGpsStatus();

                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "please you must accept access location ", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions((Activity) this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                }
            }

        }
    }

    //see if gps is working or not
    public void CheckGpsStatus() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GpsStatus) {

            showSettingsAlert();
        } else {
            mapFragment.getMapAsync(this);

        }

    }

    //show alert to ask for open GPS
    public void showSettingsAlert() {
        alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.gps_setting);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.open_gps_setting);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        alertDialog.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, MY_PERMISSIONS_REQUEST_START_GPS);
                //      dialogif.cancel();

            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(getApplicationContext(),"map ready",Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        marker = new MarkerOptions();
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
                curentlocation =arg0;
                 if (x==0){
                   x=1;
                     marker.position(new LatLng(curentlocation.getLatitude(), curentlocation.getLongitude())).title(GetLocalityName(curentlocation.getLatitude(), curentlocation.getLongitude()));
                     mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(curentlocation.getLatitude(), curentlocation.getLongitude())));
                     mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curentlocation.getLatitude(), curentlocation.getLongitude()), 16.0f));

                     mMap.clear();
                mMap.addMarker(marker);}
            }
        });


        longt=loc_longitude.getText().toString();
        latid=loc_latitude.getText().toString();
        if (!longt.equals("")&&!latid.equals(""))
        {    longt=loc_longitude.getText().toString();
            latid=loc_latitude.getText().toString();
            getDirections(new LatLng(curentlocation.getLatitude(), curentlocation.getLongitude()),new LatLng(Double.valueOf(latid),Double.valueOf(longt)) );
        }

    }
//
//    // move to current location
//    public void MovetoLocation() {
//        if (curent_location != null && mMap != null) {
//
//            LatLng your_location = new LatLng(curent_location.getLatitude(), curent_location.getLongitude());
//            lat = curent_location.getLatitude();
//            lng = curent_location.getLongitude();
//            marker = new MarkerOptions().position(your_location).title(GetLocalityName(your_location.latitude, your_location.longitude));
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(your_location));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curent_location.getLatitude(), curent_location.getLongitude()), 12.0f));
//
//            mMap.addMarker(marker);
//            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    mMap.clear();
//                    marker = new MarkerOptions().position(latLng).title(GetLocalityName(latLng.latitude, latLng.longitude));
//
//                    mMap.addMarker(marker);
//                    lat = latLng.latitude;
//                    lng = latLng.longitude;
//
//                }
//            });
//        }
//    }
//
    @OnClick(R.id.btn_locate)
    public void locate(){
        longt=loc_longitude.getText().toString();
         latid=loc_latitude.getText().toString();
       if (longt.equals("")||latid.equals(""))
       {
           Toast.makeText(getApplicationContext(),"please add two coordinates or choose location from map",Toast.LENGTH_SHORT).show();
       }
       else{clearroute();
        getDirections(new LatLng(curentlocation.getLatitude(), curentlocation.getLongitude()),new LatLng(Double.valueOf(latid),Double.valueOf(longt)) );
       }

    }

    // get location name of latitude , longtiude of current location
    public String GetLocalityName(double lat, double lng) {


        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String add = "";
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getCountryName();
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

        return add;
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            //Log.e("error",e.getMessage());
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {
        mMap.clear();
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),  " distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();


        //start marker
        options = new MarkerOptions();
        options.position(new LatLng(Double.valueOf(curentlocation.getLatitude()), Double.valueOf(curentlocation.getLongitude())));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(new LatLng(Double.valueOf(latid), Double.valueOf(longt)));
        //   options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }

    public void getDirections(LatLng start, LatLng end) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
//                .key("AIzaSyDMY-MnnW-QNAxy6OWT0XNVwc0j8r7OcfA")
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    public void clearroute() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
        mMap.clear();
    }



    @Override
    protected void onResume() {
        super.onResume();

    }
}
