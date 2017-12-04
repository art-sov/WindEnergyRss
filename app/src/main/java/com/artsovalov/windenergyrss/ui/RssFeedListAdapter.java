package com.artsovalov.windenergyrss.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artsovalov.windenergyrss.R;
import com.artsovalov.windenergyrss.model.RssFeedModel;
import com.artsovalov.windenergyrss.service.RssFeedListAdapterListener;

import java.util.List;

public class RssFeedListAdapter extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {

    private List<RssFeedModel> mRssFeedModels;
    private static final String TAG = RssFeedListAdapter.class.getSimpleName();
    private RssFeedListAdapterListener mListener;

    static class FeedModelViewHolder extends RecyclerView.ViewHolder{
        private View rssFeedView;

        FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }
    }

    RssFeedListAdapter(List<RssFeedModel> rssFeedModels, RssFeedListAdapterListener rssFeedListAdapterListener) {
        this.mRssFeedModels = rssFeedModels;
        mListener = rssFeedListAdapterListener;
    }


    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss_feed, parent, false);
        return new FeedModelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FeedModelViewHolder holder, final int position) {

        final RssFeedModel rssFeedModel = mRssFeedModels.get(position);

        ((TextView)holder.rssFeedView.findViewById(R.id.titleText)).setText(rssFeedModel.getTitle());
        ((TextView)holder.rssFeedView.findViewById(R.id.descriptionText)).setText(rssFeedModel.getDescription());
        ((TextView)holder.rssFeedView.findViewById(R.id.dateText)).setText(rssFeedModel.getDate());

        holder.rssFeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null)
                    mListener.rssItemSelected(rssFeedModel);
                Log.i(TAG, "onClick() rssFeedView");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRssFeedModels.size();
    }
}
