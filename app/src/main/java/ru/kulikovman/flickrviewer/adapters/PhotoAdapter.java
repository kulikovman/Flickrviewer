package ru.kulikovman.flickrviewer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import ru.kulikovman.flickrviewer.FullscreenActivity;
import ru.kulikovman.flickrviewer.R;
import ru.kulikovman.flickrviewer.models.Photo;

public class PhotoAdapter extends RealmRecyclerViewAdapter<Photo, PhotoAdapter.PhotoHolder> {
    private static final String TAG = "PhotoAdapter";

    private OrderedRealmCollection<Photo> mPhotos;
    private Context mContext;
    private Photo mPhoto;

    public PhotoAdapter(Context context, OrderedRealmCollection<Photo> photos) {
        super(photos, true);
        mContext = context;
        mPhotos = photos;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.image_container);
            mItemImageView.setOnClickListener(this);
        }

        public void bindPhoto(Photo photo) {
            mPhoto = photo;
            Picasso.get()
                    .load(photo.getUrlN())
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View v) {
            // https://farm1.staticflickr.com/2/1418878_1e92283336_m.jpg
            // farm-id: 1
            // server-id: 2
            // photo-id: 1418878
            // secret: 1e92283336
            // size: m

            // Формируем ссылку на оригинал фото
            String urlFullSize = "https://farm" + mPhoto.getFarm() + ".staticflickr.com/"
                    + mPhoto.getServer() + "/" + mPhoto.getId() + "_" + mPhoto.getSecret()
                    + "_o.jpg";

            // Передаем ссылку в фуллскрин активити
            Intent intent = new Intent(mContext, FullscreenActivity.class);
            intent.putExtra("url_full_size", urlFullSize);
            mContext.startActivity(intent);
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
        Photo photo = mPhotos.get(position);
        photoHolder.bindPhoto(photo);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }
}
