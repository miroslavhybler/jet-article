package com.jet.article.example.devblog.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * @author Miroslav Hýbler <br>
 * created on 20.08.2024
 */
@Database(
    entities = [PostItem::class],
    version = 1,
    exportSchema = true,
)
abstract class LocalDatabase constructor() : RoomDatabase() {

    companion object {
        fun create(context: Context): LocalDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = LocalDatabase::class.java,
                name = "local-database"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

    val postDao: PostDao
        get() = postDao()



    protected abstract fun postDao(): PostDao


    @Dao
    interface PostDao : BaseDao<PostItem> {

        @Query("SELECT * FROM posts")
        fun getAll(): List<PostItem>


        @Query("SELECT EXISTS(SELECT id FROM posts WHERE url=:url)")
        fun contains(url: String): Boolean
    }


    interface BaseDao<T> {

        @Insert
        fun insert(item: T)

        @Insert
        fun insert(items: List<T>)
    }
}