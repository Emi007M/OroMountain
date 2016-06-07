package com.emilia.oromountain;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Emilia on 31.05.2016.
 */
public class Guy {
    Context context;
    RelativeLayout container;
    ImageView guy;
    Point pos;
    Point imagePosChange;
    ArrayList<Point> path;
    int no_scroll_y;
    int current_scroll_offset;
    boolean animation_started = false;

    boolean moving=false;

    int currGameIndex;

    public Guy(Context context, RelativeLayout container){
        this.context = context;
        this.container = container;

        guy = new ImageView(context);
        guy.setImageResource(R.drawable.guy);
        container.addView(guy);


        current_scroll_offset=0;
        currGameIndex = 0;

        rescale();



    }


    public void setPosition(Point p){
        pos = p;
        pos.x += imagePosChange.x;
        pos.y += imagePosChange.y;
        guy.setX(pos.x);
        guy.setY(pos.y);

        no_scroll_y=pos.y;

        System.out.println("PPX "+pos);

    }

    public void ChangePosBy(int y){
       // btn.setY(pos.y + y);
        pos.y = y + no_scroll_y;
        //guy.setY(pos.y + imagePosChange.y + y);
        guy.setY(pos.y);

        current_scroll_offset = y;

        System.out.println("PP "+pos);

    }

    public void setPath(ArrayList<Point> arr){
        path = arr;
    }

    public void moveToGame(int index){
        System.out.println("GUY:"+index+", "+path.size());
        if(moving)return;
        moving=true;

        if(index>=path.size() || index==currGameIndex) return;


        if(index>currGameIndex) {
            if(index-1==currGameIndex)
                doStep(++currGameIndex, index, new android.view.animation.AccelerateDecelerateInterpolator(), 1000);
            else
                doStep(++currGameIndex, index, new android.view.animation.AccelerateInterpolator(), 1000);
        }
        else {
            if(index+1==currGameIndex)
                doStep(--currGameIndex, index, new android.view.animation.AccelerateDecelerateInterpolator(), 1000);
            else
                doStep(--currGameIndex, index, new android.view.animation.AccelerateInterpolator(), 1000);
        }


    }

    private void doStep(int index, final int desired_index, Interpolator i, int duration){
        final Point target = new Point(path.get(index));
        target.x+=imagePosChange.x;
        target.y+=imagePosChange.y;



        AnimatorSet animSetXY = new AnimatorSet();
        ObjectAnimator translateX = ObjectAnimator.ofFloat(guy, "translationX", target.x);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(guy, "translationY", target.y + current_scroll_offset);
        animSetXY.playTogether(translateX,translateY);
        animSetXY.setDuration(duration);
        animSetXY.setInterpolator(i);


        animSetXY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mListener != null)
                    mListener.onMoveStart();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                no_scroll_y = target.y ;

                pos.x += (int)(target.x - pos.x);
                pos.y += (int)(target.y - pos.y+ current_scroll_offset);

                guy.setX(pos.x);
                guy.setY(pos.y);

                if(desired_index>currGameIndex){
                    if(desired_index-1==currGameIndex) //last step
                        doStep(++currGameIndex, desired_index, new android.view.animation.DecelerateInterpolator(), 1000);
                    else
                        doStep(++currGameIndex, desired_index, new android.view.animation.LinearInterpolator(), 700);
                }

                else if(desired_index<currGameIndex) {
                    if (desired_index + 1 == currGameIndex) //last step
                        doStep(--currGameIndex, desired_index, new android.view.animation.DecelerateInterpolator(), 1000);
                    else
                        doStep(--currGameIndex, desired_index, new android.view.animation.LinearInterpolator(), 700);
                }
                else{
                    moving=false;
                    if (mListener != null)
                        mListener.onMoveEnded(desired_index);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animSetXY.start();



    }

    private void rescale(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        guy.getLayoutParams().height = (int)(size.y*0.25);
        imagePosChange = new Point(-50,-(int)(size.y*0.22));

    }



    public interface Listener {
        public void onMoveEnded(int index);
        public void onMoveStart();

    }

    private Listener mListener = null;
    public void registerListener (Listener listener) {
        mListener = listener;
    }
}
