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

package th.or.nectec.tanrabad.survey.repository;

import th.or.nectec.tanrabad.domain.place.PlaceRepository;
import th.or.nectec.tanrabad.entity.Place;
import th.or.nectec.tanrabad.survey.TanrabadApp;
import th.or.nectec.tanrabad.survey.repository.persistence.DbPlaceRepository;

import java.util.List;
import java.util.UUID;

public class PlaceRepoBroker implements PlaceRepository {

    private static PlaceRepoBroker instance;
    private PlaceRepository cache;
    private PlaceRepository persistence;


    protected PlaceRepoBroker(PlaceRepository cache, PlaceRepository persistence) {
        this.cache = cache;
        this.persistence = persistence;
    }

    public static PlaceRepoBroker getInstance() {
        if (instance == null)
            instance = new PlaceRepoBroker(InMemoryPlaceRepository.getInstance(),
                    new DbPlaceRepository(TanrabadApp.getInstance()));
        return instance;
    }

    @Override
    public List<Place> find() {
        return persistence.find();
    }

    @Override
    public Place findByUUID(UUID placeUUID) {
        Place place = cache.findByUUID(placeUUID);
        if (place == null) {
            place = persistence.findByUUID(placeUUID);
            cache.save(place);
        }
        return place;
    }

    @Override
    public List<Place> findByPlaceType(int placeType) {
        return persistence.findByPlaceType(placeType);
    }

    @Override
    public List<Place> findByName(String placeName) {
        return persistence.findByName(placeName);
    }

    @Override
    public boolean save(Place place) {
        boolean success = persistence.save(place);
        if (success) {
            cache.save(place);
        }
        return success;
    }

    @Override
    public boolean update(Place place) {
        boolean success = persistence.update(place);
        if (success) {
            cache.update(place);
        }
        return success;
    }

    @Override
    public void updateOrInsert(List<Place> update) {
        persistence.updateOrInsert(update);
        cache.updateOrInsert(update);
    }
}