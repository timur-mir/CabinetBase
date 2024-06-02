package home.howework.databaseofcompletedworks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import home.howework.databaseofcompletedworks.Cabinet


@Database(entities = [Cabinet::class], version =1)
@TypeConverters(CabinetTypeConverters::class)
abstract class CabinetDatabase : RoomDatabase() {
    abstract fun cabinetDao(): CabinetDao
}
