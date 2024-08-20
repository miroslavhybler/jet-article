package com.jet.article.example.devblog.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jet.article.data.HtmlElement

@Entity(tableName = "posts")
data class PostItem constructor(
        @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT)
        val title: String,
        @ColumnInfo(name = "url", typeAffinity = ColumnInfo.TEXT)
        val url: String,
        @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
        val time: String,
        @ColumnInfo(name = "description", typeAffinity = ColumnInfo.TEXT)
        val description: String,
        @ColumnInfo(name = "image_url", typeAffinity = ColumnInfo.TEXT)
        val image: String,
) {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
        var id: Int = 0
}