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

import org.tanrabad.survey.domain.organization.OrganizationRepository;
import org.tanrabad.survey.entity.Organization;
import org.tanrabad.survey.utils.collection.CursorMapper;

import java.util.List;

public class DbOrganizationRepository extends DbRepository implements OrganizationRepository {

    public static final String TABLE_NAME = "organization";

    public DbOrganizationRepository(Context context) {
        super(context);
    }

    @Override
    public Organization findById(int organizationId) {
        SQLiteDatabase db = readableDatabase();
        Cursor cursor = db.query(TABLE_NAME, OrganizationColumn.wildcard(),
                OrganizationColumn.ID + "=?", new String[]{String.valueOf(organizationId)}, null, null, null);
        Organization organization = getOrganization(cursor);
        db.close();
        return organization;
    }

    private Organization getOrganization(Cursor cursor) {
        if (cursor.moveToFirst()) {
            Organization organization = getMapper(cursor).map(cursor);
            cursor.close();
            return organization;
        } else {
            cursor.close();
            return null;
        }
    }

    private CursorMapper<Organization> getMapper(Cursor cursor) {
        return new OrganizationCursorMapper(cursor);
    }

    @Override
    public boolean save(Organization organization) {
        ContentValues values = orgContentValues(organization);
        SQLiteDatabase db = writableDatabase();
        boolean success = saveByContentValues(db, values);
        db.close();
        return success;
    }

    private ContentValues orgContentValues(Organization organization) {
        ContentValues values = new ContentValues();
        values.put(OrganizationColumn.ID, organization.getOrganizationId());
        values.put(OrganizationColumn.NAME, organization.getName());
        values.put(OrganizationColumn.ADDRESS, organization.getAddress());
        values.put(OrganizationColumn.SUBDISTRICT_CODE, organization.getSubdistrictCode());
        values.put(OrganizationColumn.HEALTH_REGION_CODE, organization.getHealthRegionCode());
        return values;
    }

    private boolean saveByContentValues(SQLiteDatabase db, ContentValues organization) {
        return db.insert(TABLE_NAME, null, organization) != ERROR_INSERT_ID;
    }

    @Override
    public boolean update(Organization organization) {
        ContentValues values = orgContentValues(organization);
        SQLiteDatabase db = writableDatabase();
        boolean success = updateByContentValues(db, values);
        db.close();
        return success;
    }

    @Override
    public boolean delete(Organization data) {
        return false;
    }

    @Override
    public void updateOrInsert(List<Organization> organizations) {
    }

    private boolean updateByContentValues(SQLiteDatabase db, ContentValues organization) {
        return db.update(
                TABLE_NAME, organization, OrganizationColumn.ID + "=?",
                new String[]{organization.getAsString(OrganizationColumn.ID)}) > 0;
    }
}
