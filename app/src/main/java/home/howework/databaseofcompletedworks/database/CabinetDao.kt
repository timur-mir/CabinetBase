package home.howework.databaseofcompletedworks.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import home.howework.databaseofcompletedworks.Cabinet
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CabinetDao {
    @Query("SELECT * FROM cabinet")
    fun getCabinets(): Flow<List<Cabinet>>

    @Query("SELECT * FROM cabinet WHERE id=(:id)")
    suspend fun getCabinet(id: UUID): Cabinet

    @Update
    suspend fun updateCabinet(cabinet: Cabinet)

    @Insert
    suspend fun addCabinet(cabinet: Cabinet)

    @Delete
    fun deleteCabinet(cabinet:Cabinet)
}