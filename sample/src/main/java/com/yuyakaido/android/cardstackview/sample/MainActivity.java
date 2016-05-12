package com.yuyakaido.android.cardstackview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yuyakaido.android.cardstackview.CardStackView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardAdapter adapter = new CardAdapter(getApplicationContext());
        for (int i = 0; i < 20; i++) {
            adapter.add(String.valueOf(i));
        }
        final CardStackView cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack);
        cardStackView.setAdapter(adapter);

        View button = findViewById(R.id.activity_main_reverse_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStackView.reverse();
            }
        });
    }

}
