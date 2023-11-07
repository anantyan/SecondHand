package id.co.binar.secondhand.ui.dashboard.list_sell.dialog

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.model.seller.order.GetOrderResponse
import id.co.binar.secondhand.model.seller.order.UpdateOrderRequest
import id.co.binar.secondhand.model.seller.product.GetProductResponse
import id.co.binar.secondhand.repository.NotificationRepository
import id.co.binar.secondhand.repository.SellerRepository
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoBidViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
    private val notificationRepository: NotificationRepository,
    state: SavedStateHandle
) : ViewModel() {
    private val _response = MutableLiveData<Resource<GetOrderResponse>>()
    val response : LiveData<Resource<GetOrderResponse>> = _response

    private val _response1 = MutableLiveData<Resource<GetProductResponse>>()
    val response1 : LiveData<Resource<GetProductResponse>> = _response1

    private val _sendNotif = MutableLiveData<Resource<Boolean>>()
    val sendNotif: LiveData<Resource<Boolean>> = _sendNotif

    private val _args = state.getLiveData<Int>("ARGS_ID_ORDER")
    fun args(value: Int) {
        _args.postValue(value)
    }

    val getOrderById = _args.switchMap {
        sellerRepository.getOrderById(it).asLiveData()
    }

    fun sendNotif(to: String?, title: String?, message: String?) = CoroutineScope(Dispatchers.IO).launch {
        notificationRepository.sendNotifOrderSuccess(to, title, message).collectLatest {
            _sendNotif.postValue(it)
        }
    }

    fun updateOrder(id: Int, field: UpdateOrderRequest) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.updateOrder(id, field).collectLatest {
            _response.postValue(it)
        }
    }

    fun updateProduct(id: Int, field: UpdateOrderRequest) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.editProduct(id, field).collectLatest {
            _response1.postValue(it)
        }
    }
}