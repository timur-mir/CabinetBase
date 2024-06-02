package home.howework.databaseofcompletedworks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import android.content.Context
import androidx.room.Room
import home.howework.databaseofcompletedworks.database.CabinetDatabase

import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File

private const val DATABASE_NAME = "cabinetdatabase"

class CabinetRepository @OptIn(DelicateCoroutinesApi::class)
private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val database: CabinetDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            CabinetDatabase::class.java,
            DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
      //  .createFromAsset(DATABASE_NAME)
      private val filesDir=context.applicationContext.filesDir

    fun getCabinets(): Flow<List<Cabinet>> = database.cabinetDao().getCabinets()

    suspend fun getCabinet(id: UUID): Cabinet = database.cabinetDao().getCabinet(id)

    fun updateCabinet(cabinet: Cabinet) {
        coroutineScope.launch {
            database.cabinetDao().updateCabinet(cabinet)
        }
    }

    suspend fun addCabinet(cabinet: Cabinet) {
        database.cabinetDao().addCabinet(cabinet)
    }
    fun getPhotoFile(cabinet: Cabinet): File = File(filesDir, cabinet.photoFileName)

    fun getPhotoFile2(cabinet: Cabinet): File = File(filesDir, cabinet.photoFileName2)

    fun getPhotoFile3(cabinet: Cabinet): File = File(filesDir, cabinet.photoFileName3)

    fun getPhotoFile4(cabinet: Cabinet): File = File(filesDir, cabinet.photoFileName4)

    fun deleteCabinet(cabinet: Cabinet) {

        database.cabinetDao().deleteCabinet(cabinet)

    }

    companion object {
        private var INSTANCE: CabinetRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CabinetRepository(context)
            }
        }

        fun get(): CabinetRepository {
            return INSTANCE
                ?: throw IllegalStateException("CabinetRepository must be initialized")
        }
    }
}