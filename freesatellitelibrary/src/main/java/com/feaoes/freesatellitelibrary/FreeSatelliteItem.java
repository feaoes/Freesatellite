package com.feaoes.freesatellitelibrary;

import android.view.animation.Animation;
import android.widget.ImageView;

/**
 *
 * Created by ff on 2015/12/16.
 */
public class FreeSatelliteItem {

    public FreeSatelliteItem(int id, int level, int imgResourceId) {
        this.id = id;
        this.level = level;
        this.imgResourceId = imgResourceId;
    }

    private int level;

    private int id;
    private int imgResourceId;
    private ImageView view;
    private Animation outAnimation;
    private Animation inAnimation;
    private Animation clickAnimation;
    private int finalX;
    private int finalY;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgResourceId() {
        return imgResourceId;
    }

    public void setImgResourceId(int imgResourceId) {
        this.imgResourceId = imgResourceId;
    }


    void setView(ImageView view) {
        this.view = view;
    }

    ImageView getView() {
        return view;
    }

    void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    Animation getInAnimation() {
        return inAnimation;
    }

    void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    Animation getOutAnimation() {
        return outAnimation;
    }

    void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    int getFinalX() {
        return finalX;
    }

    int getFinalY() {
        return finalY;
    }


    void setClickAnimation(Animation clickAnim) {
        this.clickAnimation = clickAnim;
    }

    Animation getClickAnimation() {
        return clickAnimation;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
