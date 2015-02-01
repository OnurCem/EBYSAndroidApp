package ocs.com.ebys;

import java.util.List;

/**
 * Created by Onur Cem on 1/20/2015.
 */
public interface GetCoursesTaskListener {
    public abstract void onGetCoursesTaskStarted();
    public abstract void onGetCoursesTaskCompleted(ServerResult result);
}
