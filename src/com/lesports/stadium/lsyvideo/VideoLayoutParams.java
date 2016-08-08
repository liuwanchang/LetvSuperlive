package com.lesports.stadium.lsyvideo;

import com.lesports.stadium.utils.GlobalParams;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Created by gaolinhua on 2016/5/26.
 */
public class VideoLayoutParams {
    public static RelativeLayout.LayoutParams computeContainerSize(Context context, int mWidth, int mHeight) {
        int width = GlobalParams.WIN_WIDTH;
        int height = width * mHeight / mWidth;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = width;
        params.height = height;
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }
}