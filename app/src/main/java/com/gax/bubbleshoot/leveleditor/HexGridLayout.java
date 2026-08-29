package com.gax.bubbleshoot.leveleditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * BubbleSystem.java ka EXACT wahi hex-packing formula yahan replicate kiya
 * hai (odd rows ko half-width se shift karna) - taaki editor ka grid
 * gameplay screen jaisa hi (touching circles, offset rows) dikhe, plain
 * square-grid-with-gaps jaisa nahi.
 * <p>
 * Formula (BubbleSystem.java se): x = col*cellWidth + (row%2!=0 ? cellWidth/2 : 0)
 * cellHeight = cellWidth * 0.86 (GRID_HEIGHT/GRID_WIDTH ratio wahi se)
 */
public class HexGridLayout extends ViewGroup {

    private static final float HEIGHT_RATIO = 0.86f; // BubbleSystem.GRID_HEIGHT / GRID_WIDTH
    private static final int COLUMN = 11;

    public HexGridLayout(Context context) {
        super(context);
    }

    public HexGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float cellWidth = width / (COLUMN + 0.5f); // +0.5 taaki odd-row offset ke liye extra jagah rahe
        float cellHeight = cellWidth * HEIGHT_RATIO;

        int rows = getChildCount() / COLUMN;
        int totalHeight = (int) (rows * cellHeight + cellHeight / 2f);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(
                    MeasureSpec.makeMeasureSpec((int) cellWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec((int) cellHeight, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        float cellWidth = width / (COLUMN + 0.5f);
        float cellHeight = cellWidth * HEIGHT_RATIO;

        for (int i = 0; i < getChildCount(); i++) {
            int row = i / COLUMN;
            int col = i % COLUMN;
            float x = col * cellWidth + ((row % 2) != 0 ? cellWidth / 2f : 0);
            float y = row * cellHeight;
            View child = getChildAt(i);
            child.layout((int) x, (int) y, (int) (x + cellWidth), (int) (y + cellHeight));
        }
    }

    /** Grid ko poora clear karke naye cell-count ke saath rebuild karta hai. */
    public void rebuild(EditorCell[] cells, OnCellChangedListener listener) {
        removeAllViews();
        for (int i = 0; i < cells.length; i++) {
            int position = i;
            ImageView cell = new ImageView(getContext());
            cell.setScaleType(ImageView.ScaleType.FIT_CENTER);
            updateCellImage(cell, cells[position]);
            cell.setOnClickListener(v -> {
                cells[position] = cells[position].next();
                updateCellImage(cell, cells[position]);
                listener.onCellChanged();
            });
            addView(cell);
        }
        requestLayout();
    }

    private void updateCellImage(ImageView imageView, EditorCell cell) {
        if (cell.drawableRes != 0) {
            imageView.setImageResource(cell.drawableRes);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    public interface OnCellChangedListener {
        void onCellChanged();
    }
}
