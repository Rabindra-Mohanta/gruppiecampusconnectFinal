package school.campusconnect.utils.address;

import android.location.Location;

/**
 * Created by frenzin07 on 9/29/2018.
 */

public interface LocationInterface {

    public void locationUpdate(Location location);
    public void startLocationTimer();

}
