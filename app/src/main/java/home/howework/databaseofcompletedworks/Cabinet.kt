package home.howework.databaseofcompletedworks

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
@Entity
data class Cabinet(
    @PrimaryKey val id: UUID= UUID.randomUUID(),
    var title: String="",
    val date: Date=Date(),
    var isMainCabinet: Boolean=false,
    val master: String = "",
    var photoFileName: String? = null,
    var photoFileName2: String? = null,
    var photoFileName3: String? = null,
    var photoFileName4: String? = null
)