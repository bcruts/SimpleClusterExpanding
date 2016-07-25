package com.briancruts.clusterexpanding;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;

/**
 * Created by Brian.Cruts on 7/20/2016.
 */

public class SimpleExpander implements ClusterManager.OnClusterItemClickListener {

    private final int EXPAND_CLUSTER_ZOOM_LEVEL = 10;
    private final int MAX_RENDERED_MARKERS = 11;
    private final float RADIUS = .15f;

    private final GoogleMap mMap;
    private final ClusterManager mClusterManager;
    private LatLng clusterPosition;
    private LatLng newPositionOnCircle;
    private final ArrayList<Item> clickedCluster = new ArrayList<>();
    private final ArrayList<Polyline> lines = new ArrayList<>();
    private Item centerItem;
    private PolylineOptions polylineOptions;

    private float offsetDegrees;
    private float distanceFromPoint;
    private boolean isAnythingExpanded = false;
    private boolean tooManyMarkers = false;

    public SimpleExpander(GoogleMap map, final ClusterManager clusterManager, Context context) {
        mMap = map;
        mClusterManager = clusterManager;
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setRenderer(new InfoMarkerRenderer(context, mMap, mClusterManager));


    }

    public void expandListener(Cluster<Item> cluster) {
        if (isAnythingExpanded) {
            collapse();
        }
        clusterPosition = cluster.getPosition();
        centerCameraOnCluster(cluster.getPosition());
        expandMarkerCluster(cluster);
        addLines(clickedCluster);

    }

    private void expandMarkerCluster(Cluster<Item> cluster) {
        offsetDegrees = -90f;
        distanceFromPoint = 360 / cluster.getSize();
        if (cluster.getSize() >= MAX_RENDERED_MARKERS) {
            tooManyMarkers = true;
        }
        for (Item item : cluster.getItems()) {
            newPositionOnCircle = PointOnCircle(RADIUS, offsetDegrees, item.getPosition());
            Item newItem = new Item(newPositionOnCircle.latitude, newPositionOnCircle.longitude);
            mClusterManager.removeItem(item);
            mClusterManager.addItem(newItem);
            clickedCluster.add(newItem);
            offsetDegrees += distanceFromPoint;
        }
        centerItem = new Item(clusterPosition.latitude, clusterPosition.longitude);
        mClusterManager.addItem(centerItem);
        mClusterManager.cluster();
        isAnythingExpanded = true;
    }

    private void collapseMarkerCluster() {

        mClusterManager.removeItem(centerItem);
        for (Item item : clickedCluster) {
            mClusterManager.removeItem(item);
            mClusterManager.addItem(new Item(clusterPosition.latitude, clusterPosition.longitude));
            mClusterManager.cluster();

        }
        clickedCluster.clear();
        tooManyMarkers = false;
    }

    private void centerCameraOnCluster(LatLng clusterPosition) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clusterPosition, EXPAND_CLUSTER_ZOOM_LEVEL));
    }

    private LatLng PointOnCircle(float radius, float angle, LatLng center) {
        double x = (radius * Math.cos(angle * Math.PI / 180F)) + center.latitude;
        double y = (radius * Math.sin(angle * Math.PI / 180F)) + center.longitude;
        return new LatLng(x, y);
    }

    private void addLines(ArrayList<Item> cluster) {
        for (Item item : cluster) {
            polylineOptions = new PolylineOptions()
                    .color(Color.BLACK)
                    .width(5)
                    .add(new LatLng(clusterPosition.latitude, clusterPosition.longitude))
                    .add(item.getPosition());
            lines.add(mMap.addPolyline(polylineOptions));
        }
    }

    private void removeLines() {
        for (Polyline line : lines) {
            line.remove();
        }
    }

    private void collapse() {
        collapseMarkerCluster();
        removeLines();
        isAnythingExpanded = false;
    }


    @Override
    public boolean onClusterItemClick(ClusterItem clusterItem) {
        if (isAnythingExpanded) {
            if (clusterItem.getPosition().latitude == clusterPosition.latitude) {
                if (clusterItem.getPosition().longitude == clusterPosition.longitude) {
                    collapse();
                }
            }
        }
        return false;
    }

    public class InfoMarkerRenderer extends DefaultClusterRenderer<Item> {

        public InfoMarkerRenderer(Context context, GoogleMap map, ClusterManager<Item> clusterManager) {
            super(context, map, clusterManager);
            //constructor
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            if (isAnythingExpanded && tooManyMarkers) {
                return cluster.getSize() > 200;
            } else {
                return cluster.getSize() > 1;
            }
        }
    }


}
