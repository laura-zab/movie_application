package com.movietest.displaymovie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favMovies")
class DatabaseModel(
    @field:ColumnInfo(name = "id") @field:PrimaryKey var id: Int,
    @field:ColumnInfo(
        name = "image"
    ) var image: String,
    @field:ColumnInfo(name = "year") var year: String,
    @field:ColumnInfo(
        name = "title"
    ) var title: String,
    @field:ColumnInfo(name = "description") var description: String,
    @field:ColumnInfo(
        name = "releaseDate"
    ) var releaseDate: String,
    @field:ColumnInfo(name = "rating") var rating: String
)