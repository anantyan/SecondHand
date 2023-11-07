package id.co.binar.secondhand.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {
    private val _title = state.getLiveData<String>("TITLE")
    val title: LiveData<String> = _title
    fun title(title: String) {
        _title.postValue(title)
    }
}