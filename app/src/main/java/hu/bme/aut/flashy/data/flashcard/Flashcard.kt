package hu.bme.aut.flashy.data.flashcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard")
data class Flashcard(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "term") val term: String,
    @ColumnInfo(name = "definition") val definition: String,
    @ColumnInfo(name = "collection") val collectionId: Long
)