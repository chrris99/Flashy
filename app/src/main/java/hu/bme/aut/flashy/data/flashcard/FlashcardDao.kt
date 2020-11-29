package hu.bme.aut.flashy.data.flashcard

import androidx.room.*

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard WHERE collection = :collection")
    fun getAll(collection: Long): List<Flashcard>

    @Query("SELECT COUNT(id) FROM flashcard")
    fun getFlashcardCount(): Int

    @Query("SELECT COUNT(id) FROM flashcard WHERE collection = :collection and learned=1")
    fun getLearnedFlashcardCount(collection: Long): Int

    @Query("SELECT * FROM flashcard WHERE id = :id")
    fun getWithId(id: Long): Flashcard

    @Insert
    fun insert(flashcard: Flashcard): Long

    @Update
    fun update(flashcard: Flashcard)

    @Delete
    fun deleteItem(flashcard: Flashcard)

    @Query("DELETE FROM flashcard")
    fun nukeTable()
}