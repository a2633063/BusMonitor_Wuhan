package com.zyc.busmonitor.mainrecycler;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.Collections;

//Recycler 长按拖动排序功能
public class MainRecyclerItemTouchHelper extends ItemTouchHelper.Callback {
    private MainRecyclerAdapter adapter;

    public MainRecyclerItemTouchHelper(MainRecyclerAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 返回滑动的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag;
        int swipeFlag;
        dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP;
        swipeFlag = 0;//ItemTouchHelper.LEFT;//只允许从右到左的侧滑
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    /**
     * 当用户拖动一个item从旧的位置移动到新的位置时会调用此方法
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adapter.getDataList(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adapter.getDataList(), i, i - 1);
            }
        }
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 当用户左右滑动item达到删除条件时会调用此方法
     * 一般达到item的一般宽度时才会删除，否则弹回原位置
     *
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        int position = viewHolder.getAdapterPosition();
//        if (direction == ItemTouchHelper.END) {
//            adapter.getDataList().remove(position);
//            adapter.notifyItemRemoved(position);
//        }
//        adapter.onItemDissmiss(position);
    }

    /**
     * 当某个item由静止状态变为滑动或拖动状态时调用此方法
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//            viewHolder.itemView.setBackgroundColor(Color.GRAY);

            viewHolder.itemView.setAlpha(0.5f);
        }
    }

    /**
     * 当用户操作完某个item动画结束时调用此方法
     *
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
//        viewHolder.itemView.setBackgroundColor(0);
        viewHolder.itemView.setAlpha(1.0f);
    }

    /**
     * 是否支持长按拖动
     * 默认返回true
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 是否支持侧滑删除
     * 默认返回true
     * 不支持侧滑删除返回false
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }
}
