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

package th.or.nectec.tanrabad.survey.validator;

import org.junit.Test;
import org.mockito.Mockito;
import th.or.nectec.tanrabad.domain.place.PlaceRepository;
import th.or.nectec.tanrabad.entity.Location;
import th.or.nectec.tanrabad.entity.Place;
import th.or.nectec.tanrabad.entity.utils.Address;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SavePlaceValidatorTest {

    @Test(expected = EmptyNameException.class)
    public void testEmptyName() throws Exception {
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        Place place = new Place(UUID.randomUUID(), null);

        savePlaceValidator.validate(place);
    }

    @Test(expected = NullAddressException.class)
    public void testAddressNull() throws Exception {
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        Place place = new Place(UUID.randomUUID(), "5555");

        savePlaceValidator.validate(place);
    }

    @Test(expected = NullLocationException.class)
    public void testLocationNull() throws Exception {
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        Place place = new Place(UUID.randomUUID(), "5555");
        place.setAddress(stubAddress());

        savePlaceValidator.validate(place);
    }

    private Address stubAddress() {
        Address address = new Address();
        address.setAddressCode("130202");
        address.setSubdistrict("คลองหลวง");
        address.setDistrict("คลองสอง");
        address.setProvince("ปทุมธานี");
        return address;
    }

    @Test(expected = ValidatorException.class)
    public void testSamePlaceNameAndPlaceTypeAndPlaceAddressException() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());

        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);
        savePlaceValidator.validate(stubPlace());
    }

    @Test
    public void testPlaceNameNotSame() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = new Place(UUID.randomUUID(), "ทดสอบ123");
        place.setAddress(stubAddress());
        place.setLocation(stubLocation());
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    private ArrayList<Place> stubPlacesList() {
        ArrayList<Place> places = new ArrayList<>();
        places.add(stubPlace());
        places.add(stubPlaceWorship());
        return places;
    }

    private Place stubPlace() {
        Place testPlace = Place.withName("ทดสอบ");
        testPlace.setAddress(stubAddress());
        testPlace.setLocation(stubLocation());
        testPlace.setType(Place.TYPE_HOSPITAL);
        return testPlace;
    }

    private Location stubLocation() {
        return new Location(1, 1);
    }

    private Place stubPlaceWorship() {
        Place testPlace = Place.withName("วัดสาม");
        testPlace.setAddress(stubAddress());
        testPlace.setLocation(stubLocation());
        testPlace.setType(Place.TYPE_WORSHIP);
        testPlace.setSubType(Place.SUBTYPE_TEMPLE);
        return testPlace;
    }

    @Test
    public void testSameNameAndTypeDifferentAddressCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = new Place(UUID.randomUUID(), "ทดสอบ123");
        place.setAddress(new Address());
        place.setLocation(stubLocation());
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testSameNameAndAddressDifferentTypeCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlace();
        place.setType(Place.TYPE_FACTORY);
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testDifferenceNameAndTypeAndSameAddressCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlace();
        place.setName("ทดสอบ789");
        place.setType(Place.TYPE_SCHOOL);
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testSameTypeAndAddressAndDifferenceNameCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlace();
        place.setName("ทดสอบ234");
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testSameNameDifferentAddressAndTypeCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlace();
        place.setAddress(new Address());
        place.setType(Place.TYPE_SCHOOL);
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testSameNameAndTypeAndDifferenceAddressCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlace();
        place.setAddress(new Address());
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testSameTypeAndDifferenceNameAndAddressCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlace();
        place.setName("ทดสอบ345");
        place.setAddress(new Address());
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }

    @Test
    public void testSameNameAndAddressAndDifferenceSubTypeOfWorshipCanSave() throws Exception {
        PlaceRepository placeRepository = Mockito.mock(PlaceRepository.class);
        Mockito.when(placeRepository.find()).thenReturn(stubPlacesList());
        Place place = stubPlaceWorship();
        place.setSubType(Place.SUBTYPE_CHURCH);
        SavePlaceValidator savePlaceValidator = new SavePlaceValidator();
        savePlaceValidator.setPlaceRepository(placeRepository);

        assertEquals(true, savePlaceValidator.validate(place));
    }
}