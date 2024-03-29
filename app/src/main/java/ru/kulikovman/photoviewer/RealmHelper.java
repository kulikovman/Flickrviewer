package ru.kulikovman.photoviewer;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import ru.kulikovman.photoviewer.models.photo.Photo;

public class RealmHelper {
    private static RealmHelper sRealmHelper;
    private Realm mRealm;

    public static RealmHelper get() {
        if (sRealmHelper == null) {
            sRealmHelper = new RealmHelper();
        }
        return sRealmHelper;
    }

    private RealmHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public boolean isExistUrl(String url) {
        Photo photo = mRealm.where(Photo.class)
                .equalTo(Photo.URL, url)
                .findFirst();

        return photo != null;
    }

    OrderedRealmCollection<Photo> getPhotoList() {
        return mRealm.where(Photo.class)
                .findAll();
    }
}
