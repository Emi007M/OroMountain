package com.emilia.oromountain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Emilia on 06.06.2016.
 */
public class Flower {
    final int ID;
    Context context;
    RelativeLayout container;
    ImageView icon;

    RelativeLayout.LayoutParams absParams;
    Point imagePosChange;
    Point pos;

    boolean enabled = true;

    Guy guy;

    public Flower(int id, Point pos, final Context context, final RelativeLayout container, final boolean enabled) {
        ID = id;
        this.context = context;
        this.container = container;
        this.pos = pos;
        this.enabled = enabled;


        icon = new ImageView(context);
        icon.setImageResource(R.drawable.flower);
        container.addView(icon);



        icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!enabled) return;

                guy.moveToGame(ID);

            }
        });


        rescale();

        setPosition();
        if(this.enabled) Enable();
    }

    protected void setPosition(){
        icon.setX(pos.x);
        icon.setY(pos.y + imagePosChange.y);

    }

    public void ChangePosBy(int y){
        icon.setY(pos.y + imagePosChange.y + y);

    }


    public void Enable(){
        enabled = true;
        TranslateAnimation jump;
        jump = new TranslateAnimation(0,  0 , 0, -20 );
        jump.setDuration(500);
        jump.setRepeatCount(Animation.INFINITE);
        jump.setRepeatMode(Animation.REVERSE);
        icon.startAnimation(jump);

    }

    private void rescale(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        float scale = (float)container.getLayoutParams().height * 0.1f;

        icon.getLayoutParams().height = (int)(scale);
        imagePosChange = new Point(-(int)scale,-(int)(scale));

    }

    public void setGuy(Guy g){
        guy = g;
    }

    }
