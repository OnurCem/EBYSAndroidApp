package ocs.com.ebys;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Onur Cem on 2/1/2015.
 */
public class TranscriptListAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    private List<Transcript> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<Transcript, List<Course>> listDataChild;
    private List<Transcript> filtered;
    private TransciptFilter filter;

    public TranscriptListAdapter(Context context, List<Transcript> listDataHeader,
                             HashMap<Transcript, List<Course>> listChildData) {
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

        final String courseName = ((Course) getChild(groupPosition, childPosition)).getName();
        final String courseCredit = ((Course) getChild(groupPosition, childPosition)).getCredit();
        final String courseGrade = ((Course) getChild(groupPosition, childPosition)).getLetterGrade();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.transcript_list_item, null);
        }

        TextView txtCourseName = (TextView) convertView.findViewById(R.id.transcript_course_name);
        TextView txtCourseCredit = (TextView) convertView.findViewById(R.id.transcript_course_credit);
        TextView txtCourseGrade = (TextView) convertView.findViewById(R.id.transcript_course_grade);

        txtCourseName.setText(courseName);
        txtCourseCredit.setText(courseCredit);
        txtCourseGrade.setText(courseGrade);
        txtCourseGrade.setTextColor(Color.parseColor(getColorByGrade(courseGrade)));

        if (courseName.contains("GNO")) {
            txtCourseName.setGravity(Gravity.RIGHT);
            txtCourseName.setTypeface(null, Typeface.BOLD);
            txtCourseName.setPadding(0, 0, 20, 0);
            txtCourseCredit.setVisibility(View.GONE);
            txtCourseGrade.setVisibility(View.GONE);
        } else {
            txtCourseName.setGravity(Gravity.CENTER_VERTICAL);
            txtCourseName.setTypeface(null, Typeface.NORMAL);
            txtCourseName.setPadding(0, 0, 0, 0);
            txtCourseCredit.setVisibility(View.VISIBLE);
            txtCourseGrade.setVisibility(View.VISIBLE);
        }

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

        final Transcript transcript = (Transcript) getGroup(groupPosition);
        final String header = transcript.getHeader();
        final String cGNO = transcript.getcGNO();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.transcript_list_group, null);
        }

        TextView txtTranscriptHeader = (TextView) convertView.findViewById(R.id.transcript_header);
        TextView txtTranscriptGNO = (TextView) convertView.findViewById(R.id.transcript_cGNO);

        txtTranscriptHeader.setText(header);
        txtTranscriptGNO.setText(cGNO);

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
            filter = new TransciptFilter();
        }

        return filter;
    }

    private class TransciptFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<Transcript> tempList = new ArrayList<>();

                // search content in friend list
                for (Transcript t : listDataHeader) {
                    for (Course c : t.getCourses()) {
                        if (c.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tempList.add(t);
                        }
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
            filtered = (List<Transcript>) results.values;
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