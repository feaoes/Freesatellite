package com.feaoes.freesatellite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import com.feaoes.freesatellitelibrary.FreeSatelliteItem;
import com.feaoes.freesatellitelibrary.FreeSatelliteView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FreeSatelliteView freeSV = (FreeSatelliteView) findViewById(R.id.freeSV);

            FreeSatelliteItem item1 = new FreeSatelliteItem(1, 0, R.drawable.a1);
            FreeSatelliteItem item2 = new FreeSatelliteItem(2, 0, R.drawable.a1);
            FreeSatelliteItem item3 = new FreeSatelliteItem(3, 0, R.drawable.a1);
            FreeSatelliteItem item4 = new FreeSatelliteItem(4, 0, R.drawable.a1);
            FreeSatelliteItem item5 = new FreeSatelliteItem(5, 0, R.drawable.a1);
            FreeSatelliteItem item6 = new FreeSatelliteItem(6, 0, R.drawable.a1);

        ArrayList<FreeSatelliteItem> list = new ArrayList<>();
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        list.add(item5);
        list.add(item6);

        freeSV.addItems(this, list);
        freeSV.setOnItemImgClickListener(new FreeSatelliteView.OnItemImgClickListener() {
            @Override
            public void onClick(int id, int level, View v) {

            }
        });
    }

    public void startAnimation(View v){

        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setStartOffset(0);
        rotateAnimation.setDuration(400);
        TranslateAnimation translateAnimation = new TranslateAnimation( 0,  100,
               0, 100
        );
        translateAnimation.setStartOffset(0);
        translateAnimation.setDuration(400);

        set.addAnimation(rotateAnimation);
        set.addAnimation(translateAnimation);

        set.setFillAfter(true);
        v.startAnimation(set);
    }
}
