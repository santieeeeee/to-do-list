package com.example.to_do_list.config;

import org.hibernate.dialect.H2Dialect;

/**
 * Lightweight SQLite dialect shim.
 *
 * This class intentionally extends H2Dialect to ensure compatibility with the
 * Hibernate 6 API used by Spring Boot in this project. It acts as a pragmatic
 * shim: SQL generated will be H2-like, which is sufficient for simple CRUD and
 * schema generation against SQLite for local development. For production use
 * a more complete, SQLite-specific dialect implementation is recommended.
 */
public class SQLiteDialect extends H2Dialect {
    public SQLiteDialect() {
        super();
    }
}
