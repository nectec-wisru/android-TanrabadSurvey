package th.or.nectec.tanrabad.survey.presenter.maps;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import th.or.nectec.tanrabad.survey.R;
import th.or.nectec.tanrabad.survey.utils.android.ResourceUtils;

public class MapMarkerFragment extends TanrabadSupportMapFragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, OnMapReadyCallback {

    public static final String FRAGMENT_TAG = "map_marker_fragment";
    public static final String ARGS_LOCATION = "args_location";
    Marker marker;

    public static MapMarkerFragment newInstance() {
        MapMarkerFragment mapMarkerFragment = new MapMarkerFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_MOVE_TO_MY_LOCATION, true);
        mapMarkerFragment.setArguments(args);
        return mapMarkerFragment;
    }

    public static MapMarkerFragment newInstanceWithLocation(LatLng location) {
        MapMarkerFragment mapMarkerFragment = new MapMarkerFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_MOVE_TO_MY_LOCATION, false);
        args.putParcelable(ARGS_LOCATION, location);
        mapMarkerFragment.setArguments(args);
        return mapMarkerFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnMarkerDragListener(this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        LatLng location = getArguments().getParcelable(ARGS_LOCATION);
        if (location != null)
            return;

        if (getLastLocation() != null) {
            location = new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude());
            addMarker(location);
            moveToLocation(location);
        }
    }

    public void addMarker(LatLng position) {
        addMarker(position, ResourceUtils.from(getActivity()).getColor(R.color.shock_pink), false);
    }

    private void addMarker(LatLng position, int color, boolean draggable) {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(draggable);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hsv[0]));
        markerOptions.position(position);
        marker = googleMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = getArguments().getParcelable(ARGS_LOCATION);
        if (location != null) {
            addDraggableMarker(location);
            moveToLocation(location);
        }
    }

    public void addDraggableMarker(LatLng position) {
        addMarker(position, ResourceUtils.from(getActivity()).getColor(R.color.shock_pink), true);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        removeMarkedLocation();
        addDraggableMarker(latLng);
    }

    public void removeMarkedLocation() {
        marker.remove();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        getMap().getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        getMap().getUiSettings().setScrollGesturesEnabled(true);
    }

    public LatLng getMarkedLocation() {
        return marker.getPosition();
    }
}