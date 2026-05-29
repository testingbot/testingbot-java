/**
 * Plain Gson-mapped POJOs for the TestingBot REST API resources.
 *
 * <p>Each class represents one entity from the
 * <a href="https://testingbot.com/support/api">TestingBot REST API</a>;
 * field names map to snake_case JSON keys via
 * {@link com.google.gson.annotations.SerializedName}.
 *
 * <p>{@code *Collection} classes wrap paginated list responses with
 * {@code data} (the entity list) and {@code meta} (the
 * {@code offset}/{@code count}/{@code total} block).
 */
package com.testingbot.models;
