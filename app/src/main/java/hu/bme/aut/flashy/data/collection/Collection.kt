package hu.bme.aut.flashy.data.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "collection")
data class Collection(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "color") val color: CollectionColor
) {
    enum class CollectionColor {
        YELLOW, BLUE, PURPLE, GREEN;

        companion object {
            @JvmStatic
            @TypeConverter
            fun getByOrdinal(ordinal: Int): CollectionColor? {
                return values().find { it.ordinal == ordinal }
            }

            @JvmStatic
            @TypeConverter
            fun toInt(color: CollectionColor): Int {
                return color.ordinal
            }
        }
    }
}