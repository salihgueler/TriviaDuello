package com.iamsalih.triviaduello.data.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.TABLE_NAME;

/**
 * Created by muhammedsalihguler on 10.12.17.
 */

public class QuestionProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.iamsalih.triviaduello.data.database.QuestionProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/questions";
    static final Uri CONTENT_URI = Uri.parse(URL);

    private SQLiteDatabase database;

    static final int questions = 1;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "questions", questions);
    }
    @Override
    public boolean onCreate() {
        QuestionDbHelper helper = new QuestionDbHelper(getContext());
        database = helper.getWritableDatabase();
        return (database == null)? false:true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(TABLE_NAME);

            Cursor cursor = qb.query(database,
                                projection,
                                selection,
                                selectionArgs,null,null,
                                sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.dir/vnd.triviaduello.questions";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rowID = database.insert(TABLE_NAME, "", contentValues);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = database.delete(TABLE_NAME, selection, selectionArgs);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = database.update(TABLE_NAME,  contentValues, selection, selectionArgs);
        return count;
    }
}
