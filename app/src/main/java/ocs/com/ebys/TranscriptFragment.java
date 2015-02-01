package ocs.com.ebys;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.params.CoreConnectionPNames;

import java.util.HashMap;
import java.util.List;

public class TranscriptFragment extends Fragment
        implements GetTranscriptTaskListener {

    private ProgressDialog progressDialog;
    private ExpandableListView listView;
    private TranscriptListAdapter adapter;
    private List<Transcript> transcripts;
    private HashMap<Transcript, List<Course>> courses;
    private static DataStore dataStore;

    public static TranscriptFragment newInstance() {
        TranscriptFragment f = new TranscriptFragment();
        dataStore = DataStore.getInstance();
        return f;
    }

    public TranscriptFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transcript, container, false);

        getActivity().setTitle("Transkript");

        listView = (ExpandableListView) rootView.findViewById(R.id.lv_transcript);

        transcripts = dataStore.getTranscripts();
        courses = dataStore.getTranscriptCourses();

        if (transcripts != null && courses != null) {
            adapter = new TranscriptListAdapter(getActivity(), transcripts, courses);
            listView.setAdapter(adapter);
        } else {
            getTranscript();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        if (searchMenuItem != null) {
            searchMenuItem.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onGetTranscriptTaskStarted() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Transkript yükleniyor...");
        progressDialog.show();
    }

    @Override
    public void onGetTranscriptTaskCompleted(ServerResult result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (result.isSuccess()) {
            transcripts = (List<Transcript>) result.getData();
            courses = new HashMap<Transcript, List<Course>>();

            for (Transcript t : transcripts) {
                courses.put(t, t.getCourses());
            }

            dataStore.saveTranscripts(transcripts);
            dataStore.saveTranscriptCourses(courses);
            adapter = new TranscriptListAdapter(getActivity(), transcripts, courses);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void filterList(String query) {
        for (int i = 0; i < listView.getCount(); i++) {
            listView.collapseGroup(i);
        }
        adapter.getFilter().filter(query);
    }

    private void getTranscript() {
        EBYSController.getInstance().getTranscript(this);
    }
}
