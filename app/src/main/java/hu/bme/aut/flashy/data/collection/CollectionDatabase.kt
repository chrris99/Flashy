package hu.bme.aut.flashy.data.collection

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Collection::class], version = 1)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}