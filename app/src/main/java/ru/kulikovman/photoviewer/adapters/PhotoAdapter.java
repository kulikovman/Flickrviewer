package ru.kulikovman.photoviewer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import ru.kulikovman.photoviewer.FullscreenActivity;
import ru.kulikovman.photoviewer.R;
import ru.kulikovman.photoviewer.models.photo.Photo;

public class PhotoAdapter extends RealmRecyclerViewAdapter<Photo, PhotoAdapter.PhotoHolder> {
    private static final String TAG = "PhotoAdapter";

    private OrderedRealmCollection<Photo> mPhotos;
    private Context mContext;

    public PhotoAdapter(Context context, OrderedRealmCollection<Photo> photos) {
        super(photos, true);
        mContext = context;
        mPhotos = photos;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mItemImageView;
        private Photo mPhoto;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.image_container);
            mItemImageView.setOnClickListener(this);
        }

        public void bindPhoto(Photo photo) {
            mPhoto = photo;
            Picasso.get()
                    .load(photo.getUrlN())
                    .config(Bitmap.Config.ARGB_4444)
                    .centerCrop()
                    .fit()
                    .noFade()
                    .placeholder(R.drawable.loading_preview)
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View v) {
            // Передаем ссылки и заголовок в фуллскрин активити
            Intent intent = new Intent(mContext, FullscreenActivity.class);
            intent.putExtra("two_link_and_title", mPhoto.getTwoLinkAndTitle());
            mContext.startActivity(intent);
        }
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.photo_item, parent, false);
        return new PhotoHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int position) {
        Photo photo = mPhotos.get(position);
        photoHolder.bindPhoto(photo);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }
}
