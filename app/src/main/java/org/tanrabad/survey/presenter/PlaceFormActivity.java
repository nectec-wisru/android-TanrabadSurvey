/*
 * Copyright (c) 2016 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tanrabad.survey.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import org.joda.time.DateTime;
import org.tanrabad.survey.TanrabadApp;
import org.tanrabad.survey.presenter.maps.LocationUtils;
import org.tanrabad.survey.repository.BrokerPlaceRepository;
import org.tanrabad.survey.repository.adapter.ThaiWidgetDistrictRepository;
import org.tanrabad.survey.repository.adapter.ThaiWidgetProvinceRepository;
import org.tanrabad.survey.repository.adapter.ThaiWidgetSubdistrictRepository;
import org.tanrabad.survey.utils.MapUtils;
import org.tanrabad.survey.utils.alert.Alert;
import org.tanrabad.survey.utils.android.SoftKeyboard;
import org.tanrabad.survey.utils.android.TwiceBackPressed;
import org.tanrabad.survey.utils.map.MarkerUtil;
import org.tanrabad.survey.validator.SavePlaceValidator;
import org.tanrabad.survey.validator.UpdatePlaceValidator;
import org.tanrabad.survey.validator.ValidatorException;
import org.tanrabad.survey.domain.place.*;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.field.Location;
import org.tanrabad.survey.entity.lookup.PlaceSubType;
import org.tanrabad.survey.entity.lookup.PlaceType;
import org.tanrabad.survey.R;
import th.or.nectec.thai.widget.address.AddressPicker;
import th.or.nectec.thai.widget.address.AddressPickerDialog;

import java.util.UUID;

public class PlaceFormActivity extends TanrabadActivity implements View.OnClickListener,
        PlaceSavePresenter, PlacePresenter {

    public static final String PLACE_TYPE_ID_ARG = "place_category_id_arg";
    public static final String PLACE_UUID_ARG = "place_uuid_arg";

    public static final int ADD_PLACE_REQ_CODE = 30000;
    private Place place;
    private PlaceRepository placeRepository = BrokerPlaceRepository.getInstance();

    private EditText placeNameView;
    private AddressPicker addressSelectView;
    private AppCompatSpinner placeTypeSelectorView;
    private View placeSubtypeLayout;
    private TextView placeSubtypeLabelView;
    private AppCompatSpinner placeSubtypeSelectorView;
    private Button editLocationButton;
    private FrameLayout addLocationBackground;
    private TwiceBackPressed twiceBackPressed;
    private SupportMapFragment mapFragment;

    public static void startAdd(Activity activity, int placeTypeId) {
        Intent intent = new Intent(activity, PlaceFormActivity.class);
        intent.putExtra(PlaceFormActivity.PLACE_TYPE_ID_ARG, placeTypeId);
        activity.startActivityForResult(intent, ADD_PLACE_REQ_CODE);
    }

    public static void startEdit(Activity activity, Place place) {
        Intent intent = new Intent(activity, PlaceFormActivity.class);
        intent.putExtra(PlaceFormActivity.PLACE_UUID_ARG, place.getId().toString());
        intent.putExtra(PlaceFormActivity.PLACE_TYPE_ID_ARG, place.getType());
        activity.startActivityForResult(intent, ADD_PLACE_REQ_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_form);
        setupViews();
        setupHomeButton();
        setupTwiceBackPressed();
        setupPlaceTypeSelector();
        setupMap();
        loadPlaceData();
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        if (mapFragment.getView() != null)
            mapFragment.getView().setClickable(false);
    }

    private void setupViews() {
        placeNameView = (EditText) findViewById(R.id.place_name);
        addressSelectView = (AddressPicker) findViewById(R.id.address_select);
        AddressPickerDialog popup = new AddressPickerDialog(this)
                .setProvinceRepository(new ThaiWidgetProvinceRepository())
                .setDistrictRepository(new ThaiWidgetDistrictRepository())
                .setSubDistrictRepository(new ThaiWidgetSubdistrictRepository());
        addressSelectView.setPopup(popup);
        placeTypeSelectorView = (AppCompatSpinner) findViewById(R.id.place_type_selector);
        placeSubtypeLayout = findViewById(R.id.place_subtype_layout);
        placeSubtypeLabelView = (TextView) findViewById(R.id.place_subtype_label);
        placeSubtypeSelectorView = (AppCompatSpinner) findViewById(R.id.place_subtype_selector);
        addLocationBackground = (FrameLayout) findViewById(R.id.add_location_background);
        addLocationBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftKeyboard.hideOn(PlaceFormActivity.this);
            }
        });
        editLocationButton = (Button) findViewById(R.id.edit_location);
        editLocationButton.setVisibility(View.GONE);
        editLocationButton.setOnClickListener(this);
        Button addMarker = (Button) findViewById(R.id.add_marker);
        addMarker.setOnClickListener(this);
        addMarker.setText(R.string.define_place_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupTwiceBackPressed() {
        twiceBackPressed = new TwiceBackPressed(this);
    }

    private void setupPlaceTypeSelector() {
        final ReferenceEntityAdapter placeAdapter =
                ReferenceEntityAdapter.buildPlaceTypeForAdd(this, AccountUtils.canAddOrEditVillage());
        placeTypeSelectorView.setAdapter(placeAdapter);
        placeTypeSelectorView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupPlaceSubtypeSpinner((PlaceType) placeAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        placeTypeSelectorView.setSelection(placeAdapter.getPosition(getPlaceTypeId()));
    }

    private void setupPlaceSubtypeSpinner(PlaceType selectedPlaceType) {
        placeSubtypeLabelView.setText(
                String.format(getString(R.string.place_subtype_label), selectedPlaceType.getName()));
        ReferenceEntityAdapter placeSubTypeAdapter =
                ReferenceEntityAdapter.buildPlaceSubType(this, selectedPlaceType.getId());
        placeSubtypeSelectorView.setAdapter(placeSubTypeAdapter);
        if (placeSubTypeAdapter.getCount() > 0) {
            placeSubtypeLayout.setVisibility(View.VISIBLE);
            placeSubtypeSelectorView.setSelection(placeSubTypeAdapter.getPosition(place.getSubType()));
        } else {
            placeSubtypeLayout.setVisibility(View.GONE);
        }

    }

    private int getPlaceTypeId() {
        return getIntent().getIntExtra(PLACE_TYPE_ID_ARG, PlaceType.WORSHIP);
    }

    private void loadPlaceData() {
        if (TextUtils.isEmpty(getPlaceUuid())) {
            place = Place.withName(null);
        } else {
            PlaceController placeController = new PlaceController(placeRepository, this);
            placeController.showPlace(UUID.fromString(getPlaceUuid()));
        }
    }

    private String getPlaceUuid() {
        return getIntent().getStringExtra(PLACE_UUID_ARG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (TextUtils.isEmpty(getPlaceUuid())) {
                    doSaveData();
                } else {
                    doUpdateData();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSaveData() {
        try {
            getPlaceFieldData();
            PlaceSaver placeSaver = new PlaceSaver(placeRepository, new SavePlaceValidator(), this);
            placeSaver.save(place);
        } catch (ValidatorException exception) {
            Alert.highLevel().show(exception.getMessageId());
        }
    }

    private void getPlaceFieldData() {
        place.setName(placeNameView.getText().toString().trim());
        int placeTypeId = ((PlaceType) placeTypeSelectorView.getSelectedItem()).getId();
        place.setType(placeTypeId);
        PlaceSubType placeSubType = ((PlaceSubType) placeSubtypeSelectorView.getSelectedItem());
        place.setSubType(placeSubType.getId());
        place.setSubdistrictCode(addressSelectView.getAddress() == null ? null
                : addressSelectView.getAddress().getCode());
        place.setUpdateTimestamp(DateTime.now().toString());
        place.setUpdateBy(AccountUtils.getUser().getUsername());
    }

    private void doUpdateData() {
        getPlaceFieldData();
        try {
            PlaceSaver placeSaver = new PlaceSaver(placeRepository, new UpdatePlaceValidator(), this);
            placeSaver.update(place);
        } catch (ValidatorException exception) {
            Alert.highLevel().show(exception.getMessageId());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_activity_place_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MapMarkerActivity.MARK_LOCATION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Location placeLocation = LocationUtils.convertJsonToLocation(
                            data.getStringExtra(MapMarkerActivity.MAP_LOCATION));
                    place.setLocation(placeLocation);
                    setupPreviewMapWithPosition(placeLocation);
                }
        }
    }

    private void setupPreviewMapWithPosition(final Location location) {
        addLocationBackground.setVisibility(View.GONE);
        editLocationButton.setVisibility(View.VISIBLE);
        editLocationButton.setOnClickListener(this);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                googleMap.addMarker(MarkerUtil.buildMarkerOption(location));
                googleMap.moveCamera(MapUtils.locationZoom(location, 15));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (twiceBackPressed.onTwiceBackPressed()) {
            finish();
        }
    }

    public void onRootViewClick(View view) {
        SoftKeyboard.hideOn(this);
    }

    @Override
    public void displaySaveSuccess() {
        setResult(RESULT_OK);
        finish();
        SurveyBuildingHistoryActivity.open(PlaceFormActivity.this, place);
        TanrabadApp.action().addPlace(place);
    }

    @Override
    public void displaySaveFail() {
        Alert.highLevel().show(R.string.save_place_failed);
    }

    @Override
    public void alertCannotSaveVillageType() {

    }

    @Override
    public void displayUpdateSuccess() {
        setResult(RESULT_OK);
        finish();
        TanrabadApp.action().updatePlace(place);
    }


    @Override
    public void displayUpdateFail() {
        Alert.highLevel().show(R.string.save_place_failed);
    }

    @Override
    public void displayPlace(Place place) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.edit_place);

        this.place = place;
        placeNameView.setText(place.getName());

        placeTypeSelectorView.setEnabled(false);

        if (!TextUtils.isEmpty(place.getSubdistrictCode()))
            addressSelectView.setAddressCode(place.getSubdistrictCode());

        if (place.getLocation() != null)
            setupPreviewMapWithPosition(place.getLocation());
    }


    @Override
    public void alertPlaceNotFound() {
        this.place = Place.withName(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_marker:
                MapMarkerActivity.startAdd(PlaceFormActivity.this);
                break;
            case R.id.edit_location:
                MapMarkerActivity.startEdit(PlaceFormActivity.this, place.getLocation());
                break;
        }
    }
}