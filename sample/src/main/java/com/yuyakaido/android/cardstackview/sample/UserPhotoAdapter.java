package com.yuyakaido.android.cardstackview.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;


/**
 * Created by luongvo on 2/26/18.
 */

public class UserPhotoAdapter extends PagerAdapter {

    private Context context;
    private List<String> posts;

    public UserPhotoAdapter(@NonNull Context context, @NonNull List<String> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return this.posts.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_user_photo, container, false);

        String url = posts.get(position);
        Glide.with(view.getContext()).load(url).into((ImageView) view.findViewById(R.id.iv_photo));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
