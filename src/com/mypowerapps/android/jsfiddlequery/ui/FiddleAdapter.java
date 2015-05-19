package com.mypowerapps.android.jsfiddlequery.ui;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mypowerapps.android.jsfiddlequery.R;

public class FiddleAdapter extends ArrayAdapter<Fiddle> implements Filterable {
	private FiddleFilter fiddleFilter;
	ArrayList<Fiddle> fiddleList;
	ArrayList<Fiddle> filteredList;
	
    public FiddleAdapter(Context context, ArrayList<Fiddle> list) {
    	super(context, 0, list);
    	
    	fiddleList = list;
    	filteredList = new ArrayList<Fiddle>();
    	getFilter();
    }
    
    @Override
    public int getCount() {
        return filteredList.size();
    }
    
    @Override
    public Fiddle getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Fiddle fiddle = getItem(position);
    	
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.listviewitem, parent, false);
        }
        
        TextView title = (TextView) convertView.findViewById(R.id.listview_title);
        TextView details = (TextView) convertView.findViewById(R.id.listview_details);
        TextView description = (TextView) convertView.findViewById(R.id.listview_description);
        
        String link = String.format("<a href=\"%1$s\">%2$s</a>", fiddle.getUrl(), fiddle.getTitle());
        Spanned htmlTitle = Html.fromHtml(link);
        title.setText(htmlTitle);
        details.setText(fiddle.getDetails());
        description.setText(fiddle.getDescription());
        
        return convertView;
    }
    
    @Override
    public Filter getFilter() {
        if (fiddleFilter == null) {
        	fiddleFilter = new FiddleFilter();
        }

        return fiddleFilter;
    }
    
    private class FiddleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Fiddle> results = new ArrayList<Fiddle>();
            if (constraint!= null && constraint.length() > 0) {
            	
                for (Fiddle fiddle : fiddleList) {
                    if (fiddle.getTitle().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                    	results.add(fiddle);
                    }
                }

                filterResults.count = results.size();
                filterResults.values = results;
            } else {
                filterResults.count = fiddleList.size();
                filterResults.values = fiddleList;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Fiddle>) results.values;
            notifyDataSetChanged();
        }
    }
}