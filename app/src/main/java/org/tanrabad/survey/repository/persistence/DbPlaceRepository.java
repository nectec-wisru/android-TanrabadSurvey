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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.tanrabad.survey.BuildConfig;
import org.tanrabad.survey.domain.place.PlaceRepository;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.User;
import org.tanrabad.survey.utils.collection.CursorList;
import org.tanrabad.survey.utils.time.ThaiDateTimeConverter;

public class DbPlaceRepository extends DbRepository implements PlaceRepository, ChangedRepository<Place> {

    public static final String TABLE_NAME = "place";

    public DbPlaceRepository(Context context) {
        super(context);
    }

    @Override
    public List<Place> find() {
        SQLiteDatabase db = readableDatabase();
        Cursor placeCursor = db.query(TABLE_NAME, PlaceColumn.wildcard(),
            null, null, null, null, PlaceColumn.NAME + ", "
                + PlaceColumn.UPDATE_TIME);
        List<Place> places = placeListFrom(placeCursor);
        db.close();
        return places;
    }

    @Override
    public Place findByUuid(UUID placeUuid) {
        SQLiteDatabase db = readableDatabase();
        Cursor placeCursor = db.query(TABLE_NAME, PlaceColumn.wildcard(),
            PlaceColumn.ID + "=?", new String[]{placeUuid.toString()}, null, null, null);
        Place place = placeFrom(placeCursor);
        db.close();
        return place;
    }

    private Place placeFrom(Cursor cursor) {
        if (cursor.moveToFirst()) {
            Place place = new PlaceCursorMapper(cursor).map(cursor);
            cursor.close();
            return place;
        } else {
            cursor.close();
            return null;
        }
    }

    @Override
    public List<Place> findByPlaceType(int placeType) {
        SQLiteDatabase db = readableDatabase();
        String[] placeColumn = new String[]{PlaceColumn.ID, TABLE_NAME + "." + PlaceColumn.NAME, PlaceColumn.SUBTYPE_ID,
            PlaceColumn.SUBDISTRICT_CODE, PlaceColumn.LATITUDE, PlaceColumn.LONGITUDE,
            PlaceColumn.UPDATE_BY, PlaceColumn.UPDATE_TIME, PlaceColumn.CHANGED_STATUS};
        Cursor placeCursor = db.query(TABLE_NAME + " INNER JOIN place_subtype using(subtype_id)", placeColumn,
            PlaceColumn.TYPE_ID + "=?",
            new String[]{String.valueOf(placeType)},
            null, null,
            TABLE_NAME + "." + PlaceColumn.NAME);
        List<Place> places = placeListFrom(placeCursor);
        db.close();
        return places;
    }

    @Override
    public List<Place> findByName(String placeName) {
        SQLiteDatabase db = readableDatabase();
        Cursor placeCursor = db.query(TABLE_NAME, PlaceColumn.wildcard(),
            PlaceColumn.NAME + " LIKE ?", new String[]{"%" + placeName + "%"}, null, null, null);
        List<Place> places = placeListFrom(placeCursor);
        db.close();
        return places;
    }

    private List<Place> placeListFrom(Cursor placeCursor) {
        List<Place> placeList = new CursorList<>(placeCursor, new PlaceCursorMapper(placeCursor));
        return placeList.isEmpty() ? null : placeList;
    }

    @Override
    public boolean save(Place place) {
        ContentValues values = placeContentValues(place);
        values.put(PlaceColumn.CHANGED_STATUS, ChangedStatus.ADD);
        SQLiteDatabase db = writableDatabase();
        boolean success = saveByContentValues(db, values);
        db.close();
        return success;
    }

    @Override
    public boolean update(Place place) {
        ContentValues values = placeContentValues(place);
        values.put(PlaceColumn.CHANGED_STATUS, getAddOrChangedStatus(place));
        SQLiteDatabase db = writableDatabase();
        boolean success = updateByContentValues(db, values);
        db.close();
        return success;
    }

    @Override
    public boolean delete(Place place) {
        SQLiteDatabase db = writableDatabase();
        int deleted = db.delete(TABLE_NAME,
            PlaceColumn.ID + "=?",
            new String[]{place.getId().toString()});
        if (deleted > 1)
            throw new IllegalStateException("Delete Place more than 1 record");
        db.close();
        return deleted == 1;
    }

