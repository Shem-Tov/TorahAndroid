package com.torah.sinai.moses.torahandroid;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.v4.text.HtmlCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyReportRecyclerViewAdapter extends RecyclerView.Adapter<MyReportRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Match> mMatches;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // matches is passed into the constructor
    MyReportRecyclerViewAdapter(Context context, ArrayList<Match> matches) {
        this.mInflater = LayoutInflater.from(context);
        this.mMatches = matches;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_report_item_match, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitleWebView.removeAllViews();
        Match mMatch = mMatches.get(position);
        //WebSettings webSetting = holder.mTitleWebView.getSettings();
        //webSetting.setJavaScriptEnabled(true);
        holder.mTitleWebView.loadDataWithBaseURL(null, mMatch.getTitle(), "text/html", "UTF-8", null);

         //Converts Html to regular Text
        //String plainText= Jsoup.parse(mMatch.getTitle()).text();
    }

     // total number of rows
    @Override
    public int getItemCount() {
        return mMatches.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        WebView mTitleWebView;


        ViewHolder(View itemView) {
            super(itemView);
            //list_item_match.xml
            mTitleWebView = itemView.findViewById(
                    R.id.list_report_item_match_web_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Match getItem(int id) {
        return mMatches.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
