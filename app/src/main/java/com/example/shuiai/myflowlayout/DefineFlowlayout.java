package com.example.shuiai.myflowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuiai@dianjia.io
 * @Company 杭州木瓜科技有限公司
 * @date 2016/7/11
 */
public class DefineFlowlayout extends ViewGroup {
    public DefineFlowlayout(Context context) {
        super(context);
    }

    public DefineFlowlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefineFlowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DefineFlowlayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heigjtMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        //如果是wrap_content的情况下记录宽和高
        int width = 0;
        int height = 0;
        //记录每一行的宽度
        int lineWidth = 0;
        //记录每一行的高度
        int lineHeight = 0;
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            //当前子空间占的宽度
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (childWidth + lineWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;

            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);

            }
            if (i == cCount - 1) {
                width = Math.max(childWidth, lineWidth);
                height += lineHeight;
            }


        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY ? sizeWidth : width), (heigjtMode == MeasureSpec.EXACTLY ? sizeHeight : height));
    }

    //存储所有View
    private List<List<View>> mAllViews = new ArrayList<>();

    //记录每行的最大高度
    private List<Integer> mLineHeight = new ArrayList<>();

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        mAllViews.clear();
        ;
        mLineHeight.clear();
        int width = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        for (int n = 0; n < cCount; n++) {
            View view = getChildAt(n);
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();
            MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
            if (childWidth + params.leftMargin + params.rightMargin + lineWidth > width) {//需要换行
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                lineWidth = 0;
                lineViews = new ArrayList<>();
            }
            //不需要换行
            lineWidth += childWidth + params.leftMargin + params.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + params.topMargin + params.bottomMargin);
            lineViews.add(view);
        }
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);
        int left = 0;
        int top = 0;
//得到总行数
        int lineNum = mAllViews.size();
        for (int x = 0; x < lineNum; x++) {
            lineViews = mAllViews.get(x);
            lineHeight = mLineHeight.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View childView = lineViews.get(j);
                if (childView.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int lc = left + marginLayoutParams.leftMargin;
                int tc = top + marginLayoutParams.topMargin;
                int rc = lc + childView.getMeasuredWidth();
                int bc = tc + childView.getMeasuredHeight();
                childView.layout(lc, tc, rc, bc);
                left += childView.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }
}
