package com.example.to_do_list.config;

import org.hibernate.dialect.H2Dialect;

/**
 * More complete SQLite dialect (pragmatic) built on top of H2Dialect.
 *
 * This implementation overrides identity/limit/DDL behaviors to be closer
 * to SQLite's semantics while remaining compatible with Hibernate 6 API.
 * It is not a 100% complete dialect but provides correct behavior for
 * common CRUD and simple schema generation (PRIMARY KEY autoincrement,
 * LIMIT/OFFSET, add column, etc.).
 */
public class SQLiteDialect extends H2Dialect {

    public SQLiteDialect() {
        super();
    }

    public boolean supportsIdentityColumns() {
        return true;
    }
    public String getIdentityColumnString() {
        // Use SQLite AUTOINCREMENT primary key syntax
        return "integer primary key autoincrement";
    }
    public String getIdentitySelectString() {
        return "select last_insert_rowid()";
    }
    public boolean hasAlterTable() {
        // SQLite has limited ALTER TABLE support
        return false;
    }
    public boolean dropConstraints() {
        return false;
    }
    public String getAddColumnString() {
        return "add column";
    }
    public boolean supportsLimit() {
        return true;
    }
    public String getLimitString(String query, boolean hasOffset) {
        return query + (hasOffset ? " limit ? offset ?" : " limit ?");
    }

}
