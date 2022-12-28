package com.example.smartlocker;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView mTextViewLabel;
    public CircleImageView mImageViewIcon;
    public ItemClickListener itemClickListener;
    public CheckBox check;

    public MyviewHolder(View view) {
        super(view);
        // Get the widgets reference from custom layout
        mTextViewLabel = (TextView) view.findViewById(R.id.appName_txt);
        mImageViewIcon = (CircleImageView) view.findViewById(R.id.icon_app);
        check = (CheckBox) view.findViewById(R.id.check_box);
        check.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener id)
    {
        this.itemClickListener=id;
    }

    @Override
    public void onClick(View view)
    {
       this.itemClickListener.onItemClick(view,getLayoutPosition());
    }
}
