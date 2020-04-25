/**
 * <p>
 *   This package provides tools to simplify using JDBC api. It supports SQL queries with named
 *   parameters and returns result iterators to process data in a streaming way, without storing the
 *   whole result set in memory.
 * </p>
 *
 * <p>
 *   When implementing repository classes start with {@link com.codeborne.iterjdbc.Query} and
 *   {@link com.codeborne.iterjdbc.Update} and next follow their API to get the results.
 * </p>
 *
 * <p>
 *   These two classes are the value objects and can be stored as constants.
 *   You will then need an instance of {@link java.sql.Connection} to bring them to life.
 * </p>
 *
 * <p>
 *   When testing service classes, you will need to mock repositories. Use
 *   {@link com.codeborne.iterjdbc.CloseableListIterator} for it.
 * </p>
 *
 * <p>
 *   This package provides you an opportunity to reuse prepared queries and iterate through results
 *   in an efficient way. But with it comes the responsibility to close all resources that you use.
 *   Don't forget to unit-test that you close {@link com.codeborne.iterjdbc.CloseableIterator}
 *   as well as {@link com.codeborne.iterjdbc.PreparedQuery} and
 *   {@link com.codeborne.iterjdbc.PreparedUpdate}.
 * </p>
 */
package com.codeborne.iterjdbc;
