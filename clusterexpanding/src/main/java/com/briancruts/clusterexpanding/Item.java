package com.briancruts.clusterexpanding;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Brian.Cruts on 7/20/2016.
 */

public class Item implements ClusterItem{

    private final LatLng mPosition;

    public Item(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
