package com.xinyang.alienware.gaodeiosstyle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.xinyang.alienware.gaodeiosstyle.R;

import java.util.List;

/**
 * Created by Alienware on 2017/12/7.
 */

public class ChoutiRecyclerViewAdapter extends RecyclerView.Adapter<ChoutiRecyclerViewAdapter.ViewHolder>  {

    public ItemClickListener mItemClickListener;
    private List<PoiItem> itemList;

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }


    public interface ItemClickListener {
        public void onItemClick(View view, int pos);
    }

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //  public final ImageView mImageView;
        public TextView mtitle,mdesc;
        public RelativeLayout mItem;

        public ViewHolder(View view) {

            super(view);
            // mImageView = (ImageView) view.findViewById(R.id.avatar);
            mtitle =view.findViewById(R.id.tv_title);
            mdesc =view.findViewById(R.id.tv_address);
            mItem = view.findViewById(R.id.rl_item);

        }


    }

    public ChoutiRecyclerViewAdapter(Context context,List<PoiItem> poiItemList) {
        super();
        mContext = context;
        this.itemList = poiItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showaddress, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mtitle .setText(itemList.get(position).getTitle());
        holder.mdesc .setText(itemList.get(position).getSnippet());
        holder.itemView.setTag(position);



        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null){

                    mItemClickListener.onItemClick(v, (Integer) v.getTag());

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (itemList != null){
            return itemList.size();
        }else {
            return 0;
        }

    }




}
