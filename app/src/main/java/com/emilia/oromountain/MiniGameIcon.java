package com.emilia.oromountain;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Emilia on 30.05.2016.
 */
public abstract class MiniGameIcon {
    final int ID;
    Context context;
    RelativeLayout container;
    protected int resourceImgId;
    String TAG;

    ImageView stars;
    ImageView icon;

    RelativeLayout.LayoutParams absParams;
    Point imagePosChange;
    Point pos;

    boolean enabled = false;
    int starsAmount; //<0,3>;


    Guy guy;

    public MiniGameIcon(int id, String TAG, Point pos, final Context context, final RelativeLayout container, int stars, final boolean enabled) {
        ID = id;
        this.TAG = TAG;
        this.context = context;
        this.container = container;
        this.pos = pos;
        this.starsAmount = stars;
        this.enabled = enabled;

        this.stars = new ImageView(context);
        setStars(stars);
        if(enabled)
            Enable();
        else
            this.stars.setVisibility(View.INVISIBLE);


        icon = new ImageView(context);
        container.addView(icon);
        container.addView(this.stars);


        icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!enabled) return;
                if(guy.moving) return;

                ObjectAnimator jump = ObjectAnimator.ofFloat(icon, "translationY", icon.getY()-20);
                jump.setDuration(200);
                jump.setRepeatCount(1);
                jump.setRepeatMode(Animation.REVERSE);
                jump.start();

                guy.moveToGame(ID);

            }
        });



    }

    protected void setPosition(){
        icon.setX(pos.x+imagePosChange.x);
        icon.setY(pos.y+imagePosChange.y);
      //  icon.set


        stars.setX(pos.x+(int)(0.25*(float)imagePosChange.x));
        stars.setY(pos.y);
    }

    public void ChangePosBy(int y){
        stars.setY(pos.y+ y);
        icon.setY(pos.y + imagePosChange.y + y);

    }

//    public void Disable(){
//        enabled = false;
//    }

    public void Enable(){
        enabled = true;
        stars.setVisibility(View.VISIBLE);


    }

    public void setStars(int x){
        starsAmount = x;

        switch (starsAmount){
            case 0:
                this.stars.setImageResource(R.drawable.stars_0);
                break;
            case 1:
                this.stars.setImageResource(R.drawable.stars_1);
                break;
            case 2:
                this.stars.setImageResource(R.drawable.stars_2);
                break;
            case 3:
                this.stars.setImageResource(R.drawable.stars_3);
                break;


        }

    }

    public void setGuy(Guy g){
        guy = g;
    }
}
