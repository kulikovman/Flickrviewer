package ru.kulikovman.flickrviewer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import ru.kulikovman.flickrviewer.R;
import ru.kulikovman.flickrviewer.models.PhotoPreview;

public class PhotoAdapter extends RealmRecyclerViewAdapter<PhotoPreview, PhotoAdapter.PhotoHolder> {
    private static final String TAG = "PhotoAdapter";

    private Context mContext;
    private OrderedRealmCollection<PhotoPreview> mPhotoPreviews;

    public PhotoAdapter(Context context, OrderedRealmCollection<PhotoPreview> photoPreviews) {
        super(photoPreviews, true);
        mContext = context;
        mPhotoPreviews = photoPreviews;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.image_container);
        }

        public void bindPhoto(PhotoPreview photoPreview) {
            Picasso.get()
                    .load(photoPreview.getUrl())
                    .into(mItemImageView);
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
        PhotoPreview photoPreview = mPhotoPreviews.get(position);
        photoHolder.bindPhoto(photoPreview);
    }

    @Override
    public int getItemCount() {
        return  mPhotoPreviews.size();
    }
}
