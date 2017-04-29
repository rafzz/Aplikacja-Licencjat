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
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.android.SphericalUtil;


import java.util.ArrayList;

public class RecordMapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Database database;
    private GoogleApiClient mGoogleApiClient;

    public ArrayList<LatLng> getPathList() {
        return pathList;
    }

    public void setPathList(ArrayList<LatLng> pathList) {
        this.pathList = pathList;
    }

    private ArrayList<LatLng> pathList = new ArrayList<LatLng>();


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

        ToggleButton but = (ToggleButton) findViewById(R.id.toggleB);
        //but.setText(String.valueOf(id));

        database = new Database(this);
        Cursor cursor = database.writeAllData();

        String path;

        while (cursor.moveToNext()) {

            if (id == cursor.getInt(0)) {
                lat = cursor.getDouble(2);
                lng = cursor.getDouble(3);
                zoom = cursor.getFloat(4);

                //animate camera and bounds
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom), new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        //LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                        //mMap.setLatLngBoundsForCameraTarget(latLngBounds);
                    }

                    @Override
                    public void onCancel() {
                    }

                });

                mMap.setMinZoomPreference(zoom);


                path = cursor.getString(5);

                if (path != null) {

                    String[] pathTab = path.split("\n");

                    for (String s : pathTab) {
                        String[] doubles = s.split(",");

                        pathList.add(new LatLng(Double.valueOf(doubles[0]), Double.valueOf(doubles[1])));
                    }

                }


                break;
            }
        }
    }

    private LocationRequest locationRequest;
    private ImageView imageView;
    private TextView textView;
    private FloatingActionButton photoBut;
    private FloatingActionButton closeButton;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadDB();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("marker");

                mMap.addMarker(markerOptions);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                //marker.remove();
                imageView = (ImageView) findViewById(R.id.markerImageView);
                textView = (TextView) findViewById(R.id.markerTextView);
                photoBut = (FloatingActionButton) findViewById(R.id.photoButton);
                closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                photoBut.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                return false;
            }
        });


    }

    public void closeMarkerMenu(View view) {
        imageView = (ImageView) findViewById(R.id.markerImageView);
        textView = (TextView) findViewById(R.id.markerTextView);
        photoBut = (FloatingActionButton) findViewById(R.id.photoButton);
        closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        photoBut.setVisibility(View.INVISIBLE);
        closeButton.setVisibility(View.INVISIBLE);
    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onLocationChanged(Location location) {


        pathList.add(new LatLng(location.getLatitude(), location.getLongitude()));

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
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }

    private String pathListString = "";

    @Override
    protected void onStop() {


        //String pathListString ="";

        for (LatLng ll : pathList) {

            pathListString += ll.latitude + "," + ll.longitude + "\n";

        }

        //ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton);
        //tb.setText(pathListString);

        database.updateData(intent.getExtras().getInt("id"), pathListString);

        super.onStop();
    }

    //TODO
    public void startRecordingPath(View button) {
        if (!button.isSelected()) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.toggleB);
            //tb.setText("kufa");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            button.setSelected(true);




        } else if ( button.isSelected() ) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.toggleB);
            //tb.setText("nie kufa");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this );
            button.setSelected(false);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}




}
