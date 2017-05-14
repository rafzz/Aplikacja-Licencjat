package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Gallery;
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


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.*;

import static android.provider.Settings.System.DATE_FORMAT;

public class RecordMapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Database database;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<LatLng> pathList = new ArrayList<LatLng>();
    private HashMap<LatLng,MarkerContener> markersMap = new HashMap<LatLng,MarkerContener>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_maps);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

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

        //ToggleButton but = (ToggleButton) findViewById(R.id.toggleB);
        //but.setText(String.valueOf(id));

        database = new Database(this);
        Cursor cursor = database.writeAllData();

        String path;
        String markers;

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

                markers = cursor.getString(6);



                if (markers != null) {

                    String[] markersTab = markers.split("\n");

                    for (String s : markersTab) {
                        String[] doubles = s.split(",");


                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(doubles[0]), Double.valueOf(doubles[1]))));

                        if(doubles.length==2){
                            markersMap.put(new LatLng(Double.valueOf(doubles[0]), Double.valueOf(doubles[1])), new MarkerContener());
                        }else if(doubles.length==3){
                            markersMap.put(new LatLng(Double.valueOf(doubles[0]), Double.valueOf(doubles[1])), new MarkerContener(doubles[2]));
                        }
                        /*
                        try {
                            markersMap.put(new LatLng(Double.valueOf(doubles[0]), Double.valueOf(doubles[1])), new MarkerContener(doubles[3]));
                        }catch (ArrayIndexOutOfBoundsException e){
                            markersMap.put(new LatLng(Double.valueOf(doubles[0]), Double.valueOf(doubles[1])), new MarkerContener());
                        }
                        */
                    }

                }



                break;
            }

        }
    }

    private LocationRequest locationRequest;
    private ImageView imageView;
    private FloatingActionButton deleteButton;
    private FloatingActionButton photoBut;
    private FloatingActionButton closeButton;
    private FloatingActionButton saveButton;
    private FloatingActionButton rotateButton;

    private static LatLng mCurrentMarkerLatLng;
    private static Marker mCurrentMarker;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadDB();

        mMap.addPolyline(new PolylineOptions()
                .addAll(pathList)
                .width(5).color(Color.BLUE).geodesic(true));




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.addMarker(new MarkerOptions().position(latLng));

                markersMap.put(latLng, new MarkerContener());


            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                //marker.remove();
                imageView = (ImageView) findViewById(R.id.markerImageView);
                rotateButton = (FloatingActionButton) findViewById(R.id.rotateButton);
                photoBut = (FloatingActionButton) findViewById(R.id.photoButton);
                closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
                saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
                deleteButton = (FloatingActionButton) findViewById(R.id.deleteMarkerButton);
                deleteButton.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                rotateButton.setVisibility(View.VISIBLE);
                photoBut.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);


                mCurrentMarker=marker;
                mCurrentMarkerLatLng=marker.getPosition();
                for(Map.Entry<LatLng,MarkerContener> entry : markersMap.entrySet()){
                    if(entry.getKey().equals(mCurrentMarkerLatLng)){
                        //markersMap.put(ll,new MarkerContener(mCurrentPhotoPath));
                        mCurrentPhotoPath=entry.getValue().getPhotoPath();
                        setPic();
                    }
                }

                //!!!!



                return false;
            }







        });

        /*
        imageView = (ImageView) findViewById(R.id.markerImageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(imageView.getScaleType()==ImageView.ScaleType.CENTER_CROP) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER);

                }else if(imageView.getScaleType()==ImageView.ScaleType.CENTER) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

                return false;
            }

        });
        */



    }

    public void rotateImage(View view){

        imageView = (ImageView) findViewById(R.id.markerImageView);
        if(imageView.getRotation()==0){
            imageView.setRotation(90);
        }else if(imageView.getRotation()==90){
            imageView.setRotation(180);
        }else if(imageView.getRotation()==180){
            imageView.setRotation(270);
        }else if(imageView.getRotation()==270){
            imageView.setRotation(360);
        }else if(imageView.getRotation()==360){
            imageView.setRotation(90);
        }


    }

    public void closeMarkerMenu(View view) {
        imageView = (ImageView) findViewById(R.id.markerImageView);
        rotateButton = (FloatingActionButton) findViewById(R.id.rotateButton);
        photoBut = (FloatingActionButton) findViewById(R.id.photoButton);
        closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        imageView.setVisibility(View.INVISIBLE);
        rotateButton.setVisibility(View.INVISIBLE);
        photoBut.setVisibility(View.INVISIBLE);
        closeButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        deleteButton = (FloatingActionButton) findViewById(R.id.deleteMarkerButton);
        deleteButton.setVisibility(View.INVISIBLE);
    }

    public void saveMarkerMenu(View view) {
        imageView = (ImageView) findViewById(R.id.markerImageView);
        rotateButton = (FloatingActionButton) findViewById(R.id.rotateButton);
        photoBut = (FloatingActionButton) findViewById(R.id.photoButton);
        closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        imageView.setVisibility(View.INVISIBLE);
        rotateButton.setVisibility(View.INVISIBLE);
        photoBut.setVisibility(View.INVISIBLE);
        closeButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        deleteButton = (FloatingActionButton) findViewById(R.id.deleteMarkerButton);
        deleteButton.setVisibility(View.INVISIBLE);

        for(LatLng ll : markersMap.keySet()){
            if(ll.equals(mCurrentMarkerLatLng)){
                markersMap.put(ll,new MarkerContener(mCurrentPhotoPath));
            }
        }

    }

    public void deleteMarker(View view){
        markersMap.remove(mCurrentMarkerLatLng);
        mCurrentMarker.remove();
        imageView = (ImageView) findViewById(R.id.markerImageView);
        rotateButton = (FloatingActionButton) findViewById(R.id.rotateButton);
        photoBut = (FloatingActionButton) findViewById(R.id.photoButton);
        closeButton = (FloatingActionButton) findViewById(R.id.closeButton);
        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        imageView.setVisibility(View.INVISIBLE);
        rotateButton.setVisibility(View.INVISIBLE);
        photoBut.setVisibility(View.INVISIBLE);
        closeButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        deleteButton = (FloatingActionButton) findViewById(R.id.deleteMarkerButton);
        deleteButton.setVisibility(View.INVISIBLE);



    }


    private LatLng mCurrentLatLng;

    @Override
    public void onLocationChanged(Location location) {



        mCurrentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        pathList.add(new LatLng(location.getLatitude(), location.getLongitude()));

        mMap.addPolyline(new PolylineOptions()
                .addAll(pathList)
                .width(5).color(Color.BLUE).geodesic(true));


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        RadioButton led = (RadioButton) findViewById(R.id.conectionLed);
        led.setSelected(mGoogleApiClient.isConnected());
        led.setChecked(mGoogleApiClient.isConnected());


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

    private String markersString="";



    //TODO
    @Override
    public void finish() {

        if(pathList.size()!=0) {
            for (LatLng ll : pathList) {
                pathListString += ll.latitude + "," + ll.longitude + "\n";
            }
            database.updateData(intent.getExtras().getInt("id"), pathListString);
        }

        if(markersMap.keySet().size()!=0) {
            for (Map.Entry<LatLng,MarkerContener> entry : markersMap.entrySet()) {


                    markersString += entry.getKey().latitude +
                            "," + entry.getKey().longitude +
                            "," + entry.getValue().getPhotoPath() + "\n";

            }


            database.updateMarkers(intent.getExtras().getInt("id"), markersString);
        }
        super.finish();
    }



    public void startRecordingPath(View button) {
        Chronometer timer = (Chronometer) findViewById(R.id.chronometer);
        if (!button.isSelected()) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.toggleB);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            button.setSelected(true);
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();


        } else if ( button.isSelected() ) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.toggleB);

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this );
            button.setSelected(false);
            //tb.setBackgroundDrawable(getResources().getDrawable(R.drawable.quantum_ic_pause_circle_filled_grey600_36));
            timer.stop();
        }
    }

    public void addMarker(View view){

        if(mCurrentLatLng==null){

        }else{
            mMap.addMarker(new MarkerOptions().position(mCurrentLatLng));
            markersMap.put(mCurrentLatLng, new MarkerContener());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        RadioButton led = (RadioButton) findViewById(R.id.conectionLed);
        led.setSelected(mGoogleApiClient.isConnected());
        led.setChecked(mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        RadioButton led = (RadioButton) findViewById(R.id.conectionLed);
        led.setSelected(mGoogleApiClient.isConnected());
        led.setChecked(mGoogleApiClient.isConnected());
    }

    private static final int REQUEST_TAKE_PHOTO = 1;
    private final String AUTHORITY = "com.example.android.fileprovider";
    private final String FILE_NAME_FORMAT = "JPEG_";
    private final String FILE_FORMAT = ".jpg";
    private final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private static String mCurrentPhotoPath;

    public void dispatchTakePictureIntent(View button) {
        //SAVE!!!
        if(pathList.size()!=0) {
            for (LatLng ll : pathList) {
                pathListString += ll.latitude + "," + ll.longitude + "\n";
            }
            database.updateData(intent.getExtras().getInt("id"), pathListString);
        }

        if(markersMap.keySet().size()!=0) {
            for (Map.Entry<LatLng,MarkerContener> entry : markersMap.entrySet()) {


                markersString += entry.getKey().latitude +
                        "," + entry.getKey().longitude +
                        "," + entry.getValue().getPhotoPath() + "\n";

            }


            database.updateMarkers(intent.getExtras().getInt("id"), markersString);
        }
        //SAVE!!!



        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String imageFileName = FILE_NAME_FORMAT + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                FILE_FORMAT,         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();



        return image;
    }

    private final int PHOTO_SCALE = 10;

    //private static ImageView mImageView; //??!!

    public void setPic() {
        ImageView mImageView = (ImageView) findViewById(R.id.markerImageView);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Determine how much to scale down the image
        //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = PHOTO_SCALE;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }



    @Override
    protected void onStart() {
        if(mCurrentPhotoPath!=null) {
            setPic();
        }
            super.onStart();
    }

}

