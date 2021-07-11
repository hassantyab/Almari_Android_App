package com.fyp.hassan.almari.views.indicators;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;

import com.fyp.hassan.almari.R;




public class SquareIndicator extends IndicatorShape {

    public SquareIndicator(Context context, int indicatorSize, boolean mustAnimateChanges) {
        super(context, indicatorSize, mustAnimateChanges);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.indicator_square_unselected, null));
        } else {
            setBackgroundDrawable(getResources().getDrawable(R.drawable.indicator_square_unselected));
        }
    }

    @Override
    public void onCheckedChange(boolean isChecked) {
        super.onCheckedChange(isChecked);
        if (isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.indicator_square_selected, null));
            } else {
                setBackgroundDrawable(getResources().getDrawable(R.drawable.indicator_square_selected));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.indicator_square_unselected, null));
            } else {
                setBackgroundDrawable(getResources().getDrawable(R.drawable.indicator_square_unselected));
            }
        }
    }
}
