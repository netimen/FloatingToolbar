/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.06.15
 */
package com.netimen.floatingtoolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Contains the action views and layouts them so they fit the maximum width and distributes them to several containers navigable via more/back buttons if needed
 * CUR stretching
 */
@SuppressLint("ViewConstructor")
public class Panel<T> extends FrameLayout {
    private int currentContainerId;
    private final Adapter adapter;
    private int visibleActionPosition;

    public Panel(Context context, Adapter adapter) {
        super(context);
        this.adapter = adapter;
        setBackgroundColor(Color.TRANSPARENT);
//        setBackgroundColor(Color.BLUE);
    }

    public void relayout(int containerWidth) {
        removeAllViews();
        currentContainerId = 0;
        int currentContainerWidth = 0, containerToShow = 0;
        for (int itemId = 0; itemId < adapter.getCount(); itemId++) {
            View actionView = initView(adapter.getView(itemId, null, null), new OnClickListener() {
                @Override
                public void onClick(View v) { // CUR make a field
                    final FloatingToolbar.Listener<T> listener = getToolbar().getListener();
                    if (listener != null)
                        listener.actionSelected(getViewAction(v));
                }
            });
            actionView.setTag(itemId);

            if (currentContainerWidth + actionView.getMeasuredWidth() > containerWidth) {
                final View moreButton = createMoreButton();
                addViewToContainer(moreButton);
                if (currentContainerWidth + moreButton.getMeasuredWidth() > containerWidth) { // if even 'more' button doesn't fit, we need to remove last action view and add it to the next container
                    actionView = getCurrentContainer().getChildAt(getCurrentContainer().getChildCount() - 2); // last added action view
                    getCurrentContainer().removeView(actionView);
                    itemId--;
                }
                currentContainerId++;
            }

            if (getCurrentContainer() == null) {
                final Container container = new Container(getContext(), currentContainerId);
                addView(container, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

                if (container.hasBackButton()) {
                    final View backButton = createBackButton();
                    addViewToContainer(backButton);
                    currentContainerWidth = backButton.getMeasuredWidth();
                } else
                    currentContainerWidth = 0;

                if (visibleActionPosition >= itemId)
                    containerToShow = currentContainerId;
            }
            addViewToContainer(actionView);
            currentContainerWidth += actionView.getMeasuredWidth();
        }

        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final Container container = (Container) getChildAt(0);
            container.adjustBackMoreButtonsHeight();
            if (maxHeight < container.getMaxChildHeight())
                maxHeight = container.getMaxChildHeight();
        }
        showContainer(containerToShow);

        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, maxHeight, Gravity.CENTER));
        measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)); // so now getMeasuredHeight also returns maxHeight. Needed to adjust parent size
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), heightMeasureSpec); // no horizontal crop
    }

    /**
     * specifies view's height and sets gravity to CENTER
     */
    private void addViewToContainer(View view) {
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        getCurrentContainer().addView(view, layoutParams);
    }

    private void showContainer(int containerId) {
        getToolbar().changePanels(getChildAt(containerId), getChildAt(currentContainerId));
        currentContainerId = containerId;
        visibleActionPosition = getViewPositionInAdapter(getCurrentContainer().getFirstActionView());
    }

    private Container getCurrentContainer() {
        return (currentContainerId == -1 || currentContainerId >= getChildCount()) ? null : (Container) getChildAt(currentContainerId);
    }

    /// more/back buttons

    private View createBackButton() { // CUR inline, make ClickListener a field
        return initView(inflate(getContext(), getToolbar().backButtonLayout, null), new OnClickListener() {
            @Override
            public void onClick(View v) {
                showContainer(currentContainerId - 1);
            }
        });
    }

    private View createMoreButton() {
        return initView(inflate(getContext(), getToolbar().moreButtonLayout, null), new OnClickListener() {
            @Override
            public void onClick(View v) {
                showContainer(currentContainerId + 1);
            }
        });
    }

    /**
     * measures and sets ClickListener
     */
    private View initView(View view, OnClickListener onClickListener) {
        view.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.setOnClickListener(onClickListener);
        return view;
    }

    @SuppressWarnings("unchecked")
    private T getViewAction(View v) {
        return (T) adapter.getItem(getViewPositionInAdapter(v));
    }

    private int getViewPositionInAdapter(View v) {
        return (int) v.getTag();
    }

    @SuppressWarnings("unchecked")
    private FloatingToolbar<T> getToolbar() {
        return (FloatingToolbar<T>) getParent();
    }
}
