package com.example.fahadhd.moviesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by fahad on 6/11/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> paths;
    private int width;

    public ImageAdapter(Context mContext, ArrayList<String> paths, int width) {
        this.mContext = mContext;
        this.paths = paths;
        this.width = width;
    }


    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    //For each column fills it with a corresponding poster image.
    //TODO: For now it just uses a placeholder image. Implement actual later.
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) imageView = new ImageView(mContext);
        else imageView = (ImageView) convertView;

        Drawable d = resizeDrawable(mContext.getResources().getDrawable(R.drawable.movieload));
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+
                paths.get(position)).resize(width,(int)(width*1.5)).placeholder(d).into(imageView);
        return imageView;

    }
    private Drawable resizeDrawable(Drawable image){
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b,width,(int)(width*1.5),false);
        return new BitmapDrawable(mContext.getResources(),bitmapResized);
    }
}