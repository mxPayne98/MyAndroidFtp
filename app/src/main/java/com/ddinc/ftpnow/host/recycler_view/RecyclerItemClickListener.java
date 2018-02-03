package com.ddinc.ftpnow.host.recycler_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private View childView;
    private RecyclerView touchView;
    private GestureDetector gestureDetector;

    public RecyclerItemClickListener(Context context, final OnItemClickListener listener) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (childView != null && listener != null)
                    listener.onItemClick(childView, touchView.getChildAdapterPosition(childView));
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (childView != null && listener != null)
                    listener.onLongClick(childView, touchView.getChildAdapterPosition(childView));
                super.onLongPress(e);
            }
        });
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        childView = rv.findChildViewUnder(e.getX(), e.getY());
        touchView = rv;
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}
