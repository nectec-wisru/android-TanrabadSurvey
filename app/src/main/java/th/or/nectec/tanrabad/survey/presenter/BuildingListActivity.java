/*
 * Copyright (c) 2015 NECTEC
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

package th.or.nectec.tanrabad.survey.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;

import java.util.List;
import java.util.UUID;

import th.or.nectec.tanrabad.domain.building.BuildingWithSurveyStatus;
import th.or.nectec.tanrabad.domain.building.BuildingWithSurveyStatusListPresenter;
import th.or.nectec.tanrabad.domain.place.PlaceController;
import th.or.nectec.tanrabad.domain.place.PlacePresenter;
import th.or.nectec.tanrabad.domain.survey.SurveyBuildingChooser;
import th.or.nectec.tanrabad.entity.Place;
import th.or.nectec.tanrabad.survey.R;
import th.or.nectec.tanrabad.survey.presenter.view.EmptyLayoutView;
import th.or.nectec.tanrabad.survey.repository.InMemoryBuildingRepository;
import th.or.nectec.tanrabad.survey.repository.InMemoryPlaceRepository;
import th.or.nectec.tanrabad.survey.repository.InMemorySurveyRepository;
import th.or.nectec.tanrabad.survey.repository.StubUserRepository;
import th.or.nectec.tanrabad.survey.utils.alert.Alert;
import th.or.nectec.tanrabad.survey.utils.prompt.AlertDialogPromptMessage;
import th.or.nectec.tanrabad.survey.utils.prompt.PromptMessage;

public class BuildingListActivity extends TanrabadActivity implements BuildingWithSurveyStatusListPresenter, PlacePresenter {

    public static final String PLACE_UUID_ARG = "place_uuid_arg";
    public static final String IS_NEW_SURVEY_ARG = "is_new_survey_arg";
    private RecyclerView buildingList;
    private BuildingWithSurveyStatusAdapter buildingAdapter;
    private Place place;
    private EmptyLayoutView emptyLayoutView;
    private SurveyBuildingChooser surveyBuildingChooser = new SurveyBuildingChooser(new StubUserRepository(), InMemoryPlaceRepository.getInstance(), InMemoryBuildingRepository.getInstance(), InMemorySurveyRepository.getInstance(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list);
        setupHomeButton();
        showPlaceName();
        setupBuildingList();
        setupSearchView();
        setupEmptyLayout();
        loadSurveyBuildingList();
    }

    private void showPlaceName() {
        PlaceController placeController = new PlaceController(InMemoryPlaceRepository.getInstance(), this);
        placeController.showPlace(getPlaceUuidFromIntent());
    }

    private UUID getPlaceUuidFromIntent() {
        String uuid = getIntent().getStringExtra(PLACE_UUID_ARG);
        return UUID.fromString(uuid);
    }

    private void setupBuildingList() {
        buildingAdapter = new BuildingWithSurveyStatusAdapter(this, BuildingIconMapping.getBuildingIcon(place));
        buildingList = (RecyclerView) findViewById(R.id.building_list);
        buildingList.setAdapter(buildingAdapter);
        buildingList.addItemDecoration(new SimpleDividerItemDecoration(this));
        buildingList.setLayoutManager(new LinearLayoutManager(this));
        buildingAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BuildingWithSurveyStatus building = buildingAdapter.getItem(position);
                SurveyActivity.open(BuildingListActivity.this, building.getBuilding());
            }
        });
        buildingAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                BuildingWithSurveyStatus building = buildingAdapter.getItem(position);
                BuildingFormActivity.startEdit(BuildingListActivity.this,
                        getPlaceUuidFromIntent().toString(),
                        building.getBuilding().getId().toString());
                return true;
            }
        });
        RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader) findViewById(R.id.card_header);
        recyclerViewHeader.attachTo(buildingList, true);
    }

    private void setupSearchView() {
        SearchView buildingSearchView = (SearchView) findViewById(R.id.building_search);
        buildingSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchString) {
                surveyBuildingChooser.searchSurveyBuildingOfPlaceByName(searchString, getPlaceUuidFromIntent().toString(), "sara");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                surveyBuildingChooser.searchSurveyBuildingOfPlaceByName(searchString, getPlaceUuidFromIntent().toString(), "sara");
                return true;
            }
        });
    }

    private void setupEmptyLayout() {
        emptyLayoutView = (EmptyLayoutView) findViewById(R.id.empty_layout);
        emptyLayoutView.setEmptyIcon(place.getType() == Place.TYPE_VILLAGE_COMMUNITY ?
                R.mipmap.ic_building_home : R.mipmap.ic_building);
        emptyLayoutView.setEmptyText(R.string.building_list_not_found);
        emptyLayoutView.setEmptyButtonText(R.string.add_building, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildingFormActivity.startAdd(BuildingListActivity.this, getPlaceUuidFromIntent().toString());
            }
        });
    }

    private void loadSurveyBuildingList() {
        surveyBuildingChooser.displaySurveyBuildingOf(getPlaceUuidFromIntent().toString(), "sara");
    }

    @Override
    public void displayPlace(Place place) {
        this.place = place;
        TextView placeName = (TextView) findViewById(R.id.place_name);
        placeName.setText(place.getName());
    }

    @Override
    public void alertBuildingsNotFound() {
        emptyLayoutView.setVisibility(View.VISIBLE);
        TextView buildingCountView = (TextView) findViewById(R.id.building_count);
        buildingCountView.setVisibility(View.GONE);
        buildingAdapter.clearData();
    }

    @Override
    public void displayAllSurveyBuildingList(List<BuildingWithSurveyStatus> buildingsWithSurveyStatuses) {
        emptyLayoutView.setVisibility(View.GONE);
        buildingAdapter.updateData(buildingsWithSurveyStatuses);
        buildingList.setAdapter(buildingAdapter);
        TextView buildingCountView = (TextView) findViewById(R.id.building_count);
        buildingCountView.setText(getString(R.string.format_building_count, buildingsWithSurveyStatuses.size()));
        buildingCountView.setVisibility(View.VISIBLE);
    }

    @Override
    public void alertUserNotFound() {
    }

    @Override
    public void alertPlaceNotFound() {
        Alert.highLevel().show(R.string.place_not_found);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_activity_building_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_building_menu:
                BuildingFormActivity.startAdd(BuildingListActivity.this, getPlaceUuidFromIntent().toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BuildingFormActivity.ADD_BUILDING_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    loadSurveyBuildingList();
                }
        }
    }

    @Override
    public void onBackPressed() {
        if (isNewSurvey()) {
            showFinishSurveyPrompt();
        } else {
            finish();
        }
    }

    private boolean isNewSurvey() {
        return getIntent().getBooleanExtra(IS_NEW_SURVEY_ARG, false);
    }

    private void showFinishSurveyPrompt() {
        PromptMessage promptMessage = new AlertDialogPromptMessage(this);
        promptMessage.setOnConfirm(getString(R.string.yes), new PromptMessage.OnConfirmListener() {
            @Override
            public void onConfirm() {
                openPlaceListActivity();
            }
        });
        promptMessage.setOnCancel(getString(R.string.no), null);
        promptMessage.show(getString(R.string.abort_survey), place.getName());
    }

    private void openPlaceListActivity() {
        PlaceListActivity.open(BuildingListActivity.this);
        finish();
    }
}