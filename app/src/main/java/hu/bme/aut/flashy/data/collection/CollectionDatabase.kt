package hu.bme.aut.flashy.data.collection

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Collection::class], version = 4)
@TypeConverters(value = [Collection.CollectionColor::class])
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}