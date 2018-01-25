package io.weichao.stickynavigationbar.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import io.weichao.stickynavigationbar.util.ViewUtil;

/**
 * Created by chao.wei on 2018/1/24.
 */
public class SectionDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "SectionDecoration";

    private final Context mContext;
    private DecorationCallback mCallback;

    private Paint barPaint;
    private int barTopGap;
    private TextPaint textPaint;
    private int alignBottom;

    public SectionDecoration(Context context, DecorationCallback decorationCallback) {
        mContext = context;
        mCallback = decorationCallback;

        // 设置悬浮栏的画笔
        barPaint = new Paint();
        barPaint.setColor(Color.LTGRAY);
        // 决定悬浮栏的高度等
        barTopGap = ViewUtil.dp2px(context, 30);

        // 设置悬浮栏中文本的画笔
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(ViewUtil.dp2px(context, 20));
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);
        // 决定悬浮栏中文本的显示位置等
        alignBottom = (int) ((barTopGap - textPaint.getTextSize()) * 0.8f);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        // 只有是同一组的第一个 item 才显示悬浮栏
        if (isFirstInGroup(pos)) {
            outRect.top = barTopGap;
        } else {
            outRect.top = 0;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
//        int itemCount = state.getItemCount();// 总共的 item 数量
        int childCount = parent.getChildCount();// 一屏显示的 item 数量

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            String title = mCallback.getGroupTitle(position).toUpperCase();
            if (TextUtils.isEmpty(title)) {
                continue;
            }

            int left = parent.getPaddingLeft();// 悬浮栏左侧绘制的边界
            int right = parent.getWidth() - parent.getPaddingRight();// 悬浮栏右侧绘制的边界

            // 第一个 item 特殊处理
            if (i == 0) {
                // 组内最后一个 item
                int viewBottom = view.getBottom();
                if (isLastInGroup(position) && viewBottom < barTopGap) {
                    c.drawRect(left, viewBottom - barTopGap, right, viewBottom, barPaint);
                    c.drawText(title, left, viewBottom - alignBottom, textPaint);
                } else
                // 非组内最后一个 item
                {
                    c.drawRect(left, 0, right, barTopGap, barPaint);
                    c.drawText(title, left, barTopGap - alignBottom, textPaint);
                }
            } else
            // 除了第一个 item
            {
                // 组内第一个 item
                if (isFirstInGroup(position)) {
                    int viewTop = view.getTop();
                    c.drawRect(left, viewTop - barTopGap, right, viewTop, barPaint);
                    c.drawText(title, left, viewTop - alignBottom, textPaint);
                }
            }
        }
    }

    /**
     * 判断是不是组中的第一个位置
     *
     * @param pos
     * @return
     */
    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String groupId = mCallback.getGroupId(pos);// 当前这个 item 的 id
            String prevGroupId = mCallback.getGroupId(pos - 1);// 前一个 item 的 id
            Log.d(TAG, "pos:" + pos + ", " + "groupId:" + groupId + ", " + "prevGroupId:" + prevGroupId);
            return !groupId.equals(prevGroupId);
        }
    }

    /**
     * 判断是不是组中的最后一个位置
     *
     * @param pos
     * @return
     */
    private boolean isLastInGroup(int pos) {
        String groupId = mCallback.getGroupId(pos);// 当前这个 item 的 id
        String nextGroupId = mCallback.getGroupId(pos + 1);// 后一个 item 的 id
        Log.d(TAG, "pos:" + pos + ", " + "groupId:" + groupId + ", " + "nextGroupId:" + nextGroupId);
        return !groupId.equals(nextGroupId);
    }

    public interface DecorationCallback {
        String getGroupId(int position);

        String getGroupTitle(int position);
    }
}
