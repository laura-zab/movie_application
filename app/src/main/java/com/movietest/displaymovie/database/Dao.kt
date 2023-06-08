package com.movietest.displaymovie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @get:Query("select * from favMovies")
    val all: List<DatabaseModel?>?



    @Insert
    fun insertAll(vararg users: DatabaseModel?)

    @Delete
    fun delete(user: DatabaseModel?)
}