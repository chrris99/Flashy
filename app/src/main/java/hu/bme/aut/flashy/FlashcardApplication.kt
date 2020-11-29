package hu.bme.aut.flashy

import android.app.Application
import androidx.room.Room
import hu.bme.aut.flashy.data.collection.CollectionDatabase
import hu.bme.aut.flashy.data.flashcard.FlashcardDatabase

class FlashcardApplication : Application() {

    companion object {
        lateinit var collectionDatabase : CollectionDatabase
            private set

    }

    override fun onCreate() {
        super.onCreate()

        collectionDatabase = Room.databaseBuilder(
            applicationContext,
            CollectionDatabase::class.java,
            "collection-database"
        ).build()

    }
}