    @Override
    public void updateOrInsert(List<Place> updateList) {
        SQLiteDatabase db = writableDatabase();
        db.beginTransaction();
        for (Place place : updateList) {
            ContentValues values = placeContentValues(place);
            values.put(PlaceColumn.CHANGED_STATUS, ChangedStatus.UNCHANGED);
            boolean updated = updateByContentValues(db, values);
            if (!updated)
                saveByContentValues(db, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private int getAddOrChangedStatus(Place place) {
        SQLiteDatabase db = readableDatabase();
        Cursor placeCursor = db.query(TABLE_NAME, new String[]{PlaceColumn.CHANGED_STATUS},
            PlaceColumn.ID + "=?", new String[]{place.getId().toString()}, null, null, null);
        if (placeCursor.moveToNext()) {
            if (placeCursor.getInt(0) == ChangedStatus.ADD)
                return ChangedStatus.ADD;
            else
                return ChangedStatus.CHANGED;
        }
        placeCursor.close();
        db.close();
        return ChangedStatus.CHANGED;
    }

    private boolean updateByContentValues(SQLiteDatabase db, ContentValues place) {
        return db.update(TABLE_NAME, place, PlaceColumn.ID + "=?", new String[]{place.getAsString(PlaceColumn.ID)}) > 0;
    }

    private ContentValues placeContentValues(Place place) {
        ContentValues values = new ContentValues();
        values.put(PlaceColumn.ID, place.getId().toString());
        values.put(PlaceColumn.NAME, place.getName());
        values.put(PlaceColumn.SUBTYPE_ID, place.getSubType());
        values.put(PlaceColumn.SUBDISTRICT_CODE, place.getSubdistrictCode());
        if (place.getLocation() != null) {
            values.put(PlaceColumn.LATITUDE, place.getLocation().getLatitude());
            values.put(PlaceColumn.LONGITUDE, place.getLocation().getLongitude());
        }
        if (place.getUpdateBy() != null) {
            values.put(PlaceColumn.UPDATE_BY, place.getUpdateBy());
        }
        values.put(PlaceColumn.UPDATE_TIME, place.getUpdateTimestamp().toString());
        values.put(PlaceColumn.IS_TYPE_EDITED, place.isTypeEdited() ? 1 : 0);
        return values;
    }

    private boolean saveByContentValues(SQLiteDatabase db, ContentValues place) {
        return db.insert(TABLE_NAME, null, place) != ERROR_INSERT_ID;
    }

    @Override
    public List<Place> getAdd() {
        SQLiteDatabase db = readableDatabase();
        Cursor placeCursor = db.query(TABLE_NAME, PlaceColumn.wildcard(),
            PlaceColumn.CHANGED_STATUS + "=?", new String[]{String.valueOf(ChangedStatus.ADD)}, null, null, null);
        List<Place> places = placeListFrom(placeCursor);
        db.close();
        return places;
    }

    @Override
    public List<Place> getChanged() {
        SQLiteDatabase db = readableDatabase();
        Cursor placeCursor = db.query(TABLE_NAME, PlaceColumn.wildcard(),
            PlaceColumn.CHANGED_STATUS + "=?",
            new String[]{String.valueOf(ChangedStatus.CHANGED)}, null, null, null);
        List<Place> places = placeListFrom(placeCursor);
        db.close();
        return places;
    }

    @Override
    public boolean markUnchanged(Place data) {
        ContentValues values = new ContentValues();
        values.put(PlaceColumn.ID, data.getId().toString());
        values.put(PlaceColumn.CHANGED_STATUS, ChangedStatus.UNCHANGED);
        values.put(PlaceColumn.IS_TYPE_EDITED, 0);
        return updateByContentValues(values);
    }

    private boolean updateByContentValues(ContentValues place) {
        SQLiteDatabase db = writableDatabase();
        boolean success = updateByContentValues(db, place);
        db.close();
        return success;
    }

    @Override
    public List<Place> findRecent(User user) {
        String[] columns = new String[]{
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.ID,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.NAME,
            PlaceColumn.SUBDISTRICT_CODE,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.LATITUDE,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.LONGITUDE,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.SUBTYPE_ID,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.UPDATE_TIME,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.UPDATE_BY,
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.CHANGED_STATUS};
        SQLiteDatabase db = readableDatabase();
        Cursor cursor = db.query(
            "survey INNER JOIN building USING(building_id) INNER JOIN " + TABLE_NAME + " USING(place_id)",
            columns,
            SurveyColumn.SURVEYOR + "=?" + "AND " + surveyWithRangeCondition(),
            new String[]{user.getUsername()},
            DbPlaceRepository.TABLE_NAME + "." + PlaceColumn.ID,
            null,
            TABLE_NAME + "." + SurveyColumn.UPDATE_TIME + " DESC");
        List<Place> places = placeListFrom(cursor);
        db.close();
        return places;
    }

    private String surveyWithRangeCondition() {
        DateTime dateTime = ThaiDateTimeConverter.convert(new DateTime().toString());
        return "date(" + DbSurveyRepository.TABLE_NAME + "." + SurveyColumn.CREATE_TIME + ")"
            + " BETWEEN date('" + dateTime.minusDays(BuildConfig.SURVEY_RANGE_DAY) + "') "
            + "AND date('" + dateTime + "') ";
    }

}
