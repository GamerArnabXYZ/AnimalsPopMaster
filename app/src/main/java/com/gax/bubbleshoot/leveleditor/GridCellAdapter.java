package com.gax.bubbleshoot.leveleditor;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gax.bubbleshoot.R;

public class GridCellAdapter extends RecyclerView.Adapter<GridCellAdapter.CellViewHolder> {

    private final EditorCell[] mCells;

    public GridCellAdapter(EditorCell[] cells) {
        mCells = cells;
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_editor_cell, parent, false);
        return new CellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        EditorCell cell = mCells[position];
        holder.txtCell.setText(cell.label);
        holder.txtCell.setBackgroundColor(Color.parseColor(cell.colorHex));
        holder.txtCell.setOnClickListener(v -> {
            mCells[position] = cell.next();
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return mCells.length;
    }

    public static class CellViewHolder extends RecyclerView.ViewHolder {
        final TextView txtCell;

        public CellViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCell = (TextView) itemView;
        }
    }
}
