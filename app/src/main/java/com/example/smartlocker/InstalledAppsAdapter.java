package com.example.smartlocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InstalledAppsAdapter extends RecyclerView.Adapter<MyviewHolder>
{
    Context c;
    ArrayList<AppInfo> data;
     ArrayList<AppInfo> checkedApps= new ArrayList<>();

    public InstalledAppsAdapter(Context c,ArrayList<AppInfo> data)
    {
        this.c=c;
        this.data = data;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position)
    {
        holder.mTextViewLabel.setText(data.get(position).getAppName());
        holder.mImageViewIcon.setImageDrawable(data.get(position).getAppIcon());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos)
            {
                CheckBox chk = (CheckBox) v;
                if (chk.isChecked())
                {
                    checkedApps.add(data.get(pos));
                }
                else if (!chk.isChecked())
                {
                    checkedApps.remove(data.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
