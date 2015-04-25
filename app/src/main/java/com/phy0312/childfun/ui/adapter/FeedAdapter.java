package com.phy0312.childfun.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.phy0312.childfun.ChildFunApplication;
import com.phy0312.childfun.R;
import com.phy0312.childfun.Utils;
import com.phy0312.childfun.model.PlaceItem;
import com.phy0312.childfun.tools.AndroidUtil;
import com.phy0312.childfun.tools.ImageLoaderUtil;
import com.phy0312.childfun.tools.LocationUtil;
import com.phy0312.childfun.ui.activity.DetailActivity;
import com.phy0312.childfun.ui.activity.LocationOverlayActivity;
import com.phy0312.childfun.ui.view.SendingProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ddj on 15-3-21.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;


    private final Map<Integer, Integer> likesCount = new HashMap<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();

    private List<PlaceItem> list = new ArrayList<>();
    DisplayImageOptions options;

    public FeedAdapter(Context context) {
        this.context = context;
        options = ImageLoaderUtil.newDisplayImageOptionsInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        return cellFeedViewHolder;
    }


    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        bindDefaultFeedItem(position, holder);

    }

    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        PlaceItem placeItem = list.get(position);
        ImageLoader.getInstance().displayImage(placeItem.iconUrl, holder.ivFeedCenter, options);
        holder.tvFeedBottom.setText(placeItem.desc);
        //updateLikesCounter(holder, false);
        //updateHeartButton(holder, false);

        holder.tvName.setText(placeItem.name);
        holder.btnComments.setTag(position);
        holder.btnMore.setTag(position);
        holder.ivFeedCenter.setTag(holder);
        holder.btnLike.setTag(holder);

        if (likeAnimations.containsKey(holder)) {
            likeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);
        holder.btnComments.setOnClickListener(this);
        holder.btnMore.setOnClickListener(this);
        holder.ivFeedCenter.setOnClickListener(this);
        holder.btnLike.setOnClickListener(this);
        holder.ivFeedCenter.setTag(placeItem);
        holder.tvDistance.setOnClickListener(this);
        holder.tvDistance.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        holder.tvDistance.getPaint().setAntiAlias(true);
        holder.tvDistance.setTag(placeItem);
        GeoPoint p1 = new GeoPoint((int)(placeItem.latitude*1E6), (int)(placeItem.longitude*1E6));
        GeoPoint p2 = new GeoPoint((int)(ChildFunApplication.appContext.curLatLng[0]*1E6), (int)(ChildFunApplication.appContext.curLatLng[1]*1E6));
        holder.tvDistance.setText(LocationUtil.calcDistance(p1, p2)+"km");
        holder.ivNearby.setOnClickListener(this);
        holder.ivNearby.setTag(placeItem);
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        likeAnimations.remove(holder);
        holder.vBgLike.setVisibility(View.GONE);
        holder.ivLike.setVisibility(View.GONE);
    }


    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int currentLikesCount = likesCount.get(holder.getPosition()) + 1;
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }

        likesCount.put(holder.getPosition(), currentLikesCount);
    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if(tag instanceof PlaceItem) {
            if(view.getId() != R.id.tvDistance && view.getId()
                    != R.id.ivNearby) {
                Intent intent = new Intent();
                intent.putExtra("PlaceItem", ((PlaceItem) (tag)));
                intent.setClass(view.getContext(), DetailActivity.class);
                AndroidUtil.startActivity(view.getContext(), intent);
            }else {
                PlaceItem placeItem = (PlaceItem)tag;
                Intent intent = new Intent(view.getContext(), LocationOverlayActivity.class);
                intent.putExtra("targetLocation", new double[]{placeItem.latitude, placeItem.longitude});
                AndroidUtil.startActivity(view.getContext(), intent);
            }
        }
    }

    public void updateItems(boolean animated) {
        itemsCount = list.size();
        animateItems = animated;
        //fillLikesWithRandomValues();
        notifyDataSetChanged();
    }

    private void fillLikesWithRandomValues() {
        for (int i = 0; i < getItemCount(); i++) {
            likesCount.put(i, new Random().nextInt(100));
        }
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.tvFeedBottom)
        TextView tvFeedBottom;
        @InjectView(R.id.btnComments)
        ImageButton btnComments;
        @InjectView(R.id.btnLike)
        ImageButton btnLike;
        @InjectView(R.id.btnMore)
        ImageButton btnMore;
        @InjectView(R.id.vBgLike)
        View vBgLike;
        @InjectView(R.id.ivLike)
        ImageView ivLike;
        @InjectView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @InjectView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @InjectView(R.id.ivNearby)
        ImageView ivNearby;
        @InjectView(R.id.tvDistance)
        TextView tvDistance;

        SendingProgressView vSendingProgress;
        View vProgressBg;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

    }

    public List<PlaceItem> getList() {
        return list;
    }
}
