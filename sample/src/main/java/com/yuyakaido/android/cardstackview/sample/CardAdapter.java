package com.yuyakaido.android.cardstackview.sample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardAdapter extends ArrayAdapter<String> {

    public CardAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        if (contentView == null) {
            contentView = View.inflate(getContext(), R.layout.item_card_stack, null);
        }

        TextView v = (TextView) (contentView.findViewById(R.id.item_card_stack_text));
        v.setText(getItem(position));

        return contentView;
    }

}

