package com.torah.sinai.moses.torahandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.HtmlCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Match> mMatches;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // matches is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<Match> matches) {
        this.mInflater = LayoutInflater.from(context);
        this.mMatches = matches;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_match, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Match mMatch = mMatches.get(position);
        holder.mTitleTextView.setText(HtmlCompat
                .fromHtml(mMatch.getTitle()
                        , HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.mPositionTextView.setText(HtmlCompat
                .fromHtml(mMatch.getPosition()
                        , HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.mPasukTextView.setText(HtmlCompat.fromHtml(
                mMatch.getPasuk()
                , HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.mPasukTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mMatch.getDetail() == null) || (mMatch.getDetail() == "")) {
                    return;
                }
                TextView textCToast = new TextView(view.getContext());

                textCToast.setText(HtmlCompat.fromHtml(
                        "<b>"+mMatch.getDetail()+"</b>"
                        , HtmlCompat.FROM_HTML_MODE_LEGACY));
                //textCToast.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorBG_search));
                textCToast.setPadding(36,18,36,18);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(0xFF00FF00); // Changes this drawbale to use a single color instead of a gradient
                gd.setCornerRadius(50);
                gd.setStroke(8, 0xFF000000);
                textCToast.setBackground(gd);
                Toast toast = new Toast(view.getContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(textCToast);
                killAllToastandTimers();
                msjsView.add(textCToast);
                msjsToast.add(toast);
                toast.show();
                CountDownTimer cTimer = new CountDownTimer(10000, 1000)
                {
                    public void onTick(long millisUntilFinished) {toast.show();}
                    public void onFinish() {toast.show();}
                };
                msjsTimer.add(cTimer);
                cTimer.start();
            }
        });


         //Converts Html to regular Text
        //String plainText= Jsoup.parse(mMatch.getTitle()).text();
    }

    private ArrayList<View> msjsView = new ArrayList<View>();
    private ArrayList<Toast> msjsToast = new ArrayList<Toast>();
    private ArrayList<CountDownTimer> msjsTimer = new ArrayList<CountDownTimer>();

    private void killAllToastandTimers(){
        for(Toast t:msjsToast){
            if(t!=null) {
                t.cancel();
            }
        }
        msjsToast.clear();
        for(CountDownTimer t:msjsTimer){
            if(t!=null) {
                t.cancel();
            }
        }
        msjsTimer.clear();
        for(View t:msjsView){
            if(t!=null) {
                t.setVisibility(View.GONE);
                t=null;
            }
        }
        msjsView.clear();
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
        TextView mTitleTextView;
        TextView mPasukTextView;
        TextView mPositionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            //list_item_match.xml
            mTitleTextView = itemView.findViewById(
                    R.id.list_item_match_title_text_view);
            mPasukTextView = itemView.findViewById(
                    R.id.list_item_match_text_pasuk);
            mPositionTextView = itemView.findViewById(
                    R.id.list_item_match_position);

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
