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
import org.tanrabad.survey.domain.place.PlaceSubTypeRepositoryException;
import org.tanrabad.survey.entity.lookup.PlaceSubType;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class InMemoryPlaceSubTypeRepositoryTest {

    private static InMemoryPlaceSubTypeRepository placeSubTypeRepository = InMemoryPlaceSubTypeRepository.getInstance();
    private static PlaceSubType pst1 = new PlaceSubType(1, "สำนักงานสาธารณสุขจังหวัด", 4);
    private static PlaceSubType pst2 = new PlaceSubType(2, "สำนักงานสาธารณสุขอำเภอ", 4);

    @BeforeClass
    public static void setUp() throws Exception {
        placeSubTypeRepository.save(pst1);
        placeSubTypeRepository.save(pst2);
    }

    @Test(expected = PlaceSubTypeRepositoryException.class)
    public void testSaveExistPlaceMustThrowException() throws Exception {
        placeSubTypeRepository.save(pst1);
    }

    @Test(expected = PlaceSubTypeRepositoryException.class)
    public void testUpdateNotExistPlaceMustThrowException() throws Exception {
        placeSubTypeRepository.update(new PlaceSubType(3, "โบสถ์", 3));
    }

    @Test
    public void testFindAllPlaceType() throws Exception {
        List<PlaceSubType> placeSubTypes = placeSubTypeRepository.find();
        assertEquals(2, placeSubTypes.size());
        assertEquals(pst1, placeSubTypes.get(0));
        assertEquals(pst2, placeSubTypes.get(1));
    }

    @Test
    public void testEveryCallGetInstanceMustGotSameInstance() throws Exception {
        assertEquals(placeSubTypeRepository, InMemoryPlaceSubTypeRepository.getInstance());
    }

    @Test
    public void testFindById() throws Exception {
        assertEquals(pst1, placeSubTypeRepository.findById(1));
    }

    @Test
    public void testFindByPlaceTypeId() throws Exception {
        List<PlaceSubType> placeSubTypes = placeSubTypeRepository.findByPlaceTypeId(4);
        assertEquals(2, placeSubTypes.size());
        assertEquals(pst1, placeSubTypes.get(0));
        assertEquals(pst2, placeSubTypes.get(1));
    }
}
