package hu.bme.aut.flashy.data

import androidx.room.*

@Dao
interface FlashcardCollectionDao {
    @Query("SELECT * FROM flashcardcollection")
    fun getAll(): List<FlashcardCollection>

    @Insert
    fun insert(flashcardCollections: FlashcardCollection): Long

    @Update
    fun update(flashcardCollection: FlashcardCollection)

    @Delete
    fun deleteItem(flashcardCollection: FlashcardCollection)
}