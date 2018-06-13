package ru.kulikovman.flickrviewer.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        public PhotoHolder(View itemView) {
            super(itemView);
        }
    }
}
