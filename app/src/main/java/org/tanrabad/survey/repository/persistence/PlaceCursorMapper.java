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

package org.tanrabad.survey.repository.persistence;

import android.database.Cursor;
import android.support.annotation.NonNull;
import java.util.UUID;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.field.Location;
import org.tanrabad.survey.entity.lookup.PlaceSubType;
import org.tanrabad.survey.repository.BrokerPlaceSubTypeRepository;
import org.tanrabad.survey.utils.collection.CursorMapper;

class PlaceCursorMapper implements CursorMapper<Place> {

    private int idIndex;
    private int nameIndex;
    private int subtypeIndex;
    private int subdistrictCodeIndex;
    private int latIndex;
    private int lngIndex;
    private int updateByIndex;
    private int updateTimeIndex;
    private int changedStatusIndex;
    private int isTypeEditedIndex;

    public PlaceCursorMapper(Cursor cursor) {
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
        changedStatusIndex = cursor.getColumnIndex(PlaceColumn.CHANGED_STATUS);
        isTypeEditedIndex = cursor.getColumnIndex(PlaceColumn.IS_TYPE_EDITED);
    }

    @Override
    public PlaceWithChange map(Cursor cursor) {
        UUID uuid = UUID.fromString(cursor.getString(idIndex));
        PlaceWithChange place = new PlaceWithChange(
                uuid, cursor.getString(nameIndex), cursor.getInt(changedStatusIndex));
        place.setSubdistrictCode(cursor.getString(subdistrictCodeIndex));
        PlaceSubType subType = getSubType(cursor);
        place.setType(subType.getPlaceTypeId());
        place.setSubType(subType.getId());
        place.setLocation(getLocation(cursor));
        place.setUpdateBy(cursor.getString(updateByIndex));
        place.setUpdateTimestamp(cursor.getString(updateTimeIndex));
        if (isTypeEditedIndex >= 0)
            place.setTypeEdited(cursor.getInt(isTypeEditedIndex) == 1);
        return place;
    }

    @NonNull
    private PlaceSubType getSubType(Cursor cursor) {
        int subTypeId = cursor.getInt(subtypeIndex);
        PlaceSubType subType = BrokerPlaceSubTypeRepository
            .getInstance()
            .findById(subTypeId);
        if (subType == null)
            throw new IllegalArgumentException("Not found place subtype " + subTypeId);
        return subType;
    }

    private Location getLocation(Cursor cursor) {
        double lat = cursor.getDouble(latIndex);
        double lng = cursor.getDouble(lngIndex);
        return (lat != 0f && lng != 0f) ? new Location(lat, lng) : null;
    }

}
