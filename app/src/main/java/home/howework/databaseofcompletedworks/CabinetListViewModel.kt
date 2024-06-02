package home.howework.databaseofcompletedworks
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CabinetListViewModel : ViewModel() {
    private val cabinetRepository = CabinetRepository.get()

    private val _cabinets: MutableStateFlow<List<Cabinet>> = MutableStateFlow(emptyList())
    val cabinets: StateFlow<List<Cabinet>>
        get() = _cabinets.asStateFlow()

    private val _cabinetD: MutableStateFlow<Cabinet?> = MutableStateFlow(null)
    val cabinetD: StateFlow<Cabinet?> = _cabinetD.asStateFlow()

    init {
        viewModelScope.launch {
            cabinetRepository.getCabinets().collect {
                _cabinets.value = it
            }
        }
    }
    suspend fun getCabinet(cabinet: Cabinet){
        viewModelScope.launch {
            _cabinetD.value = cabinetRepository.getCabinet(cabinet.id)
        }
    }
    suspend fun addCabinet(cabinet: Cabinet) {
        cabinetRepository.addCabinet(cabinet)
    }
    suspend fun deleteCabinet(cabinet: Cabinet) {
        cabinetRepository.deleteCabinet(cabinet)
    }
}
