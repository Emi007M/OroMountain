package com.emilia.oromountain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.widget.RelativeLayout;

/**
 * Created by Emilia on 31.05.2016.
 */
public class OrodanceIcon extends MiniGameIcon {
    public OrodanceIcon(int id, String TAG, Point pos, final Context context, final RelativeLayout container, int stars, final boolean enabled){
        super(id, TAG, pos, context, container, stars, enabled);

        resourceImgId =  R.drawable.orodance_icon;
        icon.setImageResource(resourceImgId);
        //imagePosChange = new Point(-100,-160);

        rescale();

        setPosition();
    }

    private void rescale(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        float scale = (float)container.getLayoutParams().height * 0.1f;

        icon.getLayoutParams().height = (int)(scale);
        imagePosChange = new Point(-(int)scale,-(int)(scale));

        stars.getLayoutParams().height = (int)(scale*0.50);
        stars.getLayoutParams().width = (int)(scale*0.7);


    }

}
