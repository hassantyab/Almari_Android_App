package com.fyp.hassan.almari.SingleProductAvtivity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.R;

import java.util.List;

/**
 * Created by Hassan on 22-Dec-17.
 */
public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Product> productList;


    public ViewPageAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList=productList;
    }

    @Override
    public int getCount() {
        return productList.get(0).getImgs().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.imageslider, null);
        try {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.product_progressBar);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH);

            new GlideImageLoader(imageView,
                    progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/" + productList.get(0).getImgs().get(position),options);
            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}