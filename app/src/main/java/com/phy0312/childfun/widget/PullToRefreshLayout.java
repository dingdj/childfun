package com.phy0312.childfun.widget;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.phy0312.childfun.R;
import com.phy0312.childfun.widget.smoothprogressbar.SmoothProgressBar;


public class PullToRefreshLayout extends FrameLayout {
    public final static float MAX_PULL_DISTANCE = 300.0f;

    private boolean isRefreshingUp;
    private boolean isRefreshingBottom;

    private boolean isDownFromUp;
    private boolean isDownFromBottom;

    private boolean isDragFromUp;
    private boolean isDragFromBottom;

    private RecyclerView recyclerView;
    private GridView gridView;
    private SmoothProgressBar upProgressBar;
    private SmoothProgressBar bottomProgressBar;

    private float touchSlop;

    private float initialMotionY = -1f;
    private float initialMotionX = -1f;
    private float lastMotionY = -1f;

    private PullRefreshListener pullRefreshListener;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setUpProgressBar(SmoothProgressBar upProgressBar) {
        this.upProgressBar = upProgressBar;
        applyProgressBarSettings(upProgressBar);
    }

    public void setBottomProgressBar(SmoothProgressBar bottomProgressBar) {
        this.bottomProgressBar = bottomProgressBar;
        applyProgressBarSettings(bottomProgressBar);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final float x = event.getX(), y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isRefreshingUp && isReadyForPullFromTop()) {
                    initialMotionX = x;
                    initialMotionY = y;
                    lastMotionY = y;
                    isDownFromUp= true;
                    isDownFromBottom = false;
                } else if (!isRefreshingBottom && isReadyForPullFromBottom()) {
                    initialMotionX = x;
                    initialMotionY = y;
                    lastMotionY = y;
                    isDownFromBottom= true;
                    isDownFromUp = false;
                } else {
                    isDownFromUp = false;
                    isDownFromBottom = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (initialMotionY > 0) {
                    if (!isDragFromUp && isDownFromUp) {
                        final float yDx = y - initialMotionY;
                        final float xDx = x - initialMotionX;
                        if (yDx > xDx && yDx > touchSlop) {
                            onPullUpStart(y);
                            isDragFromUp = true;
                        } else if (yDx < -touchSlop) {
                            resetTouch();
                        }
                    } else if (!isDragFromBottom && isDownFromBottom) {
                        final float yDx = initialMotionY - y;
                        final float xDx = initialMotionX - x;
                        if (yDx > xDx && yDx > touchSlop) {
                            onPullBottomStart(y);
                            isDragFromBottom = true;
                        } else if (yDx < -touchSlop) {
                            resetTouch();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                resetTouch();
                break;
        }

        return isDragFromUp || isDragFromBottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isDragFromUp && y != lastMotionY) {
                    final float yDx = y - lastMotionY;
                    if (yDx >= -touchSlop) {
                        final float progress = (y - initialMotionY) / MAX_PULL_DISTANCE;
                        onPullUp(progress);
                        if (yDx > 0 ) {
                            lastMotionY = y;
                        }
                    } else {
                        onPullUpEnd();
                        resetTouch();
                    }
                } else if (isDragFromBottom && y != lastMotionY) {
                    final float yDx = lastMotionY - y;
                    if (yDx >= -touchSlop) {
                        final float progress = (initialMotionY - y) / MAX_PULL_DISTANCE;
                        onPullBottom(progress);
                        if (yDx > 0 ) {
                            lastMotionY = y;
                        }
                    } else {
                        onPullBottomEnd();
                        resetTouch();
                    }
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                checkForRefresh(y);
                if (isDragFromUp) {
                    onPullUpEnd();
                } else if (isDragFromBottom) {
                    onPullBottomEnd();
                }
                resetTouch();
                break;
        }
        return true;
    }

    private void checkForRefresh(float y) {
        if (isDragFromUp) {
            if (y - initialMotionY > MAX_PULL_DISTANCE) {
                onRefreshingUp();
            }
        } else if (isDragFromBottom) {
            if (initialMotionY - y > MAX_PULL_DISTANCE) {
                onRefreshingBottom();
            }
        }
    }

    private void resetTouch() {
        isDragFromUp = isDragFromBottom = false;
        initialMotionY = initialMotionX = -1f;
    }

    private boolean isReadyForPullFromTop() {
        boolean ready = false;
        if(recyclerView != null) {
            if (recyclerView.getAdapter().getItemCount() == 0) {
                ready = true;
            } else if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0) {
                final View firstVisibleChild = recyclerView.getChildAt(0);
                ready = firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
            }
        }else if(gridView != null) {
            if (gridView.getCount() == 0) {
                ready = true;
            } else if (gridView.getFirstVisiblePosition() == 0) {
                final View firstVisibleChild = gridView.getChildAt(0);
                ready = firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
            }
        }
        return ready;
    }

