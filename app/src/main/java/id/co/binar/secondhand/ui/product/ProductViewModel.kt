package id.co.binar.secondhand.ui.product

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.model.buyer.order.AddOrderRequest
import id.co.binar.secondhand.model.buyer.order.GetOrderResponse
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.model.notification.NotificationUsers
import id.co.binar.secondhand.model.seller.product.AddProductRequest
import id.co.binar.secondhand.model.seller.product.AddProductResponse
import id.co.binar.secondhand.model.seller.product.UpdateProductByIdRequest
import id.co.binar.secondhand.model.seller.product.UpdateProductByIdResponse
import id.co.binar.secondhand.repository.AuthRepository
import id.co.binar.secondhand.repository.BuyerRepository
import id.co.binar.secondhand.repository.NotificationRepository
import id.co.binar.secondhand.repository.SellerRepository
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val buyerRepository: BuyerRepository,
    private val sellerRepository: SellerRepository,
    private val notificationRepository: NotificationRepository,
    authRepository: AuthRepository,
    state: SavedStateHandle
) : ViewModel() {
    val getAccount = authRepository.getAccount().asLiveData()

    private val _passToBiding = state.getLiveData<GetProductResponse>("STATE_PRODUCT")
    val passToBiding: LiveData<GetProductResponse> = _passToBiding
    fun passToBiding(item: GetProductResponse) {
        _passToBiding.postValue(item)
    }

    private val _getProductById = state.getLiveData<Int>("STATE_ID_PRODUCT")
    fun getProductById(product_id: Int) {
        _getProductById.postValue(product_id)
    }

    val getProductById = _getProductById.switchMap {
        buyerRepository.getProductById(it).asLiveData()
    }

    val getProductId: LiveData<Int> = _getProductById

    private val _newOrder = MutableLiveData<Resource<GetOrderResponse>>()
    val newOrder: LiveData<Resource<GetOrderResponse>> = _newOrder
    fun newOrder(field: AddOrderRequest) = CoroutineScope(Dispatchers.IO).launch {
        buyerRepository.newOrder(field).collectLatest {
            _newOrder.postValue(it)
        }
    }

    private val _addProduct = MutableLiveData<Resource<AddProductResponse>>()
    val addProduct: LiveData<Resource<AddProductResponse>> = _addProduct
    fun addProduct(field: AddProductRequest, image: MultipartBody.Part) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.addProduct(field, image).collectLatest {
            _addProduct.postValue(it)
        }
    }

    private val _editProduct = MutableLiveData<Resource<UpdateProductByIdResponse>>()
    val editProduct: LiveData<Resource<UpdateProductByIdResponse>> = _editProduct
    fun editProduct(id_product: Int, field: UpdateProductByIdRequest, image: MultipartBody.Part) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.editProduct(id_product, field, image).collectLatest {
            _editProduct.postValue(it)
        }
    }

    fun getProductPreview() = sellerRepository.getProductPreview().asLiveData()

    private val _sendNotif = MutableLiveData<Resource<Boolean>>()
    val sendNotif: LiveData<Resource<Boolean>> = _sendNotif
    fun sendNotif(product: GetProductResponse?, order: GetOrderResponse?) = CoroutineScope(Dispatchers.IO).launch {
        notificationRepository.sendNotif(product, order).collectLatest {
            _sendNotif.postValue(it)
        }
    }
}