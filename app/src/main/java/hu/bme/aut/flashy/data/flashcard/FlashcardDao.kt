package hu.bme.aut.flashy.data.flashcard

import androidx.room.*

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard WHERE collection = :collection")
    fun getAll(collection: Long): List<Flashcard>

    @Query("SELECT * FROM flashcard WHERE id = :id")
    fun getWithId(id: Long): Flashcard

    @Insert
    fun insert(flashcard: Flashcard): Long

    @Update
    fun update(flashcard: Flashcard)

    @Delete
    fun deleteItem(flashcard: Flashcard)
}