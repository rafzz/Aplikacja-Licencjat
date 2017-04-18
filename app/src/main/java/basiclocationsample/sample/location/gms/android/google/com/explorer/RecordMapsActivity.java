package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecordMapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private Database database;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    private Intent intent;

    public void loadDB() {
        double lat;
        double lng;
        float zoom;

        intent = getIntent();

        int id = intent.getExtras().getInt("id");

        ToggleButton but = (ToggleButton) findViewById(R.id.toggleButton);
        but.setText(String.valueOf(id));

        database = new Database(this);
        Cursor cursor = database.writeAllData();

        String path;

        while (cursor.moveToNext()) {

            if (id == cursor.getInt(0)) {
                lat = cursor.getDouble(2);
                lng = cursor.getDouble(3);
                zoom = cursor.getFloat(4);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

                path = cursor.getString(5);

                if(path != null){

                    String[] pathTab = path.split("\n");

                    for(String s : pathTab){
                        String[] doubles = s.split(",");

                        pathList.add(new LatLng(Double.valueOf( doubles[0]),Double.valueOf(doubles[1])));
                    }

                }


                break;
            }
        }
    }

    private LocationRequest locationRequest;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadDB();
    }

    private  ArrayList<LatLng> pathList = new ArrayList<LatLng>();

    public void startTraceRoute(){

    }

    @Override
    public void onLocationChanged(Location location) {

        pathList.add(new LatLng(location.getLatitude(),location.getLongitude()));
        mMap.addPolyline(new PolylineOptions()
                .addAll(pathList)
                .width(5).color(Color.BLUE).geodesic(true));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }

    private String pathListString ="";

    @Override
    protected void onStop() {


        //String pathListString ="";

        for (LatLng ll : pathList){

            pathListString+=ll.latitude+","+ll.longitude+"\n";

        }

        //ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton);
        //tb.setText(pathListString);

        database.updateData(intent.getExtras().getInt("id"),pathListString);

        super.onStop();
    }

    //TODO
    public void startRecordingPath(View button) {
        if ( !button.isSelected() ) {

            //button.setSelected(true);
        } else if ( button.isSelected() ) {

            //button.setSelected(false);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}




}
