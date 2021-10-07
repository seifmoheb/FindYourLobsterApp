package com.app.findyourlobster.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.findyourlobster.R;

import java.util.ArrayList;

public class SquaredRecycleAdapter extends RecyclerView.Adapter<SquaredRecycleAdapter.SquaredViewHolder> {
    private ArrayList<squares> list;

    public SquaredRecycleAdapter(ArrayList<squares> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SquaredRecycleAdapter.SquaredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.squared_recycler, parent, false);
        return new SquaredRecycleAdapter.SquaredViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SquaredRecycleAdapter.SquaredViewHolder holder, int position) {
        holder.text.setText(list.get(position).text);
        holder.image.setImageDrawable(list.get(position).image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SquaredViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ImageView image;

        public SquaredViewHolder(@NonNull View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);

        }
    }
}
