package id.co.binar.secondhand.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.model.auth.AddAuthRequest
import id.co.binar.secondhand.model.auth.AddAuthResponse
import id.co.binar.secondhand.repository.AuthRepository
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _register = MutableLiveData<Resource<AddAuthResponse>>()
    val register : LiveData<Resource<AddAuthResponse>> = _register
    fun register(field: AddAuthRequest) = CoroutineScope(Dispatchers.IO).launch {
        authRepository.register(field).collectLatest {
            _register.postValue(it)
        }
    }
}