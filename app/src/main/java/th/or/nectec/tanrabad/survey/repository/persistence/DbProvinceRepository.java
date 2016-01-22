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


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import th.or.nectec.tanrabad.domain.address.ProvinceRepository;
import th.or.nectec.tanrabad.entity.Province;

import java.util.List;

public class DbProvinceRepository implements ProvinceRepository {

    private Context context;

    public DbProvinceRepository(Context context) {
        this.context = context;
    }

    @Override
    public List<Province> find() {
        return null;
    }

    @Override
    public Province findByCode(String provinceCode) {
        return null;
    }

    @Override
    public boolean save(Province province) {
        return false;
    }

    @Override
    public boolean update(Province province) {
        return false;
    }

    @Override
    public void updateOrInsert(List<Province> provinces) {
        SQLiteDatabase db = new SurveyLiteDatabase(context).getWritableDatabase();
        db.beginTransaction();
        for (Province province : provinces) {
            ContentValues cv = provinceContentValues(province);
            boolean update = updateWithContentValues(db, cv);
            if (!update)
                insertWithContentValue(db, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private boolean insertWithContentValue(SQLiteDatabase db, ContentValues cv) {
        return db.insert("province", null, cv) != -1;
    }

    private boolean updateWithContentValues(SQLiteDatabase db, ContentValues cv) {
        return db.update("province", cv, "province_code=?", new String[]{cv.getAsString("province_code")}) > 0;
    }

    private ContentValues provinceContentValues(Province province) {
        ContentValues cv = new ContentValues();
        cv.put("province_code", province.getCode());
        cv.put("name", province.getName());
        return cv;
    }
}