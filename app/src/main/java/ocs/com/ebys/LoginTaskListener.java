package ocs.com.ebys;

/**
 * Created by Onur Cem on 1/19/2015.
 */
public interface LoginTaskListener {
    public abstract void onLoginTaskStarted();
    public abstract void onLoginTaskCompleted(ServerResult result);
}
