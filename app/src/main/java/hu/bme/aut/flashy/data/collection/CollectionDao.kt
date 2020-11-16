package hu.bme.aut.flashy.data.collection

import androidx.room.*

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection")
    fun getAll(): List<Collection>

    @Query("SELECT * FROM collection WHERE id = :id")
    fun getWithId(id: Long): Collection

    @Insert
    fun insert(collections: Collection): Long

    @Update
    fun update(collection: Collection)

    @Delete
    fun deleteItem(collection: Collection)

    @Query("DELETE FROM collection")
    fun nukeTable()
}