package th.or.nectec.tanrabad.survey.repository.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import th.or.nectec.tanrabad.domain.place.PlaceTypeRepository;
import th.or.nectec.tanrabad.entity.lookup.PlaceType;
import th.or.nectec.tanrabad.survey.utils.collection.CursorList;
import th.or.nectec.tanrabad.survey.utils.collection.CursorMapper;

import java.util.List;

public class DbPlaceTypeRepository extends DbRepository implements PlaceTypeRepository {

    public static final String TABLE_NAME = "place_type";
    public static final int ERROR_INSERT_ID = -1;

    public DbPlaceTypeRepository(Context context) {
        super(context);
    }

    @Override
    public List<PlaceType> find() {
        SQLiteDatabase db = readableDatabase();
        Cursor placeTypeCursor = db.query(TABLE_NAME, PlaceTypeColumn.wildcard(),
                null, null, null, null, null);
        return new CursorList<>(placeTypeCursor, getMapper(placeTypeCursor));
    }

    @Override
    public PlaceType findByID(int placeTypeId) {
        SQLiteDatabase db = readableDatabase();
        Cursor placeTypeCursor = db.query(TABLE_NAME, PlaceTypeColumn.wildcard(),
                PlaceTypeColumn.ID + "=?", new String[]{String.valueOf(placeTypeId)}, null, null, null);
        return getPlaceType(placeTypeCursor);
    }

    private PlaceType getPlaceType(Cursor cursor) {
        if (cursor.moveToFirst()) {
            PlaceType placeType = getMapper(cursor).map(cursor);
            cursor.close();
            return placeType;
        } else {
            cursor.close();
            return null;
        }
    }

    private CursorMapper<PlaceType> getMapper(Cursor cursor) {
        return new PlaceTypeCursorMapper(cursor);
    }

    @Override
    public boolean save(PlaceType placeType) {
        return saveByContentValues(writableDatabase(),
                placeTypeContentValues(placeType));
    }

    @Override
    public boolean update(PlaceType placeType) {
        return updateByContentValues(writableDatabase(),
                placeTypeContentValues(placeType));
    }

    @Override
    public void updateOrInsert(List<PlaceType> updateList) {
        SQLiteDatabase db = writableDatabase();
        db.beginTransaction();
        for (PlaceType eachPlaceType : updateList) {
            ContentValues values = placeTypeContentValues(eachPlaceType);
            boolean updated = updateByContentValues(db, values);
            if (!updated)
                saveByContentValues(db, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private boolean updateByContentValues(SQLiteDatabase db, ContentValues placeType) {
        return db.update(TABLE_NAME, placeType, PlaceTypeColumn.ID + "=?",
                new String[]{placeType.getAsString(PlaceTypeColumn.ID)}) > 0;
    }

    private boolean saveByContentValues(SQLiteDatabase db, ContentValues placeType) {
        return db.insert(TABLE_NAME, null, placeType) != ERROR_INSERT_ID;
    }

    private ContentValues placeTypeContentValues(PlaceType placeType) {
        ContentValues values = new ContentValues();
        values.put(PlaceTypeColumn.ID, placeType.getId());
        values.put(PlaceTypeColumn.NAME, placeType.getName());
        return values;
    }
}
