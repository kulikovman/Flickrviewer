package ru.kulikovman.flickrviewer.models;

public class Coordinates {
    double mLat;
    double mLon;

    public Coordinates(double lat, double lon) {
        mLat = lat;
        mLon = lon;
    }

    public double getLat() {
        return mLat;
    }

    public double getLon() {
        return mLon;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Coordinates)){
            return false;
        }

        Coordinates coordinates = (Coordinates) obj;
        double lat = coordinates.getLat();
        double lon = coordinates.getLon();

        return mLat == lat && mLon == lon;
    }
}
