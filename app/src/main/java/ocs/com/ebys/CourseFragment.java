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
        implements OnGetCoursesTaskCompleted {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ProgressDialog progressDialog;
    private ExpandableListView listView;
    private CourseListAdapter adapter;
    private List<Course> courses;
    private HashMap<Course, List<Grade>> grades;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_course, container, false);
        listView = (ExpandableListView) rootView.findViewById(R.id.lv_courses);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Notlar yükleniyor...");
        progressDialog.show();

        EBYSController ebysCtrl = EBYSController.getInstance();
        ebysCtrl.getCourses(this);
        grades = new HashMap<Course, List<Grade>>();

        return rootView;
    }

    @Override
    public void onGetCoursesTaskCompleted(ServerResult result) {
        if (result.isSuccess()) {
            mNavigationDrawerFragment.setUserProfile(EBYSController.getUser());
            courses = (List<Course>) result.getData();
            for (Course c : courses) {
                grades.put(c, c.getGrades());
            }

            adapter = new CourseListAdapter(getActivity(), courses, grades);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void filterList(String query) {
        for (int i = 0; i < listView.getCount(); i++) {
            listView.collapseGroup(i);
        }
        adapter.getFilter().filter(query);
    }
}
