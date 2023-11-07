package id.co.binar.secondhand.ui.dashboard.list_sell.child

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.data.local.model.SellerProductLocal
import id.co.binar.secondhand.model.ErrorResponse
import id.co.binar.secondhand.repository.SellerRepository
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListSellByProductViewModel @Inject constructor(
    private val sellerRepository: SellerRepository
) : ViewModel() {
    private val _deleteProduct = MutableLiveData<Resource<ErrorResponse>>()
    val deleteProduct: LiveData<Resource<ErrorResponse>> = _deleteProduct
    fun deleteProduct(id_product: Int) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.deleteProduct(id_product).collectLatest {
            _deleteProduct.postValue(it)
        }
    }

    private val _getProduct = MutableLiveData<Resource<List<SellerProductLocal>>>()
    val getProduct: LiveData<Resource<List<SellerProductLocal>>> = _getProduct
    fun getProduct() = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.getProduct().collectLatest {
            _getProduct.postValue(it)
        }
    }
}