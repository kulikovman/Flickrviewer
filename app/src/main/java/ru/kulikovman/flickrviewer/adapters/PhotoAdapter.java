package ru.kulikovman.flickrviewer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

        public void bindTempDrawable(Drawable drawable) {
            mItemImageView.getLayoutParams().width = FrameLayout.LayoutParams.WRAP_CONTENT;
            mItemImageView.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
            mItemImageView.requestLayout();
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            mItemImageView.getLayoutParams().height = convertDpToPx(mContext, 120);
            mItemImageView.requestLayout();
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

        // Временная картинка
        Drawable placeholder = mContext.getResources().getDrawable(R.drawable.ic_autorenew_24dp);
        photoHolder.bindTempDrawable(placeholder);

        // Загрузка реальной картинки
        mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
    }

    @Override
    public int getItemCount() {
        return  mGalleryItems.size();
    }

    public void setGalleryItems(List<GalleryItem> items) {
        mGalleryItems = items;
    }
    public int convertDpToPx(Context context, int valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp,
                context.getResources().getDisplayMetrics());
    }
}
