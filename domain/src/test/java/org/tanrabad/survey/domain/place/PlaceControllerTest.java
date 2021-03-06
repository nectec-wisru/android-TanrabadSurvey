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

package org.tanrabad.survey.domain.place;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.tanrabad.survey.entity.Place;

import java.util.UUID;

public class PlaceControllerTest {
    private static final String PLACE_NAME = "New York";
    private final Place place = Place.withName(PLACE_NAME);
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private PlaceRepository placeRepository;
    private PlacePresenter placePresenter;
    private UUID placeUuid;

    @Before
    public void setUp() {
        placeUuid = UUID.nameUUIDFromBytes("3zyx".getBytes());
        placeRepository = context.mock(PlaceRepository.class);
        placePresenter = context.mock(PlacePresenter.class);
    }

    @Test
    public void testFoundPlace() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(placeRepository).findByUuid(placeUuid);
                will(returnValue(place));
                oneOf(placePresenter).displayPlace(place);
            }
        });
        PlaceController placeController = new PlaceController(placeRepository, placePresenter);
        placeController.showPlace(placeUuid);
    }

    @Test
    public void testNotFoundPlace() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(placeRepository).findByUuid(placeUuid);
                will(returnValue(null));
                oneOf(placePresenter).alertPlaceNotFound();
            }
        });
        PlaceController placeController = new PlaceController(placeRepository, placePresenter);
        placeController.showPlace(placeUuid);
    }

}
