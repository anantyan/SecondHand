package id.co.binar.secondhand.ui.dashboard.list_sell.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.model.seller.order.GetOrderResponse
import id.co.binar.secondhand.repository.NotificationRepository
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoBidSuccessViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    state: SavedStateHandle
) : ViewModel() {
    private val _args = state.getLiveData<GetOrderResponse>("ARGS_ORDER")
    val args: LiveData<GetOrderResponse> = _args
    fun args(value: GetOrderResponse) {
        _args.postValue(value)
    }

    private val _sendNotif = MutableLiveData<Resource<Boolean>>()
    val sendNotif: LiveData<Resource<Boolean>> = _sendNotif
    fun sendNotif(to: String?, title: String?, message: String?) = CoroutineScope(
        Dispatchers.IO).launch {
        notificationRepository.sendNotifOrderSuccess(to, title, message).collectLatest {
            _sendNotif.postValue(it)
        }
    }
}