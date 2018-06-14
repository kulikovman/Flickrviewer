package ru.kulikovman.flickrviewer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.kulikovman.flickrviewer.R;
import ru.kulikovman.flickrviewer.ThumbnailDownloader;
import ru.kulikovman.flickrviewer.models.GalleryItem;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private static final String TAG = "PhotoAdapter";

    private Context mContext;
    private List<GalleryItem> mGalleryItems;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public PhotoAdapter(Context context, List<GalleryItem> galleryItems, ThumbnailDownloader<PhotoHolder> thumbnailDownloader) {
        mThumbnailDownloader = thumbnailDownloader;
        mContext = context;
        mGalleryItems = galleryItems;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.image_container);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gallery_item, parent, false);
        return new PhotoHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int position) {
        GalleryItem galleryItem = mGalleryItems.get(position);
        Drawable placeholder = mContext.getResources().getDrawable(R.drawable.ic_autorenew_24dp);
        photoHolder.bindDrawable(placeholder);
        mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
    }

    @Override
    public int getItemCount() {
        return  mGalleryItems.size();
    }

    public void setGalleryItems(List<GalleryItem> items) {
        mGalleryItems = items;
    }


}
