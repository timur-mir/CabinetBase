package home.howework.databaseofcompletedworks
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
class CabinetDetailViewModel(cabinetId: UUID) : ViewModel() {
    private val cabinetRepository = CabinetRepository.get()

    private val _cabinet: MutableStateFlow<Cabinet?> = MutableStateFlow(null)
    val cabinet: StateFlow<Cabinet?> = _cabinet.asStateFlow()

    init {
        viewModelScope.launch {
            _cabinet.value = cabinetRepository.getCabinet(cabinetId)
        }
    }


    fun updateCabinet(onUpdate: (Cabinet) -> Cabinet) {
        _cabinet.update { oldCabinet ->
            oldCabinet?.let { onUpdate(it) }
        }
        cabinet.value?.let { cabinetRepository.updateCabinet(it) }
    }

    override fun onCleared() {
        super.onCleared()
        cabinet.value?.let { cabinetRepository.updateCabinet(it) }
    }
}

class CabinetDetailViewModelFactory(
    private val cabinetId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CabinetDetailViewModel(cabinetId) as T
    }
}
