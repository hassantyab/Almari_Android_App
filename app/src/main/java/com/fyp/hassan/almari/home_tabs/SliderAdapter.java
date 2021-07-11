package com.fyp.hassan.almari.home_tabs;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fyp.hassan.almari.R;

/**
 * Created by Hassan on 24-Mar-18.
 */

public class SliderAdapter extends PagerAdapter {

    private int[] imgs={R.drawable.asus1,R.drawable.asus2,R.drawable.asus2};
    private LayoutInflater inflater;
    private Context ctx;

    public  SliderAdapter(Context ctx)
    {
        this.ctx=ctx;
    }
    @Override
    public int getCount() {
        return imgs.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.slider,container,false);
        ImageView img =(ImageView)v.findViewById(R.id.imageView);
        img.setImageResource(imgs[position]);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
