package ocs.com.ebys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class CourseListAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    private List<Course> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<Course, List<Grade>> listDataChild;
    private List<Course> filtered;
    private CourseFilter filter;

    public CourseListAdapter(Context context, List<Course> listDataHeader,
                             HashMap<Course, List<Grade>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        filtered = listDataHeader;
        getFilter();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.filtered.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String gradeName = ((Grade) getChild(groupPosition, childPosition)).getName();
        final String gradeValue = ((Grade) getChild(groupPosition, childPosition)).getValue();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_list_item, null);
        }

        TextView txtGradeName = (TextView) convertView.findViewById(R.id.grade_name);
        TextView txtGradeValue = (TextView) convertView.findViewById(R.id.grade_value);

        txtGradeName.setText(gradeName + ": ");
        txtGradeValue.setText(gradeValue);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.filtered.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.filtered.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.filtered.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        Course course = (Course) getGroup(groupPosition);
        String courseName = course.getName();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_list_group, null);
        }

        TextView txtCourseName = (TextView) convertView.findViewById(R.id.course_name);
        TextView txtCourseGrade = (TextView) convertView.findViewById(R.id.course_grade);
        txtCourseName.setText(courseName);
        txtCourseGrade.setText(course.getLetterGrade());
        txtCourseGrade.setTextColor(Color.parseColor(getColorByGrade(course.getLetterGrade())));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Get custom filter
     * @return filter
     */
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CourseFilter();
        }

        return filter;
    }

    private class CourseFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<Course> tempList = new ArrayList<Course>();

                // search content in friend list
                for (Course c : listDataHeader) {
                    if (c.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(c);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = listDataHeader.size();
                filterResults.values = listDataHeader;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            filtered = (List<Course>) results.values;
            notifyDataSetChanged();
        }
    }

    public String getColorByGrade(String grade) {
        if (grade.equals("AA"))
            return "#4CAF50";
        if (grade.equals("BA"))
            return "#3F51B5";
        if (grade.equals("BB"))
            return "#FFC107";
        if (grade.equals("CB"))
            return "#FF9800";
        if (grade.equals("CC"))
            return "#795548";
        if (grade.equals("FD"))
            return "#D50000";
        else
            return "#9E9E9E";
    }
}