    private boolean isReadyForPullFromBottom() {
        boolean ready = false;
        if(recyclerView != null) {
            Log.e("ddj", ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()+"");
            Log.e("ddj", (recyclerView.getAdapter().getItemCount() - 1)+"");
            if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() ==
                    recyclerView.getAdapter().getItemCount() - 1) {

                ready = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1;
            }
        }else if(gridView != null) {
            if (gridView.getLastVisiblePosition() == gridView.getCount() - 1) {
                final View lastVisibleChild = gridView.getChildAt(gridView.getLastVisiblePosition() -
                        gridView.getFirstVisiblePosition());
                ready = lastVisibleChild != null && (lastVisibleChild.getBottom() +
                        gridView.getListPaddingBottom() == gridView.getHeight());
            }
        }
        return ready;
    }

    private void applyProgressBarSettings(SmoothProgressBar smoothProgressBar) {
        if (smoothProgressBar != null) {
            int progressDrawableColor = getResources().getColor(R.color.spb_default_color);
            ShapeDrawable shape = new ShapeDrawable();
            shape.setShape(new RectShape());
            shape.getPaint().setColor(progressDrawableColor);
            ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER, ClipDrawable.HORIZONTAL);

            smoothProgressBar.setProgressDrawable(clipDrawable);
        }
    }

    public void setRefreshUpEnd() {
        isRefreshingUp = false;
        if (this.upProgressBar != null) {
            this.upProgressBar.setVisibility(View.INVISIBLE);
            this.upProgressBar.setProgress(0);
            this.upProgressBar.setIndeterminate(false);
        }
    }

    public void setRefreshingBottomEnd() {
        isRefreshingBottom = false;
        if (this.bottomProgressBar != null) {
            this.bottomProgressBar.setVisibility(View.INVISIBLE);
            this.bottomProgressBar.setProgress(0);
            this.bottomProgressBar.setIndeterminate(false);
        }
    }

    public void onRefreshingUp() {
        setRefreshingUp();
        pullRefreshListener.onRefreshingUp();
    }

    public void setRefreshingUp() {
        isRefreshingUp = true;
        if (this.upProgressBar != null) {
            this.upProgressBar.setProgress(100);
            this.upProgressBar.setIndeterminate(true);
            this.upProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void onRefreshingBottom() {
        isRefreshingBottom = true;
        if (this.bottomProgressBar != null) {
            this.bottomProgressBar.setProgress(100);
            this.bottomProgressBar.setIndeterminate(true);
            this.bottomProgressBar.setVisibility(View.VISIBLE);
        }
        pullRefreshListener.onRefreshingBottom();
    }

    public void setOnPullRefreshListener(PullRefreshListener pullRefreshListener) {
        this.pullRefreshListener = pullRefreshListener;
    }

    private void onPullUpStart(float y) {}

    private void onPullBottomStart(float y) {}

    private void onPullUp(float progress) {
        if (this.upProgressBar != null) {
            this.upProgressBar.setProgress(Math.round(progress * upProgressBar.getMax()));
            this.upProgressBar.setIndeterminate(false);
            this.upProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void onPullUpEnd() {
        if (!isRefreshingUp && this.upProgressBar != null) {
            upProgressBar.setVisibility(View.INVISIBLE);
            upProgressBar.setProgress(0);
            upProgressBar.setIndeterminate(false);
            isDragFromUp = false;
        }
    }

    private void onPullBottom(float progress) {
        if (this.bottomProgressBar != null) {
            this.bottomProgressBar.setProgress(Math.round(progress * bottomProgressBar.getMax()));
            this.bottomProgressBar.setIndeterminate(false);
            this.bottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void onPullBottomEnd() {
        if (!isRefreshingBottom && this.bottomProgressBar != null) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
            bottomProgressBar.setProgress(0);
            bottomProgressBar.setIndeterminate(false);
            isDragFromBottom = false;
        }
    }

    public static interface PullRefreshListener {
        void onRefreshingUp();
        void onRefreshingBottom();
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }
}
