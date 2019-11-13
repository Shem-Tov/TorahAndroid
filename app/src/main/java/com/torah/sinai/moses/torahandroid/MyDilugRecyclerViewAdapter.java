package com.torah.sinai.moses.torahandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.v4.text.HtmlCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyDilugRecyclerViewAdapter extends RecyclerView.Adapter<MyDilugRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<Match>> mDilugMatches;
    private ArrayList<String> mLineDilugSet;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    // matches is passed into the constructor
    MyDilugRecyclerViewAdapter(Context context
            , ArrayList<ArrayList<Match>> dilugSets, ArrayList<String> lineDilugSet) {
        this.mInflater = LayoutInflater.from(context);
        this.mDilugMatches = dilugSets;
        this.mLineDilugSet = lineDilugSet;
        this.mContext = context;
        //setHasStableIds(true);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_dilug_item_match, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArrayList<Match> mDilugSet = mDilugMatches.get(position);
        //holder.mTableLayoutView;
        holder.mLinearLayoutView.removeAllViews();
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
         for (int i = 0; i < mDilugSet.size(); i++) {
             final int final_i=i;
             LinearLayout LL_Inner = new LinearLayout(mContext);
             LL_Inner.setOrientation(LinearLayout.HORIZONTAL); // set orientation
             //LL_Inner.setBackgroundColor(color.white); // set background
             // set Layout_Width and Layout_Height
             LinearLayout.LayoutParams layoutForOuter = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
             LL_Inner.setLayoutParams(layoutForOuter);
             TextView Title1v = new TextView(mContext);
            //tbrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
             Title1v.setText(HtmlCompat
                    .fromHtml(mDilugSet.get(i).getTitle()
                            , HtmlCompat.FROM_HTML_MODE_LEGACY));
            Title1v.setLayoutParams(param);
            Title1v.setMinEms(4);
            //Title1v.setTextColor(Color.WHITE);
            //Title1v.setGravity(Gravity.CENTER);
            LL_Inner.addView(Title1v);
            TextView Position2v = new TextView(mContext);
            Position2v.setText(HtmlCompat
                     .fromHtml(mDilugSet.get(i).getPosition()
                             , HtmlCompat.FROM_HTML_MODE_LEGACY));
            Position2v.setLayoutParams(param);
            Position2v.setMinEms(7);
            //Position2v.setTextColor(Color.WHITE);
            //Position2v.setGravity(Gravity.CENTER);
            LL_Inner.addView(Position2v);
            TextView Pasuk3v = new TextView(mContext);
            Pasuk3v.setText(HtmlCompat.fromHtml(
                     mDilugSet.get(i).getPasuk()
                     , HtmlCompat.FROM_HTML_MODE_LEGACY));
            Pasuk3v.setLayoutParams(param);
            Pasuk3v.setPadding(3,3,30,3);
            Pasuk3v.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if ((mDilugSet.get(final_i).getDetail() == null) || (mDilugSet.get(final_i).getDetail() == "")) {
                         return;
                     }
                     TextView textCToast = new TextView(view.getContext());

                     textCToast.setText(HtmlCompat.fromHtml(
                             "<b>"+mDilugSet.get(final_i).getDetail()+"</b>"
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
            //Pasuk3v.setTextColor(Color.WHITE);
            //Pasuk3v.setGravity(Gravity.CENTER);
            LL_Inner.addView(Pasuk3v);
            holder.mLinearLayoutView.addView(LL_Inner);
        }
        LinearLayout LL_Inner = new LinearLayout(mContext);
        LL_Inner.setOrientation(LinearLayout.HORIZONTAL); // set orientation
        // set Layout_Width and Layout_Height
        LinearLayout.LayoutParams layoutForOuter = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LL_Inner.setLayoutParams(layoutForOuter);
        TextView LineDilug0 = new TextView(mContext);
        LineDilug0.setText(HtmlCompat.fromHtml(
                mLineDilugSet.get(position)
                , HtmlCompat.FROM_HTML_MODE_LEGACY));
        LineDilug0.setLayoutParams(param);
        LL_Inner.addView(LineDilug0);
        holder.mLinearLayoutView.addView(LL_Inner);
        View view = new View(mContext);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 6));
        view.setBackgroundResource(R.color.colorBG_search);
        holder.mLinearLayoutView.addView(view);
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
        return mDilugMatches.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

     // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout mLinearLayoutView;

        ViewHolder(View itemView) {
            super(itemView);
            //list_item_match.xml
            mLinearLayoutView = itemView.findViewById(
                    R.id.Dilug_LinearLayout);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
