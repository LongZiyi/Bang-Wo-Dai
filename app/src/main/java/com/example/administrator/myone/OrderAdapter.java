package com.example.administrator.myone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class OrderAdapter extends ArrayAdapter<Order1> {

    private int resourceId;

    public OrderAdapter(Context context, int textViewResourceId, List<Order1> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order1 order = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        ImageView orderImage = (ImageView) view.findViewById(R.id.order_image);
        TextView orderName = (TextView) view.findViewById(R.id.order_name);
        TextView orderAddress = (TextView) view.findViewById(R.id.order_address);
        orderImage.setImageResource(order.getImage());
        orderName.setText(order.getName());
        orderAddress.setText(order.getAddress());
        return view;
    }
}
