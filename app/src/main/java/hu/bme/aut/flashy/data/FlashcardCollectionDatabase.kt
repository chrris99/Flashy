package hu.bme.aut.flashy.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FlashcardCollection::class], version = 1)
abstract class FlashcardCollectionDatabase : RoomDatabase() {
    abstract fun flashcardCollectionDao(): FlashcardCollectionDao
}