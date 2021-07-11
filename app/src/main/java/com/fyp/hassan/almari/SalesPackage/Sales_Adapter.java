package com.fyp.hassan.almari.SalesPackage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.hassan.almari.Category_package.CategoryActivity;
import com.fyp.hassan.almari.Glide_Package.GlideImageLoader;
import com.fyp.hassan.almari.R;

import java.util.List;

public class Sales_Adapter extends RecyclerView.Adapter<Sales_Adapter.mySalesView>
{
    List<salesDetails> salesDetailsList;
    Context mContext;

    public Sales_Adapter(Context context,List<salesDetails> salesDetails)
    {
        this.mContext=context;
        this.salesDetailsList=salesDetails;
    }

    @Override
    public mySalesView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_adapater,parent,false);
        return new Sales_Adapter.mySalesView(view);
    }

    @Override
    public void onBindViewHolder(mySalesView holder, int position)
    {
        try {

            final salesDetails salesDetails = salesDetailsList.get(position);
            holder.salesText.setText("Sale UPTO "+salesDetails.getSalesPercentage());
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .priority(Priority.HIGH);

            new GlideImageLoader(holder.imageView,
                    holder.progressBar).load("https://s3.us-east-2.amazonaws.com/almari-2018/" +salesDetails.getSalesImage(), options);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CategoryActivity.class);
                    intent.putExtra("sid",salesDetails.getId());
                    mContext.startActivity(intent);
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return salesDetailsList.size();
    }

    public static  class  mySalesView extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView salesText;
        private ProgressBar progressBar;
        private  View cardView;
        public mySalesView(View itemView) {
            super(itemView);
            imageView =(ImageView)itemView.findViewById(R.id.salesImage);
            salesText = (TextView)itemView.findViewById(R.id.salesPrice);
            progressBar=(ProgressBar)itemView.findViewById(R.id.sales_progressbar);
            cardView=itemView;
        }
    }
}
