package com.feaoes.freesatellitelibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 卫星菜单的View
 * 1.可以设置itemWidth,itemHeight,mainImgWidth,mainImgHeight
 * 2.可以设置动画是否可以中断。
 * 3.指定半径是多少，可以设置是四分之一圆，还是二分之一。
 * 4.可以设置View的位置，根据View的位置来进行分配布局
 * 5.动画执行的时间
 * 6.点击卫星的回调方法
 * 7.可以设置点击图片的资源
 * 8.
 * 9.加速器的设计
 * 10.显示的弧度是多少
 * 11.
 * Created by ff on 2015/12/15.
 */
public class FreeSatelliteView extends FrameLayout {

    ArrayList<FreeSatelliteItem> menuItems = new ArrayList<>();
    private boolean interceptAnim;
    private float radius;
//    private Drawable mainImg;
    private int animDuration;
    private int content_gravity;

    private final int F_LEFT = 0x01;
    private final int F_TOP = 0x02;
    private final int F_RIGTH = 0x04;
    private final int F_BOTTOM = 0x08;

    private final float offsetRatio = 0.2f;
    private int endDegree;
    private int startDegree;
    private float mainImgRadius;
    private float itemImgRadius;
    private ImageView mainImageView;

    private boolean isExpand = false;
    private GravityType gravityType;
    private int mainImgWidth;
    private int mainImgHeight;
    private int itemImgWidth;
    private int itemImgHeight;

    enum GravityType{
        LEFT,RIGHT,TOP,BOTTOM,
        L2B,L2T,R2B,R2T
    }
    public FreeSatelliteView(Context context) {
        super(context);
        init(context, null);
    }

    public FreeSatelliteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FreeSatelliteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void init(Context context, AttributeSet attrs) {
        mainImageView = new ImageView(context);
        mainImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if(attrs==null){
            return ;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FreeSatelliteView);

        interceptAnim = typedArray.getBoolean(R.styleable.FreeSatelliteView_interceptAnim, false);
        radius = typedArray.getDimension(R.styleable.FreeSatelliteView_radius, dip2px(context, 200));
        int resourceId = typedArray.getResourceId(R.styleable.FreeSatelliteView_mainImg, -1);
        animDuration = typedArray.getInt(R.styleable.FreeSatelliteView_animDuration, 300);
        content_gravity = typedArray.getInt(R.styleable.FreeSatelliteView_content_gravity, 2);

        mainImgWidth = (int) typedArray.getDimension(R.styleable.FreeSatelliteView_mainImgWidth, dip2px(context, 40));
        mainImgHeight = (int) typedArray.getDimension(R.styleable.FreeSatelliteView_mainImgHeight, dip2px(context, 40));
        itemImgWidth = (int) typedArray.getDimension(R.styleable.FreeSatelliteView_itemImgWidth, dip2px(context, 4));
        itemImgHeight = (int) typedArray.getDimension(R.styleable.FreeSatelliteView_itemImgHeight , dip2px(context, 40));

        setGravityType(content_gravity);
        int mainImgGravity = getMainImgGravity();
        mainImageView.setLayoutParams(new LayoutParams(mainImgWidth, mainImgHeight, mainImgGravity));

        mainImageView.setImageBitmap(MBitmapUtils.decodeSampledBitmapFromResource(context.getResources(), resourceId, mainImgWidth, mainImgHeight));
        addView(mainImageView);
    }


