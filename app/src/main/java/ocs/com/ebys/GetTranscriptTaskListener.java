package ocs.com.ebys;

/**
 * Created by Onur Cem on 2/1/2015.
 */
public interface GetTranscriptTaskListener {
    public abstract void onGetTranscriptTaskStarted();
    public abstract void onGetTranscriptTaskCompleted(ServerResult result);
}
