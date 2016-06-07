package com.emilia.oromountain;

import android.widget.HorizontalScrollView;

/**
 * Created by Emilia on 29.05.2016.
 */
public interface ScrollViewListener {
    void onScrollChanged(HorizontalScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}