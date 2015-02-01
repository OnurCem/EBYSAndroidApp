package ocs.com.ebys;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Onur Cem on 1/19/2015.
 */
public class CourseFragment extends Fragment
        implements GetCoursesTaskListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ProgressDialog progressDialog;
    private ExpandableListView listView;
    private CourseListAdapter adapter;
    private List<Course> courses;
    private HashMap<Course, List<Grade>> grades;
    private static DataStore dataStore;

    public static CourseFragment newInstance() {
        CourseFragment f = new CourseFragment();
        dataStore = DataStore.getInstance();
        return f;
    }

    public CourseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_course, container, false);

        getActivity().setTitle("Notlarım");

        listView = (ExpandableListView) rootView.findViewById(R.id.lv_courses);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        mNavigationDrawerFragment.setUserProfile(EBYSController.getUser());
        courses = dataStore.getCourses();
        grades = dataStore.getGrades();

        if (courses != null && grades != null) {
            adapter = new CourseListAdapter(getActivity(), courses, grades);
            listView.setAdapter(adapter);
        } else {
            getCourses();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        listView.setAdapter(adapter);
    }

    @Override
    public void onGetCoursesTaskStarted() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Notlar yükleniyor...");
        progressDialog.show();
    }

    @Override
    public void onGetCoursesTaskCompleted(ServerResult result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (result.isSuccess()) {
            mNavigationDrawerFragment.setUserProfile(EBYSController.getUser());
            courses = (List<Course>) result.getData();
            grades = new HashMap<Course, List<Grade>>();

            for (Course c : courses) {
                grades.put(c, c.getGrades());
            }

            dataStore.saveCourses(courses);
            dataStore.saveGrades(grades);
            adapter = new CourseListAdapter(getActivity(), courses, grades);
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

    private void getCourses() {
        EBYSController.getInstance().getCourses(this);
    }
}
