package com.briancruts.simpleclusterexpanding;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.briancruts.clusterexpanding.Item;
import com.briancruts.clusterexpanding.SimpleExpander;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

public class DemoMapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ClusterManager mClusterManager;
    private SimpleExpander simpleExpander;

    private final int EXPAND_CLUSTER_ZOOM_LEVEL = 10;
    private final int ZOOM_IN_UNIT = 2;
    private final int NUM_MARKERS = 15; //Slows down with a lot of markers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager(this, mMap);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng center = new LatLng(32.1048861, 35.1753109);
        addDemoMarkersOn(center);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,10f));

        simpleExpander = new SimpleExpander(mMap, mClusterManager, getApplicationContext());

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener() {
            @Override
            public boolean onClusterClick(Cluster cluster) {
                if (mMap.getCameraPosition().zoom >= EXPAND_CLUSTER_ZOOM_LEVEL) {
                    simpleExpander.expandListener(cluster);
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            cluster.getPosition(),
                            mMap.getCameraPosition().zoom + ZOOM_IN_UNIT));
                }
                return true;
            }
        });


    }

    private void addDemoMarkersOn(LatLng center) {
        for(int i = 0; i < NUM_MARKERS; i++) {
            mClusterManager.addItem(new Item(center.latitude, center.longitude));
        }
        mClusterManager.cluster();
    }





}
