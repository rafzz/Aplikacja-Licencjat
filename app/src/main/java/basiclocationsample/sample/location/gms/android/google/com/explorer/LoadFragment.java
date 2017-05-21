package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.model.people.Person;

public class LoadFragment extends AppCompatActivity implements OnMapReadyCallback{

    private Database database;
    private ViewGroup layout;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragment);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment2);
        mapFragment.getMapAsync(this);

        loadDB();

    }

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

            RadioButton radioButton = new RadioButton(this);
            radioButton.setTextSize(50);
            radioButton.setText(name);
            radioButton.setOnClickListener(onClickListener);

            Bundle bundle = new Bundle();
            bundle.putDouble("lat",lat);
            bundle.putDouble("lng",lng);
            bundle.putFloat("zoom",zoom);


            radioButton.setTag(bundle);
            radioButton.setId(id);

            layout.addView(radioButton);


        }
    }

    private final int  animationDuration = 1000;
    private final int animFromAlpha = 0;
    private final int animToAlpha = 1;

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        public void onClick(View view) {

            view.setSelected(true);
            Animation fadeIn = new AlphaAnimation(animFromAlpha, animToAlpha);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(animationDuration);
            view.startAnimation(fadeIn);

            Bundle bundle = (Bundle) view.getTag();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(bundle.getDouble("lat"),
                            bundle.getDouble("lng")),
                    bundle.getFloat("zoom")));

        }
    };

    public void loadMapToRecordActivity(View view){

        Intent intent = new Intent(this, RecordMapsActivity.class);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.tableLayout);
        intent.putExtra("id",radioGroup.getCheckedRadioButtonId());

       if(radioGroup.getCheckedRadioButtonId()!=-1){ startActivity(intent); }

    }

    public void deleteMap(View view){
        database = new Database(this);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.tableLayout);
        database.removeData(radioGroup.getCheckedRadioButtonId());
        radioGroup.removeView(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
        radioGroup.refreshDrawableState();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
    }
}
