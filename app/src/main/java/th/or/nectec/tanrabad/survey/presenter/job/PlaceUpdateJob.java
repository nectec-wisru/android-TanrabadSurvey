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

package th.or.nectec.tanrabad.survey.presenter.job;

import th.or.nectec.tanrabad.domain.place.PlaceRepository;
import th.or.nectec.tanrabad.entity.Place;
import th.or.nectec.tanrabad.survey.service.PlaceRestService;
import th.or.nectec.tanrabad.survey.service.RestService;

import java.util.ArrayList;

public class PlaceUpdateJob implements Job {

    public static final int ID = 293789;

    private final PlaceRepository placeRepository;

    public PlaceUpdateJob(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void execute() throws JobException {
        RestService<Place> service = new PlaceRestService();
        ArrayList<Place> placeArrayList = new ArrayList<>();

        do {
            placeArrayList.addAll(service.getUpdate());
        } while (service.hasNextRequest());

        placeRepository.updateOrInsert(placeArrayList);
    }
}