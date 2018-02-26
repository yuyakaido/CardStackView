package com.yuyakaido.android.cardstackview.sample;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class TouristSpotCardAdapter extends ArrayAdapter<TouristSpot> {

    public TouristSpotCardAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.item_tourist_spot_card, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        TouristSpot spot = getItem(position);

        holder.name.setText(spot.name);
        holder.city.setText(spot.city);

        List<String> posts = new ArrayList<>();
        posts.add(spot.url);
        posts.add(spot.url);
        posts.add(spot.url);
        posts.add(spot.url);

        UserPhotoAdapter adapter = new UserPhotoAdapter(getContext(), posts);
        holder.vpPhoto.setAdapter(adapter);
        holder.ci.setViewPager(holder.vpPhoto);

        MyCardContainerView container = (MyCardContainerView) parent.getParent();
        container.setViewPager(holder.vpPhoto);

        holder.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Go to info!", Toast.LENGTH_SHORT).show();
            }
        });

        return contentView;
    }

    private static class ViewHolder {
        public TextView name;
        public TextView city;
        public ImageView ivInfo;
        public ViewPager vpPhoto;
        public CircleIndicator ci;

        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.item_tourist_spot_card_name);
            this.city = (TextView) view.findViewById(R.id.item_tourist_spot_card_city);
            this.ivInfo = (ImageView) view.findViewById(R.id.iv_info);
            this.vpPhoto = (ViewPager) view.findViewById(R.id.vp_card);
            this.ci = (CircleIndicator) view.findViewById(R.id.vpi_photo);
        }
    }

}

