package com.hct.calendar.dao;

import android.provider.BaseColumns;

/**
 * table: almanacTable field: eachDay,dateJson
 * 
 * @author cat
 * 
 */
final public class AlmanacColumns implements BaseColumns {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private AlmanacColumns() {
    }

    public static final String TABLE_NAME = "almanacTable"; // table name
    static final String COLUMN_DATE = "eachDay"; //
    static final String COLUMN_DATE_JSON = "dateJson"; //

}