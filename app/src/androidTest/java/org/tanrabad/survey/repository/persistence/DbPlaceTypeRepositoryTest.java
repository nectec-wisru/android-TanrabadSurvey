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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tanrabad.survey.base.SurveyDbTestRule;
import org.tanrabad.survey.entity.lookup.PlaceType;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DbPlaceTypeRepositoryTest {

    @Rule
    public SurveyDbTestRule dbTestRule = new SurveyDbTestRule();

    @Test
    public void testSave() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        DbPlaceTypeRepository dbContainerTypeRepository = new DbPlaceTypeRepository(context);
        boolean success = dbContainerTypeRepository.save(new PlaceType(0, "ไม่ระบุ"));

        SQLiteDatabase db = new SurveyLiteDatabase(context).getReadableDatabase();
        Cursor cursor = db.query(DbPlaceTypeRepository.TABLE_NAME,
                PlaceTypeColumn.wildcard(),
                PlaceTypeColumn.ID + "=?",
                new String[]{String.valueOf(0)},
                null, null, null);

        assertEquals(true, success);
        assertEquals(true, cursor.moveToFirst());
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(PlaceTypeColumn.ID)));
        assertEquals("ไม่ระบุ", cursor.getString(cursor.getColumnIndex(PlaceTypeColumn.NAME)));
        cursor.close();
    }

    @Test
    public void testUpdate() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        DbPlaceTypeRepository dbPlaceTypeRepository = new DbPlaceTypeRepository(context);
        boolean success = dbPlaceTypeRepository.update(new PlaceType(1, "ชุมชน"));

        SQLiteDatabase db = new SurveyLiteDatabase(context).getReadableDatabase();
        Cursor cursor = db.query(DbPlaceTypeRepository.TABLE_NAME,
                PlaceTypeColumn.wildcard(),
                PlaceTypeColumn.ID + "=?",
                new String[]{String.valueOf(1)},
                null, null, null);

        assertEquals(true, success);
        assertEquals(true, cursor.moveToFirst());
        assertEquals(1, cursor.getCount());
        assertEquals("ชุมชน", cursor.getString(cursor.getColumnIndex(PlaceTypeColumn.NAME)));
        cursor.close();
    }

    @Test
    public void testFindAllPlaceType() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        DbPlaceTypeRepository dbPlaceTypeRepository = new DbPlaceTypeRepository(context);
        List<PlaceType> placeTypeList = dbPlaceTypeRepository.find();

        assertEquals(6, placeTypeList.size());
        PlaceType placeType0 = placeTypeList.get(0);
        assertEquals(1, placeType0.getId());
        assertEquals("หมู่บ้าน/ชุมชน", placeType0.getName());
        PlaceType placeType6 = placeTypeList.get(5);
        assertEquals(6, placeType6.getId());
        assertEquals("โรงแรม/รีสอร์ท", placeType6.getName());
    }

    @Test
    public void testFindById() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        DbPlaceTypeRepository dbPlaceTypeRepository = new DbPlaceTypeRepository(context);
        PlaceType placeType = dbPlaceTypeRepository.findById(1);
        assertEquals(1, placeType.getId());
        assertEquals("หมู่บ้าน/ชุมชน", placeType.getName());
    }
}
