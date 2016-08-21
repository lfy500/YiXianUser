package com.example.lfy.myapplication.FragmentHome;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.lfy.myapplication.R;

/**
 * Created by lfy on 2016/4/24.
 */
public abstract class HeadFootAdapter<HeadViewHolder extends RecyclerView.ViewHolder, FootViewHolder extends RecyclerView.ViewHolder, ItemViewHolder extends RecyclerView.ViewHolder, GridViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //item的type 代表头部 中间  底部三个类型
    static int TYPE_HEAD = 0;
    static int TYPE_ITEM = 1;
    static int TYPE_FOOT = 2;
    static int TYPE_GRID = 3;
    int lastPosition = -1;

    /**
     * 头部ViewHolder
     **/
    public abstract HeadViewHolder onCreateHeaderViewHolder(ViewGroup parent, int position);

    public abstract ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int position);

    public abstract FootViewHolder onCreateFooterViewHolder(ViewGroup parent, int position);

    public abstract GridViewHolder onCreateGridViewHolder(ViewGroup parent, int position);

    public abstract void onBindHeaderViewHolder(HeadViewHolder holder, int position);

    public abstract void onBindItemViewHolder(ItemViewHolder holder, int position);

    public abstract void onBindFooterViewHolder(FootViewHolder holder, int position);

    public abstract void onBindGridViewHolder(GridViewHolder holder, int position);

    /**
     * 根据viewType来创建不同的ViewHolder
     **/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                return onCreateHeaderViewHolder(viewGroup, viewType);
            case 1:
                return onCreateItemViewHolder(viewGroup, viewType);
            case 2:
                return onCreateFooterViewHolder(viewGroup, viewType);
            default:
                return onCreateGridViewHolder(viewGroup, viewType);
        }
    }

    /**
     * 根据ViewType绑定数据  这里重新计算了position，使每个部分的position都从0开始
     **/
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        int headCount = getHeadViewCount();
        int itemViewCount = getItemViewCount();
        int footViewCount = getFootViewCount();
        int gridViewCount = getGridViewCount();

        int itemType = getItemViewType(i);
        switch (itemType) {
            case 0:
                onBindHeaderViewHolder((HeadViewHolder) viewHolder, i);
                break;
            case 1:
                setAnimation(viewHolder.itemView, i);
                onBindItemViewHolder((ItemViewHolder) viewHolder, i - headCount - gridViewCount);
                break;
            case 2:
                setAnimation(viewHolder.itemView, i);
                onBindFooterViewHolder((FootViewHolder) viewHolder, i - itemViewCount - headCount - gridViewCount);
                break;
            case 3:
                setAnimation(viewHolder.itemView, i);
                onBindGridViewHolder((GridViewHolder) viewHolder, i - headCount);
                break;

        }
    }

    protected void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_slide_bottom_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_slide_bottom_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    /**
     * adapter会调用这个方法来获取item的总数
     * 因为在这里  总数为上中下三部分数量加起来  不需要重写
     * 所以标志为final
     * 返回所有View的数量
     **/
    @Override
    public final int getItemCount() {
        return getHeadViewCount() + getFootViewCount() + getItemViewCount() + getGridViewCount();
    }

    /**
     * 返回头部View的数量
     **/
    public abstract int getHeadViewCount();


    /**
     * 返回底部View的数量
     **/
    public abstract int getFootViewCount();


    /**
     * 返回中间View的数量
     **/
    public abstract int getItemViewCount();

    /**
     * 返回gridView的数量
     **/
    public abstract int getGridViewCount();

    /**
     * 这句话是关键  根据position来判断item的类型
     * adapter会将此方法的返回值传入onCreateViewHolder
     **/
    @Override
    public int getItemViewType(int position) {
        //   return super.getItemViewType(position);
        int headCount = getHeadViewCount();
        int gridCount = getGridViewCount();
        int itemCount = getItemViewCount();
        int footCount = getFootViewCount();

        if (position < headCount) {
            return TYPE_HEAD;
        }

        if (position >= headCount && position < headCount + gridCount) {
            return TYPE_GRID;
        }
        if (headCount + gridCount <= position && position < headCount + gridCount + itemCount) {
            return TYPE_ITEM;
        }
        return TYPE_FOOT;
    }
}
