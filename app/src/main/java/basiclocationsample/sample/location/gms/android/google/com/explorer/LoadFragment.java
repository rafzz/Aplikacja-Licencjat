package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;

public class LoadFragment extends AppCompatActivity implements OnMapReadyCallback{

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragment);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment2);
        mapFragment.getMapAsync(this);


        loadDB();

    }

    ViewGroup layout;

    public void loadDB(){
        int id;
        String name;
        double lat;
        double lng;
        float zoom;
        LatLng target;



        database = new Database(this);
        Cursor cursor = database.writeAllData();

        layout = (ViewGroup) findViewById(R.id.tableLayout);

        while(cursor.moveToNext()){
            id = cursor.getInt(0);
            name = cursor.getString(1);
            lat = cursor.getDouble(2);
            lng = cursor.getDouble(3);
            zoom = cursor.getFloat(4);
            target = new LatLng(lat,lng);

            TextView tv = new TextView(this);

            tv.setText(id+name+"_"+String.valueOf(zoom)+"_"+target.toString());

            TableRow tableRow = new TableRow(this);
            tableRow.addView(tv);

            layout.addView(tableRow);



        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);


    }
}
