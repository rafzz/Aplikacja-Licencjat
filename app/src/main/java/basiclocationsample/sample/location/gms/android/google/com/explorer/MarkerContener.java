package basiclocationsample.sample.location.gms.android.google.com.explorer;

/**
 * Created by rafzz on 30.04.2017.
 */

public class MarkerContener {

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String photoPath;
    private String description;

    public MarkerContener(String description, String photoPath) {
        this.description = description;
        this.photoPath = photoPath;
    }

    public MarkerContener() {
    }
}
