package id.co.binar.secondhand.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.data.local.model.AuthLocal
import id.co.binar.secondhand.model.auth.GetAuthRequest
import id.co.binar.secondhand.model.auth.GetAuthResponse
import id.co.binar.secondhand.repository.AuthRepository
import id.co.binar.secondhand.util.LiveEvent
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _login = MutableLiveData<Resource<GetAuthResponse>>()
    val login: LiveData<Resource<GetAuthResponse>> = _login
    fun login(field: GetAuthRequest) = CoroutineScope(Dispatchers.IO).launch {
        authRepository.login(field).collectLatest {
            _login.postValue(it)
        }
    }
}