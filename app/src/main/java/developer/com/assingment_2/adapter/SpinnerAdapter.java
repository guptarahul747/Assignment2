package developer.com.assingment_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import developer.com.assingment_2.model.TransportModel;

/**
 * Created by Rahul on 9/3/16.
 */
public class SpinnerAdapter extends ArrayAdapter<TransportModel> {

    Context context;
    ArrayList<TransportModel> transportModels;

    public SpinnerAdapter(Context context, ArrayList<TransportModel> list) {
        super(context, 0);

        this.context = context;
        this.transportModels = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return getSpinnerView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getSpinnerView(position, convertView, parent);
    }

    View getSpinnerView(int position, View convertView, ViewGroup parent) {

        TransportModel transportModel = transportModels.get(position);
        View view = LayoutInflater.from(context).
                inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(transportModel.getName());
        return view;
    }

    @Override
    public int getCount() {
        return transportModels.size();
    }
}
