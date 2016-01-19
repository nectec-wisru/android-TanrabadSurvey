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

package th.or.nectec.tanrabad.survey.repository.persistence;

import android.database.Cursor;
import th.or.nectec.tanrabad.domain.UserRepository;
import th.or.nectec.tanrabad.domain.address.AddressRepository;
import th.or.nectec.tanrabad.entity.Location;
import th.or.nectec.tanrabad.entity.Place;
import th.or.nectec.tanrabad.survey.utils.collection.CursorMapper;

import java.util.UUID;

class PlaceCursorMapper implements CursorMapper<Place> {

    private final UserRepository userRepository;
    private AddressRepository addressRepository;
    private int idIndex;
    private int nameIndex;
    private int subtypeIndex;
    private int subdistrictCodeIndex;
    private int latIndex;
    private int lngIndex;
    private int updateByIndex;
    private int updateTimeIndex;

    public PlaceCursorMapper(Cursor cursor, UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        findColumnIndexOf(cursor);
    }

    private void findColumnIndexOf(Cursor cursor) {
        idIndex = cursor.getColumnIndex(PlaceColumn.ID);
        nameIndex = cursor.getColumnIndex(PlaceColumn.NAME);
        subtypeIndex = cursor.getColumnIndex(PlaceColumn.SUBTYPE_ID);
        subdistrictCodeIndex = cursor.getColumnIndex(PlaceColumn.SUBDISTRICT_CODE);
        latIndex = cursor.getColumnIndex(PlaceColumn.LATITUDE);
        lngIndex = cursor.getColumnIndex(PlaceColumn.LONGITUDE);
        updateByIndex = cursor.getColumnIndex(PlaceColumn.UPDATE_BY);
        updateTimeIndex = cursor.getColumnIndex(PlaceColumn.UPDATE_TIME);
    }

    @Override
    public Place map(Cursor cursor) {
        UUID uuid = UUID.fromString(cursor.getString(idIndex));
        Place place = new Place(uuid, cursor.getString(nameIndex));
        place.setAddress(addressRepository.findBySubdistrictCode(cursor.getString(subdistrictCodeIndex)));
        int subtypeID = cursor.getInt(subtypeIndex);
        place.setType(PlaceTypeMapper.getInstance().findBySubType(subtypeID));
        place.setSubType(subtypeID);
        place.setLocation(new Location(cursor.getDouble(latIndex), cursor.getDouble(lngIndex)));
        place.setUpdateBy(userRepository.findUserByName(cursor.getString(updateByIndex)));
        place.setUpdateTimestamp(cursor.getString(updateTimeIndex));
        return place;
    }
}