    private Animation getMainImgOutAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        return rotateAnimation;
    }
    private Animation getMainImgInAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        return rotateAnimation;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setGravityType(content_gravity);
        int width = 0;
        int height = 0;
        if(menuItems.size()>0){
            int viewW = itemImgWidth;
            int viewH = itemImgHeight;
            width = generateMeasureWidth(mainImgWidth, viewW, radius);
            height = generateMeasureHeight(mainImgHeight, viewH, radius);

            setMeasuredDimension((int)(width+radius * offsetRatio),(int)(height + radius * offsetRatio));
        }
    }




    public interface OnItemImgClickListener{
        void onClick(int id, int level, View v);
    }

    /**
     * 设置ItemImg的监听
     * @param listener
     */
    public void setOnItemImgClickListener(final OnItemImgClickListener listener){
        for (final FreeSatelliteItem item :menuItems){
            item.getView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(item.getId(), item.getLevel(), v);
                    item.getView().startAnimation(item.getClickAnimation());
                }
            });
        }

    }

    /**
     * 增加ItemImg
     * @param context
     * @param list
     */
    public void addItems(Context context,List<FreeSatelliteItem> list){
        menuItems.addAll(list);
        removeView(mainImageView);
        if(menuItems.size()==0){
            return;
        }
        for (FreeSatelliteItem item: list){
            ImageView itemView = new ImageView(context);
            itemView.setScaleType(ImageView.ScaleType.FIT_XY);
            itemView.setImageBitmap(MBitmapUtils.decodeSampledBitmapFromResource(context.getResources(), item.getImgResourceId(), itemImgWidth, itemImgHeight));
            item.setView(itemView);
            itemView.setVisibility(View.GONE);
        }
        setItemsLocationAndAnimation(context, menuItems, radius);//setup 1
        mainImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpand) {
                    for (FreeSatelliteItem item : menuItems) {
                        item.getView().setVisibility(View.VISIBLE);
                        item.getView().startAnimation(item.getOutAnimation());
                        mainImageView.startAnimation(getMainImgOutAnimation());
                    }
                    isExpand = true;
                } else {

                    for (FreeSatelliteItem item : menuItems) {
                        item.getView().setVisibility(View.VISIBLE);
                        item.getView().startAnimation(item.getInAnimation());
                        mainImageView.startAnimation(getMainImgInAnimation());
                    }
                    isExpand = false;
                }
            }
        });

        for (FreeSatelliteItem item :menuItems){
            addView(item.getView());
        }
        addView(mainImageView);

    }

    private void setItemsLocationAndAnimation(Context context, ArrayList<FreeSatelliteItem> menuItems, float radius) {

        int width = mainImgWidth;
        int height = mainImgHeight;
        float itemDegree;
        if(menuItems.size()==1){
            itemDegree = endDegree-startDegree;
        }else{
            itemDegree = (endDegree-startDegree)*1.0f / (menuItems.size()-1);
        }
        for (int i = 0; i < menuItems.size(); i++) {
            FreeSatelliteItem item = menuItems.get(i);
            ImageView itemView = item.getView();
            setEndPoint((float) ((startDegree+itemDegree*i)/360*2*Math.PI), item, radius);

            LayoutParams marginLayoutParams = new LayoutParams(itemImgWidth,itemImgHeight);
            setLayoutParamsByGravity(item,marginLayoutParams,width,height,itemImgWidth,itemImgWidth);
            itemView.setLayoutParams(marginLayoutParams);


            setItemAnimation(item);
        }

    }

    /**
     * 设置itemImg的动画
     * @param item
     */
    private void setItemAnimation(final FreeSatelliteItem item) {
        //outsiding mainImg Animation
        AnimationSet outSet = new AnimationSet(interceptAnim);
        TranslateAnimation translateAnimation = new TranslateAnimation( -item.getFinalX(),0 , item.getFinalY(),0 );
        translateAnimation.setDuration(animDuration);
        translateAnimation.setInterpolator(new OvershootInterpolator(2));

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setStartOffset(0);
        rotateAnimation.setDuration(animDuration);

        outSet.addAnimation(rotateAnimation);
        outSet.addAnimation(translateAnimation);
        outSet.setDuration(animDuration);

        outSet.setFillAfter(true);
        outSet.setStartOffset(item.getId() * 30);
        outSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                item.getView().setClickable(false);
                item.getView().setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.getView().setClickable(true);
                item.getView().setEnabled(true);
                item.getView().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        item.setOutAnimation(outSet);


        //insiding mainImg Animation
        AnimationSet inSet = new AnimationSet(interceptAnim);

        inSet.addAnimation(new RotateAnimation(360, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        inSet.addAnimation(new TranslateAnimation(0,
                -item.getFinalX(), 0,
                item.getFinalY()));



        inSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                item.getView().setClickable(false);
                item.getView().setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.getView().setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        inSet.setFillAfter(true);
        inSet.setDuration(animDuration);
        item.setInAnimation(inSet);

        //ItemClick Animation
        AnimationSet clickSet = new AnimationSet(interceptAnim);

        clickSet.addAnimation(new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        clickSet.addAnimation(new AlphaAnimation(1.0f, 0));

        clickSet.setDuration(animDuration);
        item.setClickAnimation(clickSet);


    }

    /**
     * 设置itemImg的endPoint，用于控制动画的起始点和结束点
     * @param itemDegree
     * @param item
     * @param radius
     */
    private void setEndPoint(float itemDegree, FreeSatelliteItem item, float radius) {
        int transY = (int) Math.round(Math.sin(itemDegree) * radius);
        int transX = (int) Math.round(Math.cos(itemDegree) * radius);

        item.setFinalX(transX);
        item.setFinalY(transY);

    }

    /**
     * 设置itemImg的位置，利用Margin来进行设置
     * @param item
     * @param marginLayoutParams
     * @param mainImgWidth
     * @param mainImgHeight
     * @param itemWidth
     * @param itemHeight
     */
    private void setLayoutParamsByGravity(FreeSatelliteItem item, LayoutParams marginLayoutParams, int mainImgWidth, int mainImgHeight, int itemWidth, int itemHeight) {
        int width = generateMeasureWidth(mainImgWidth, itemWidth,radius);
        int height = generateMeasureHeight(mainImgHeight,itemHeight,radius);

        width = (int) (width+radius*offsetRatio);
        height = (int) (height+radius*offsetRatio);
        int finalX = item.getFinalX();
        int finalY = item.getFinalY();
        int left =0;
        int top =0;
        int right =0;
        int bottom =0;

        switch (gravityType){
            case LEFT:
                left = (mainImgWidth - itemWidth) / 2;
                top = (height-itemHeight)/2;
                break;
            case RIGHT:
                left = (width-itemWidth-(mainImgWidth-itemWidth)/2);
                top = (height-itemHeight)/2;
                break;
            case TOP:
                left = (width-itemWidth)/2;
                top = (mainImgHeight - itemHeight) / 2;
                break;
            case BOTTOM:
                left = (width-itemWidth)/2;
                top = (height-itemHeight -(mainImgHeight-itemHeight)/2);
                break;
            case L2T:
                left = (mainImgWidth-itemWidth)/2;
                top = (mainImgHeight-itemHeight)/2;
                break;
            case L2B:
                left = (mainImgWidth-itemWidth)/2;
                top = (height-itemHeight -(mainImgHeight-itemHeight)/2);
                break;
            case R2B:
                left = (width-itemWidth-(mainImgWidth-itemWidth)/2);
                top = (height-itemHeight -(mainImgHeight-itemHeight)/2);
                break;
            case R2T:
                left = (width-itemWidth-(mainImgWidth-itemWidth)/2);
                top = (mainImgHeight - itemHeight) / 2;
                break;
        }
        marginLayoutParams.leftMargin = left+finalX;
        marginLayoutParams.topMargin = top-finalY;
    }

    /**
     * 根据xml中设置的content_gravity，生成枚举型的标记
     * @param content_gravity
     */
    private void setGravityType(int content_gravity) {

        boolean left = (content_gravity & F_LEFT)>0;
        boolean top = (content_gravity & F_TOP)>0;
        boolean right = (content_gravity & F_RIGTH)>0;
        boolean bottom = (content_gravity & F_BOTTOM)>0;

        if(left){
            gravityType = GravityType.LEFT;
            startDegree = 90;
            endDegree = -90;
            if(bottom){
                gravityType = GravityType.L2B;
                startDegree = 0;
                endDegree =90;
            }
            if(top){
                gravityType = GravityType.L2T;
                startDegree = 0;
                endDegree =-90;
            }
        }
        if(right){
            gravityType = GravityType.RIGHT;
            startDegree = 90;
            endDegree = 270;
            if(bottom){
                gravityType = GravityType.R2B;
                startDegree = 90;
                endDegree = 180;
            }
            if(top){
                gravityType = GravityType.R2T;
                startDegree = 180;
                endDegree = 270;
            }
        }
        if((!left)&(!right)&bottom){
            gravityType = GravityType.BOTTOM;
            startDegree = 0;
            endDegree = 180;
        }
        if((!left)&(!right)&top){
            gravityType = GravityType.TOP;
            startDegree = 0;
            endDegree = -180;
        }
    }

    /**
     * 根据gravity来生成宽高
     * @param mainImgWidth
     * @param viewW
     * @param radius
     */
    private int generateMeasureWidth(int mainImgWidth, int viewW, float radius) {
        int result = 0;
        switch (gravityType){
            case LEFT:
            case RIGHT:
            case L2B:
            case L2T:
            case R2B:
            case R2T:
                result = (int) ((mainImgWidth + viewW) / 2 + radius);
                break;
            case TOP:
            case BOTTOM:
                result = (int) (radius*2+viewW);
                break;
        }
        return result;
    }

    /**
     * 生成高
     * @param mainImgHeight
     * @param viewH
     * @param radius
     * @return
     */
    private int generateMeasureHeight(int mainImgHeight, int viewH, float radius) {
        int result = 0;
        switch (gravityType){
            case LEFT:
            case RIGHT:
                result = (int) (radius*2+viewH);
                break;
            case TOP:
            case BOTTOM:
            case L2B:
            case L2T:
            case R2B:
            case R2T:
                result = (int) ((mainImgHeight + viewH) / 2 + radius);
                break;
        }
        return result;
    }

    /**
     * 生成MainImg的权重
     * @return
     */
    private int getMainImgGravity() {
        int result = 0;
        switch (gravityType){
            case RIGHT:
                result = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
                break;
            case LEFT:
                result = Gravity.LEFT|Gravity.CENTER_VERTICAL;
                break;
            case BOTTOM:
                result = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
                break;
            case TOP:
                result = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
                break;
            case R2T:
                result = Gravity.RIGHT|Gravity.TOP;
                break;
            case R2B:
                result = Gravity.RIGHT|Gravity.BOTTOM;
                break;
            case L2B:
                result = Gravity.LEFT|Gravity.BOTTOM;
                break;
            case L2T:
                result = Gravity.LEFT|Gravity.TOP;
                break;
        }
        return result;
    }
}
