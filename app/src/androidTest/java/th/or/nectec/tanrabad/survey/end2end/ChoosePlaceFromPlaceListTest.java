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

package th.or.nectec.tanrabad.survey.end2end;


import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Test;
import th.or.nectec.tanrabad.survey.R;
import th.or.nectec.tanrabad.survey.TanrabadEspressoTestBase;
import th.or.nectec.tanrabad.survey.presenter.PlaceListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ChoosePlaceFromPlaceListTest extends TanrabadEspressoTestBase {
    public ActivityTestRule<PlaceListActivity> mActivityTestRule =
            new ActivityTestRule<>(PlaceListActivity.class);
    PlaceListActivity mActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent();
        mActivity = mActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testChoosePlaceThenBuildingNotFound() {
        onView(withText("ชุมชนกอล์ฟวิว"))
                .perform(click());
        clickSurveyButton();
        textDisplayed("ชุมชนกอล์ฟวิว");
        onView(withId(R.id.card_title))
                .check(matches(withText(R.string.title_card_building_list)));
        textDisplayed(R.string.building_list_not_found);
        pressBack();
    }

    @Test
    public void testChoosePlaceThenFound3Building() {
        onView(withText("หมู่บ้านพาลาซเซตโต้"))
                .perform(click());
        clickSurveyButton();
        textDisplayed("หมู่บ้านพาลาซเซตโต้");
        onView(withId(R.id.card_title))
                .check(matches(withText(R.string.title_card_building_list)));
        onView(withId(R.id.building_count))
                .check(matches(withText("13 อาคาร")));
        textDisplayed("214/43");
        textDisplayed("214/44");
        textDisplayed("214/45");
        pressBack();
    }
}