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

package org.tanrabad.survey.entity.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.field.Location;
import org.tanrabad.survey.entity.lookup.PlaceType;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PlaceTest {

    private static final UUID BANGPHAI_UUID = UUID.randomUUID();
    private static final String BANGPHAI_NAME = "บางไผ่";
    private final Place place1 = new Place(BANGPHAI_UUID, BANGPHAI_NAME);
    private final Place place2 = new Place(BANGPHAI_UUID, BANGPHAI_NAME);
    private final Location location = new Location(14.078606, 100.603120);

    @Test
    public void testWithName() {
        Place place = Place.withName(BANGPHAI_NAME);
        assertEquals(BANGPHAI_NAME, place.getName());
    }

    @Test
    public void testGetId() {
        assertEquals(BANGPHAI_UUID, place1.getId());
    }

    @Test
    public void testSetThenGetPlaceName() {
        place1.setName("บางโพธิ์");
        assertEquals("บางโพธิ์", place1.getName());
    }

    @Test
    public void testSetThenGetPlaceType() {
        place1.setType(PlaceType.SCHOOL);
        assertEquals(PlaceType.SCHOOL, place1.getType());
    }

    @Test
    public void testSetThenGetPlaceLocation() {
        place1.setLocation(location);
        assertEquals(location, place1.getLocation());
    }

    @Test
    public void placeWithDifferentNameMustNotEqual() {
        place1.setType(PlaceType.VILLAGE_COMMUNITY);
        place2.setType(place1.getType());
        place2.setName("บางโพธิ์");
        assertNotEquals(place1, place2);
    }

    @Test
    public void placeWithDifferentTypeMustNotEqual() {
        place1.setType(PlaceType.FACTORY);
        place2.setType(PlaceType.SCHOOL);
        assertNotEquals(place1, place2);
    }

    @Test
    public void placeWithTheSameNameAndTypeMustEqual() {
        place1.setType(PlaceType.VILLAGE_COMMUNITY);
        place2.setType(PlaceType.VILLAGE_COMMUNITY);
        assertEquals(place1, place2);
    }

    @Test
    public void typeNotEditedAtFirstTime() throws Exception {
        place1.setType(PlaceType.VILLAGE_COMMUNITY);

        assertFalse(place1.isTypeEdited());
    }

    @Test
    public void typeNotEditedOnSetOldValue() throws Exception {
        place1.setType(PlaceType.VILLAGE_COMMUNITY);
        place1.setType(PlaceType.VILLAGE_COMMUNITY);

        assertFalse(place1.isTypeEdited());
    }

    @Test
    public void typeEdited() throws Exception {
        place1.setType(PlaceType.VILLAGE_COMMUNITY);
        place1.setType(PlaceType.WORSHIP);

        assertTrue(place1.isTypeEdited());
    }
}
