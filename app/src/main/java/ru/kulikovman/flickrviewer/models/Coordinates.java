package ru.kulikovman.flickrviewer.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Coordinates {
    double mLat;
    double mLon;

    public Coordinates(double lat, double lon) {
        mLat = new BigDecimal(lat).setScale(2, RoundingMode.HALF_UP).doubleValue();
        mLon = new BigDecimal(lon).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
