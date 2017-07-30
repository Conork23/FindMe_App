package com.intimealarm.findme.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.intimealarm.findme.MainActivity;
import com.intimealarm.findme.Models.DeviceLocation;
import com.intimealarm.findme.R;
import com.intimealarm.findme.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 23/02/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>  {
    Context context;
    ArrayList<DeviceLocation> dataset;
    DatabaseReference db;
    int viewedPos = -1;

    public LocationAdapter(Context context, DatabaseReference db ) {
        this.context = context;
        this.db = db;
        this.dataset = new ArrayList<>();
        this.db.limitToLast(Constants.MAX_LOCATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateAll(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public DeviceLocation getLatest(){
        return (getItemCount() > 0)? dataset.get(0): null;
    }

    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DeviceLocation location = dataset.get(position);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(location.getTime());
        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_time_format));

        if(TextUtils.isEmpty(location.getLable())){
            holder.noAddressTv.setVisibility(View.VISIBLE);
            holder.addressTv.setText(location.getLat()+", "+location.getLng());
        }else{
            holder.noAddressTv.setVisibility(View.GONE);
            holder.addressTv.setText(location.getLable());

        }
        holder.datetimeTv.setText(format.format(c.getTime()));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.updateMap(location);
            }
        });

        animateView(holder.rootView, position);
    }

    private void animateView(View view, int position) {
        if (position > viewedPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            animation.setInterpolator(context, android.R.anim.decelerate_interpolator);
            view.startAnimation(animation);
            viewedPos = position;

        }

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.rootView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    private void updateAll(DataSnapshot dataSnapshot){

        dataset.clear();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            Map m = (Map) data.getValue();
            DeviceLocation l = new DeviceLocation(
                    (Double) m.get("lat"),
                    (Double) m.get("lng"),
                    (Long) m.get("time"),
                    (String) m.get("lable"));
            add(l);
        }

        this.notifyDataSetChanged();
    }

    public void add(DeviceLocation l) {
        dataset.add(0,l);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.addresslbl)
        TextView addressTv;

        @BindView(R.id.datetimelbl)
        TextView datetimeTv;

        @BindView(R.id.noAddressLbl)
        TextView noAddressTv;

        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rootView = itemView;
        }
    }

}
