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
        ViewHolder holder;

        if (contentView == null) {
            contentView = View.inflate(getContext(), R.layout.item_card_stack, null);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        holder.textView.setText(getItem(position));

        return contentView;
    }

    private static class ViewHolder {
        public TextView textView;

        public ViewHolder(View view) {
            this.textView = (TextView) view.findViewById(R.id.item_card_stack_text);
        }
    }

}

