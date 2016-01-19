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

package th.or.nectec.tanrabad.domain.place;

import th.or.nectec.tanrabad.entity.Place;

import java.util.List;

public class PlaceChooser {

    private PlaceRepository placeRepository;
    private PlaceListPresenter placeListPresenter;

    public PlaceChooser(PlaceRepository placeRepository, PlaceListPresenter placeListPresenter) {
        this.placeRepository = placeRepository;
        this.placeListPresenter = placeListPresenter;
    }

    public void getPlaceList() {
        List<Place> places = this.placeRepository.find();
        if (places == null) {
            placeListPresenter.displayPlaceNotFound();
        } else {
            placeListPresenter.displayPlaceList(places);
        }
    }

    public void getPlaceListWithPlaceTypeFilter(int placeType) {
        List<Place> places = this.placeRepository.findByPlaceType(placeType);
        if (places == null) {
            placeListPresenter.displayPlaceNotFound();
        } else {
            placeListPresenter.displayPlaceList(places);
        }
    }

    public void searchByName(String placeName) {
        List<Place> places = this.placeRepository.findByName(placeName);
        if (places == null) {
            placeListPresenter.displayPlaceNotFound();
        } else {
            placeListPresenter.displayPlaceList(places);
        }
    }
}
