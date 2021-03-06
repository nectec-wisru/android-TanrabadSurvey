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

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;

import org.tanrabad.survey.R;
import org.tanrabad.survey.domain.place.PlaceWithSurveyHistoryChooser;
import org.tanrabad.survey.domain.place.PlaceWithSurveyHistoryListPresenter;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.User;
import org.tanrabad.survey.job.AbsJobRunner;
import org.tanrabad.survey.job.DownloadJobBuilder;
import org.tanrabad.survey.job.UploadJobRunner;
import org.tanrabad.survey.presenter.view.MainActivityNavigation;
import org.tanrabad.survey.repository.BrokerPlaceRepository;
import org.tanrabad.survey.repository.BrokerUserRepository;
import org.tanrabad.survey.service.ApiSyncInfoPreference;
import org.tanrabad.survey.service.BuildingRestService;
import org.tanrabad.survey.service.PlaceRestService;
import org.tanrabad.survey.utils.alert.Alert;
import org.tanrabad.survey.utils.android.NetworkChangeReceiver;
import org.tanrabad.survey.utils.android.TwiceBackPressed;
import org.tanrabad.survey.utils.prompt.AlertDialogPromptMessage;
import org.tanrabad.survey.utils.prompt.PromptMessage;

import java.util.List;

public class MainActivity extends TanrabadActivity implements View.OnClickListener, View.OnLongClickListener,
        PlaceWithSurveyHistoryListPresenter, AdapterView.OnItemClickListener {

    private PlaceSurveyAdapter recentSurveyPlaceAdapter;
    private CardView recentSurveyPlaceCardView;
    private NetworkChangeReceiver networkChangeReceiver;
    private ObjectAnimator syncProgressAnimator;
    private PlaceWithSurveyHistoryChooser recentSurveyPlaceChooser;
    private TwiceBackPressed twiceBackPressed;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twiceBackPressed = new TwiceBackPressed(this);

        setContentView(R.layout.activity_main);
        MainActivityNavigation.setup(this);
        setupTrialNotice();
        setupViewOnClick();
        setupNetworkChangeReceiver();
        setupList();
        setupSyncAnimator();

        showRecentSurveyCard();
        doLoadingRecentSurveyData();
        if (!isUiTesting()) {
            startAnimation();
        }
    }

    private void setupTrialNotice() {
        boolean trialUser = AccountUtils.isTrialUser(AccountUtils.getUser());
        findViewById(R.id.notice_trial).setVisibility(trialUser ? View.VISIBLE : View.GONE);
    }

    private void setupViewOnClick() {
        findViewById(R.id.start_survey).setOnClickListener(this);
        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.magnifier).setOnClickListener(this);
        findViewById(R.id.sync_data).setOnClickListener(this);
        findViewById(R.id.sync_data).setOnLongClickListener(this);
    }

    private void setupNetworkChangeReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver(new NetworkChangeReceiver.OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean isConnected) {
                if (isConnected) {
                    findViewById(R.id.sync_data).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.sync_data).setVisibility(View.GONE);
                }
            }
        });
        registerReceiver(networkChangeReceiver, NetworkChangeReceiver.getIntentFilter());
    }

    private void setupList() {
        RecyclerView recentSurveyList = (RecyclerView) findViewById(R.id.place_history_list);
        recentSurveyPlaceAdapter = new PlaceSurveyAdapter(this, getSupportFragmentManager());
        recentSurveyList.setAdapter(recentSurveyPlaceAdapter);
        recentSurveyList.setLayoutManager(new LinearLayoutManager(this));
        recentSurveyList.addItemDecoration(new SimpleDividerItemDecoration(this));
        recentSurveyPlaceAdapter.setOnItemClickListener(this);
        RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader) findViewById(R.id.card_header);
        recyclerViewHeader.attachTo(recentSurveyList, true);
    }

    private void setupSyncAnimator() {
        syncProgressAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(
                this, R.animator.water_spin);
        syncProgressAnimator.setTarget(findViewById(R.id.water_shadow));
    }

    private void showRecentSurveyCard() {
        recentSurveyPlaceCardView = (CardView) findViewById(R.id.card_layout);
        recentSurveyPlaceChooser = new PlaceWithSurveyHistoryChooser(
                BrokerUserRepository.getInstance(),
            BrokerPlaceRepository.getInstance(),
                this);
    }

    private void doLoadingRecentSurveyData() {
        User user = AccountUtils.getUser();
        if (user != null)
            recentSurveyPlaceChooser.showSurveyPlaceList(user.getUsername());
        else finish();
    }

    private void startAnimation() {
        startAnimation(R.id.magnifier, R.anim.magnifier);
        startAnimation(R.id.larvae, R.anim.larvae);
        startAnimation(R.id.water_shadow, R.anim.water_shadow);
        startAnimation(R.id.larvae_deep, R.anim.larvae_deep);
        startAnimation(R.id.start_survey_hint, R.anim.scale_in);
    }

    private void startAnimation(@IdRes int viewId, @AnimRes int animId) {
        Animation anim = AnimationUtils.loadAnimation(this, animId);
        findViewById(viewId).startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_survey:
                if (AccountUtils.isTrialUser(AccountUtils.getUser())) {
                    PromptMessage promptMessage = new AlertDialogPromptMessage(this);
                    promptMessage.setOnConfirm(getString(R.string.got_it), new PromptMessage.OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            PlaceListActivity.open(MainActivity.this);
                        }
                    });
                    promptMessage.show(getString(R.string.trial_warning_title), getString(R.string.trial_warning_msg));
                } else {
                    PlaceListActivity.open(this);
                }

                break;
            case R.id.magnifier:
            case R.id.root:
                startAnimation(R.id.larvae, R.anim.dook_digg);
                break;
            case R.id.sync_data:
                startOrResumeSyncAnimation();
                findViewById(R.id.sync_data).setEnabled(false);
                startSyncJobs();
                break;
        }
    }

    private void startOrResumeSyncAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && syncProgressAnimator.isPaused()) {
            syncProgressAnimator.resume();
        } else {
            syncProgressAnimator.start();
        }
    }

    private void startSyncJobs() {
        AbsJobRunner jobRunner = new ManualSyncJobRunner();
        jobRunner.addJobs(new UploadJobRunner.Builder().getJobs());
        jobRunner.addJobs(new DownloadJobBuilder().getJobs());
        jobRunner.start();
    }

    @Override
    public void onBackPressed() {
        if (twiceBackPressed.onTwiceBackPressed()) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doLoadingRecentSurveyData();
    }

    @Override
    public void displaySurveyPlaceList(List<Place> surveyPlace) {
        recentSurveyPlaceAdapter.updateData(surveyPlace);
        recentSurveyPlaceCardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void alertUserNotFound() {
        Alert.highLevel().show(R.string.user_not_found);
    }

    @Override
    public void displaySurveyPlacesNotFound() {
        recentSurveyPlaceAdapter.clearData();
        recentSurveyPlaceCardView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Place place = recentSurveyPlaceAdapter.getItem(position);
        SurveyBuildingHistoryActivity.open(this, place);
    }

    private void stopSyncAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            syncProgressAnimator.pause();
        } else {
            syncProgressAnimator.cancel();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.sync_data:
                startOrResumeSyncAnimation();
                new ApiSyncInfoPreference(this, PlaceRestService.PATH).backLastUpdateTimeToYesterday();
                new ApiSyncInfoPreference(this, BuildingRestService.PATH).backLastUpdateTimeToYesterday();
                findViewById(R.id.sync_data).setEnabled(false);
                startSyncJobs();
                break;
        }
        return true;
    }

    protected class ManualSyncJobRunner extends UploadJobRunner {
        @Override
        protected void onRunFinish() {
            super.onRunFinish();
            findViewById(R.id.sync_data).setEnabled(true);
            stopSyncAnimation();
            doLoadingRecentSurveyData();
        }
    }
}
