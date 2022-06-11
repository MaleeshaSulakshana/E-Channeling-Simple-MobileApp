package com.example.e_channelling.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.e_channelling.Constructors.Doctors;
import com.example.e_channelling.R;

import java.util.ArrayList;

public class DoctorAdapter extends ArrayAdapter<Doctors> {

    private Context mContext;
    private int mResource;

    public DoctorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Doctors> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView doctorName = convertView.findViewById(R.id.doctorName);
        TextView doctorStatus = convertView.findViewById(R.id.doctorStatus);
        TextView doctorId = convertView.findViewById(R.id.doctorId);

        doctorName.setText(getItem(position).getDoctorName());
        doctorId.setText(getItem(position).getDoctorId());
        doctorStatus.setText(getItem(position).getDoctorStatus());

        return convertView;
    }

}