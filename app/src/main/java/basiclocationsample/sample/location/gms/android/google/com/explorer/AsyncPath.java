package basiclocationsample.sample.location.gms.android.google.com.explorer;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by rafzz on 14.05.2017.
 */

public class AsyncPath extends AsyncTask<Void,Location,Void> {

    private GoogleMap mMap;

    public AsyncPath(GoogleMap mMap) {
        this.mMap=mMap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Location... values) {
        super.onProgressUpdate(values);
    }


}
