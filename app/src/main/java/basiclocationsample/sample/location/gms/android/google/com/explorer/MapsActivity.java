package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng target;
    private float zoom;

    private Database database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
    }

    private TextView tv;
    private Button saveButton;

    public void snapMapFragment(View view){

        tv = (TextView) findViewById(R.id.setMapName);
        saveButton = (Button) findViewById(R.id.Save);

        database = new Database(this);
        /*
        database.addData(mMap.getCameraPosition().target.latitude,
                mMap.getCameraPosition().target.longitude,
                mMap.getCameraPosition().zoom);
         */
        target=mMap.getCameraPosition().target;
        zoom=mMap.getCameraPosition().zoom;

        tv.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
    }

    public void saveMapFragment(View view){

        database.addData(target.latitude, target.longitude, zoom);
        tv.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);

    }






    public void loadMapFragment(View view){
        Intent intent  = new Intent(this,LoadFragment.class);
        startActivity(intent);
        /*
        int id;
        double lat;
        double lng;
        float zoom =14;
        LatLng target = mMap.getCameraPosition().target;

        database = new Database(this);
        Cursor cursor = database.writeAllData();

        while(cursor.moveToNext()){
            id = cursor.getInt(0);
            lat = cursor.getDouble(1);
            lng = cursor.getDouble(2);
            zoom = cursor.getFloat(3);
            target = new LatLng(lat,lng);

        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, zoom));
        */
    }




}
