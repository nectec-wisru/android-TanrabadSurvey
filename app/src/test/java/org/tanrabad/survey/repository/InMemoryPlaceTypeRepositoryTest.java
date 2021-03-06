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

import org.junit.BeforeClass;
import org.junit.Test;
import org.tanrabad.survey.entity.lookup.PlaceType;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class InMemoryPlaceTypeRepositoryTest {

    private static InMemoryPlaceTypeRepository placeTypeRepository = InMemoryPlaceTypeRepository.getInstance();
    private static PlaceType village = new PlaceType(1, "หมู่บ้าน/ชุมชน");
    private static PlaceType worship = new PlaceType(2, "ศาสนสถาน");

    @BeforeClass
    public static void setUp() throws Exception {
        placeTypeRepository.save(village);
        placeTypeRepository.save(worship);
    }

    @Test
    public void testUpdate() throws Exception {
        PlaceType house = new PlaceType(1, "ที่พักอาศัย");

        placeTypeRepository.update(house);

        assertEquals(house, placeTypeRepository.findById(house.getId()));
    }

    @Test
    public void testFindAllPlaceType() throws Exception {
        List<PlaceType> placeTypes = placeTypeRepository.find();

        assertEquals(2, placeTypes.size());
    }

    @Test
    public void testEveryCallGetInstanceMustGotSameInstance() throws Exception {
        assertEquals(placeTypeRepository, InMemoryPlaceTypeRepository.getInstance());
    }

    @Test
    public void testFindById() throws Exception {
        assertEquals(village, placeTypeRepository.findById(1));
    }
}
