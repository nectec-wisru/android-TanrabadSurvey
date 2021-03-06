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

package org.tanrabad.survey.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.tanrabad.survey.domain.building.BuildingWithSurveyStatus;
import org.tanrabad.survey.domain.place.PlaceRepositoryException;
import org.tanrabad.survey.domain.survey.SurveyRepository;
import org.tanrabad.survey.entity.Building;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.Survey;
import org.tanrabad.survey.entity.SurveyDetail;
import org.tanrabad.survey.entity.User;

final class InMemorySurveyRepository implements SurveyRepository {

    private static InMemorySurveyRepository instance;
    private List<Survey> surveys;
    private Map<Building, Survey> surveyMaps = new ConcurrentHashMap<>();

    private InMemorySurveyRepository() {
        this.surveys = new ArrayList<>();
    }

    public static InMemorySurveyRepository getInstance() {
        if (instance == null)
            instance = new InMemorySurveyRepository();
        return instance;
    }

    @Override
    public Survey findRecent(Building building, User user) {
        for (Survey eachSurvey : surveys) {
            if (eachSurvey.getSurveyBuilding().equals(building) && eachSurvey.getUser().equals(user)) {
                return eachSurvey;
            }
        }
        return null;
    }

    @Override
    public List<Survey> findRecent(Place place, User user) {
        ArrayList<Survey> surveyBuilding = new ArrayList<>();
        for (Survey eachSurvey : surveys) {
            if (eachSurvey.getSurveyBuilding().getPlace().equals(place) && eachSurvey.getUser().equals(user)) {
                surveyBuilding.add(eachSurvey);
            }
        }
        return surveyBuilding.isEmpty() ? null : surveyBuilding;
    }

    @Override
    public List<BuildingWithSurveyStatus> findSurveyBuilding(Place place, User user) {
        List<BuildingWithSurveyStatus> buildings = new ArrayList<>();
        for (Building building : surveyMaps.keySet()) {
            if (building.getPlaceId().equals(place.getId())) {
                buildings.add(new BuildingWithSurveyStatus(building, true));
            }
        }
        return buildings.isEmpty() ? null : buildings;
    }

    @Override
    public List<BuildingWithSurveyStatus> findSurveyBuildingByBuildingName(
            Place place, User user, String buildingName) {
        List<BuildingWithSurveyStatus> buildings = new ArrayList<>();
        for (Building building : surveyMaps.keySet()) {
            if (building.getPlaceId().equals(place.getId()) && building.getName().contains(buildingName)) {
                buildings.add(new BuildingWithSurveyStatus(building, true));
            }
        }
        return buildings.isEmpty() ? null : buildings;
    }

    @Override
    public List<SurveyDetail> findSurveyDetail(UUID surveyId, int containerLocationId) {
        return null;
    }

    @Override
    public boolean save(Survey survey) {
        surveys.add(survey);
        surveyMaps.put(survey.getSurveyBuilding(), survey);
        return true;
    }


    @Override
    public boolean update(Survey survey) {
        if (surveys.contains(survey)) {
            surveys.set(surveys.indexOf(survey), survey);
        }
        surveyMaps.put(survey.getSurveyBuilding(), survey);
        return true;
    }

    @Override
    public boolean delete(Survey data) {
        if (surveyMaps.containsValue(data)) {
            surveyMaps.remove(data);
            surveys.remove(data);
            return true;
        }
        return false;
    }


    @Override
    public void updateOrInsert(List<Survey> surveys) {
        for (Survey survey : surveys) {
            try {
                update(survey);
            } catch (PlaceRepositoryException pre) {
                save(survey);
            }
        }
    }
}
