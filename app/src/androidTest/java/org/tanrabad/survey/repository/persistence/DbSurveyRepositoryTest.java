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

package org.tanrabad.survey.repository.persistence;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tanrabad.survey.base.SurveyDbTestRule;
import org.tanrabad.survey.domain.building.BuildingRepository;
import org.tanrabad.survey.domain.building.BuildingWithSurveyStatus;
import org.tanrabad.survey.domain.place.PlaceRepository;
import org.tanrabad.survey.domain.survey.ContainerTypeRepository;
import org.tanrabad.survey.domain.user.UserRepository;
import org.tanrabad.survey.entity.*;
import org.tanrabad.survey.entity.field.Location;
import org.tanrabad.survey.entity.lookup.ContainerType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.tanrabad.survey.repository.persistence.SurveyColumn.*;

@RunWith(AndroidJUnit4.class)
public class DbSurveyRepositoryTest {
    @Rule
    public SurveyDbTestRule dbTestRule = new SurveyDbTestRule();
    private DbSurveyRepository surveyRepository;

    @Before
    public void setup() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByUsername("dpc-user")).thenReturn(stubUser());

        BuildingRepository buildingRepository = mock(BuildingRepository.class);
        when(buildingRepository.findByUuid(UUID.fromString("f5bfd399-8fb2-4a69-874a-b40495f7786f")))
                .thenReturn(stubBuilding());

        PlaceRepository placeRepository = mock(PlaceRepository.class);
        when(placeRepository.findByUuid(UUID.fromString("9b27df17-4234-4b9b-b444-0dc3d637d1fe")))
                .thenReturn(stubPlace());

        ContainerTypeRepository containerTypeRepository = mock(ContainerTypeRepository.class);
        when(containerTypeRepository.findById(1)).thenReturn(getWater());
        when(containerTypeRepository.findById(2)).thenReturn(getDrinkingWater());

        Context context = InstrumentationRegistry.getTargetContext();
        surveyRepository = new DbSurveyRepository(context, userRepository, placeRepository, buildingRepository,
                containerTypeRepository);
    }

    private User stubUser() {
        return new User("dpc-user");
    }

    private Building stubBuilding() {
        return new Building(UUID.fromString("f5bfd399-8fb2-4a69-874a-b40495f7786f"), "ABC");
    }

    private Place stubPlace() {
        return new Place(UUID.fromString("9b27df17-4234-4b9b-b444-0dc3d637d1fe"), "ABCD");
    }

    private ContainerType getWater() {
        return new ContainerType(1, "น้ำใช้");
    }

    private ContainerType getDrinkingWater() {
        return new ContainerType(2, "น้ำดื่ม");
    }

    @Test
    public void testSaveSuccess() throws Exception {
        Survey survey = getSurvey();
        boolean isSuccess = surveyRepository.save(survey);

        Cursor surveyQuery = dbTestRule.getReadable().query(DbSurveyRepository.TABLE_NAME,
                wildcard(),
                ID + "=?",
                new String[]{surveyId().toString()},
                null, null, null);
        assertEquals(true, surveyQuery.moveToFirst());
        assertEquals(true, isSuccess);
        assertEquals(surveyId().toString(), surveyQuery.getString(surveyQuery.getColumnIndex(ID)));
        assertEquals(80f, surveyQuery.getDouble(surveyQuery.getColumnIndex(LATITUDE)), 0);
        assertEquals(12f, surveyQuery.getDouble(surveyQuery.getColumnIndex(LONGITUDE)), 0);
        assertEquals("dpc-user", surveyQuery.getString(surveyQuery.getColumnIndex(SURVEYOR)));
        assertEquals(15, surveyQuery.getInt(surveyQuery.getColumnIndex(PERSON_COUNT)));
        assertEquals(survey.getStartTimestamp().toString(),
                surveyQuery.getString(surveyQuery.getColumnIndex(CREATE_TIME)));
        assertEquals(survey.getFinishTimestamp().toString(),
                surveyQuery.getString(surveyQuery.getColumnIndex(UPDATE_TIME)));
        assertEquals(ChangedStatus.ADD, surveyQuery.getInt(surveyQuery.getColumnIndex(CHANGED_STATUS)));
        surveyQuery.close();

        Cursor surveyDetails = dbTestRule.getReadable().query(DbSurveyRepository.DETAIL_TABLE_NAME,
                SurveyDetailColumn.wildcard(),
                SurveyDetailColumn.SURVEY_ID + "=?",
                new String[]{surveyId().toString()}, null, null, null);
        assertEquals(3, surveyDetails.getCount());
        assertEquals(true, surveyDetails.moveToFirst());
        assertEquals(1, surveyDetails.getInt(surveyDetails.getColumnIndex(SurveyDetailColumn.CONTAINER_TYPE_ID)));
        assertEquals(1, surveyDetails.getInt(surveyDetails.getColumnIndex(SurveyDetailColumn.CONTAINER_LOCATION_ID)));
        assertEquals(true, surveyDetails.moveToNext());
        assertEquals(1, surveyDetails.getInt(surveyDetails.getColumnIndex(SurveyDetailColumn.CONTAINER_TYPE_ID)));
        assertEquals(2, surveyDetails.getInt(surveyDetails.getColumnIndex(SurveyDetailColumn.CONTAINER_LOCATION_ID)));
        assertEquals(true, surveyDetails.moveToNext());
        assertEquals(2, surveyDetails.getInt(surveyDetails.getColumnIndex(SurveyDetailColumn.CONTAINER_TYPE_ID)));
        assertEquals(2, surveyDetails.getInt(surveyDetails.getColumnIndex(SurveyDetailColumn.CONTAINER_LOCATION_ID)));
        surveyDetails.close();
    }

    private Survey getSurvey() {
        Survey survey = new Survey(surveyId(), stubUser(), stubBuilding());
        survey.setLocation(new Location(80, 12));
        survey.setResidentCount(15);
        DateTime timeStamp = new DateTime();
        survey.setStartTimestamp(timeStamp);
        survey.setFinishTimestamp(timeStamp);

        List<SurveyDetail> indoorDetail = new ArrayList<>();
        indoorDetail.add(new SurveyDetail(UUID.randomUUID(), getWater(), 3, 2));
        survey.setIndoorDetail(indoorDetail);

        List<SurveyDetail> outdoorDetail = new ArrayList<>();
        outdoorDetail.add(new SurveyDetail(UUID.randomUUID(), getWater(), 6, 5));
        outdoorDetail.add(new SurveyDetail(UUID.randomUUID(), getDrinkingWater(), 4, 1));
        survey.setOutdoorDetail(outdoorDetail);

        return survey;
    }

    private UUID surveyId() {
        return UUID.nameUUIDFromBytes("abc".getBytes());
    }

    @Test
    public void testUpdate() throws Exception {
        Survey survey = getSurvey();
        surveyRepository.save(survey);
        survey.setResidentCount(6);
        List<SurveyDetail> indoorDetail = survey.getIndoorDetail();
        indoorDetail.get(0).setContainerCount(5, 4);
        indoorDetail.add(new SurveyDetail(UUID.randomUUID(), getDrinkingWater(), 7, 3));
        survey.setIndoorDetail(indoorDetail);
        boolean isSuccess = surveyRepository.update(survey);

        Cursor surveyQuery = dbTestRule.getReadable().query(DbSurveyRepository.TABLE_NAME,
                wildcard(),
                ID + "=?",
                new String[]{survey.getId().toString()}, null, null, null);

        assertEquals(true, isSuccess);
        assertEquals(true, surveyQuery.moveToFirst());
        assertEquals(6, surveyQuery.getInt(surveyQuery.getColumnIndex(PERSON_COUNT)));
        assertEquals(ChangedStatus.ADD, surveyQuery.getInt(surveyQuery.getColumnIndex(CHANGED_STATUS)));

        List<SurveyDetail> surveyDetails = surveyRepository.findSurveyDetail(survey.getId(),
                DbSurveyRepository.INDOOR_CONTAINER_LOCATION);
        assertEquals(2, surveyDetails.size());

        Cursor detailsCursor = dbTestRule.getReadable().query(DbSurveyRepository.DETAIL_TABLE_NAME,
                SurveyDetailColumn.wildcard(), SurveyDetailColumn.SURVEY_ID + "=?",
                new String[]{surveyId().toString()}, null, null, null);

        assertEquals(4, detailsCursor.getCount());
        assertEquals(true, detailsCursor.moveToFirst());
        assertEquals(1, detailsCursor.getInt(detailsCursor.getColumnIndex(SurveyDetailColumn.CONTAINER_TYPE_ID)));
        assertEquals(1, detailsCursor.getInt(detailsCursor.getColumnIndex(SurveyDetailColumn.CONTAINER_LOCATION_ID)));
        assertEquals(true, detailsCursor.moveToNext());
        assertEquals(1, detailsCursor.getInt(detailsCursor.getColumnIndex(SurveyDetailColumn.CONTAINER_TYPE_ID)));
        assertEquals(2, detailsCursor.getInt(detailsCursor.getColumnIndex(SurveyDetailColumn.CONTAINER_LOCATION_ID)));
        assertEquals(true, detailsCursor.moveToNext());
        assertEquals(2, detailsCursor.getInt(detailsCursor.getColumnIndex(SurveyDetailColumn.CONTAINER_TYPE_ID)));
        assertEquals(2, detailsCursor.getInt(detailsCursor.getColumnIndex(SurveyDetailColumn.CONTAINER_LOCATION_ID)));
        surveyQuery.close();
        detailsCursor.close();
    }

    @Test
    public void testUpdateException() throws Exception {
        Survey survey = getSurvey();
        surveyRepository.save(survey);
        List<SurveyDetail> indoorDetail = new ArrayList<>();
        indoorDetail.add(new SurveyDetail(UUID.randomUUID(), getWater(), 3, 2));
        survey.setIndoorDetail(indoorDetail);
        boolean isSuccess = surveyRepository.update(survey);

        List<SurveyDetail> surveyDetails = surveyRepository.findSurveyDetail(survey.getId(),
                DbSurveyRepository.INDOOR_CONTAINER_LOCATION);
        assertEquals(false, isSuccess);
        assertEquals(1, surveyDetails.size());
    }

    @Test
    public void testLoadSavedSurvey() throws Exception {
        Survey survey = getSurvey();
        surveyRepository.save(survey);
        Survey querySurvey = surveyRepository.findRecent(survey.getSurveyBuilding(), survey.getUser());
        assertEquals(survey.getId(), querySurvey.getId());
        assertEquals(survey.getIndoorDetail(), querySurvey.getIndoorDetail());
        assertEquals(survey.getOutdoorDetail(), querySurvey.getOutdoorDetail());
        assertEquals(survey.getSurveyBuilding(), querySurvey.getSurveyBuilding());
        assertEquals(survey.getUser(), querySurvey.getUser());
        assertEquals(survey.getLocation(), querySurvey.getLocation());
        assertEquals(survey.getStartTimestamp(), querySurvey.getStartTimestamp());
        assertEquals(survey.getResidentCount(), querySurvey.getResidentCount());
        assertEquals(survey.getUpdateTimestamp(), querySurvey.getUpdateTimestamp());
    }

    @Test
    public void testFindSurveyBuildingInThisWeek() throws Exception {
        List<BuildingWithSurveyStatus> surveyBuildingInThisWeek =
                surveyRepository.findSurveyBuilding(stubPlace(), stubUser());

        assertEquals(4, surveyBuildingInThisWeek.size());
        assertTrue(surveyBuildingInThisWeek.get(0).isSurvey);
        assertFalse(surveyBuildingInThisWeek.get(1).isSurvey);
        assertFalse(surveyBuildingInThisWeek.get(2).isSurvey);
        assertFalse(surveyBuildingInThisWeek.get(3).isSurvey);
        for (BuildingWithSurveyStatus building : surveyBuildingInThisWeek) {
            Log.e("survey status", String.valueOf(building.isSurvey));
            Log.e("building data", String.valueOf(building.building.getName()));
        }
    }
}
