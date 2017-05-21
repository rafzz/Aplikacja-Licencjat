package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng target;
    private float zoom;
    private Database database;

    private TextView mapName;
    private Button saveButton;


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

    public void snapMapFragment(View view){

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_maps);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);

        layout.startAnimation(fadeIn);

        mapName = (TextView) findViewById(R.id.setMapName);
        saveButton = (Button) findViewById(R.id.Save);

        database = new Database(this);

        target=mMap.getCameraPosition().target;
        zoom=mMap.getCameraPosition().zoom;

        mapName.setVisibility(View.VISIBLE);
        Geocoder gc = new Geocoder(this);

        Address ad;
        try {
            ad = gc.getFromLocation(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude(),1).get(0);
            mapName.setText(ad.getLocality()+", "+ad.getAddressLine(0));
        } catch (Exception e) {
            mapName.setText("no name");
        }

        saveButton.setVisibility(View.VISIBLE);
        findViewById(R.id.closePanel).setVisibility(View.VISIBLE);



    }

    public void searchAddress(View view)
    {
        EditText searchEditView = (EditText)findViewById(R.id.searchEditView);

        String locationName = searchEditView.getText().toString();

        List<Address> addressList = null;
        if(locationName != null || !locationName.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locationName , 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude() , address.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        }
    }

    public void saveMapFragment(View view){

        database.addData(target.latitude, target.longitude, zoom, mapName.getText().toString());
        findViewById(R.id.closePanel).setVisibility(View.INVISIBLE);
        mapName.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
    }

    public void closeNamePanel(View view){
        findViewById(R.id.closePanel).setVisibility(View.INVISIBLE);
        mapName.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
    }

    public void loadMapFragment(View view){

        Intent intent  = new Intent(this,LoadFragment.class);
        startActivity(intent);

        if( mapName != null && mapName.getVisibility()==View.VISIBLE ){

            mapName.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.INVISIBLE);

        }

    }

}
