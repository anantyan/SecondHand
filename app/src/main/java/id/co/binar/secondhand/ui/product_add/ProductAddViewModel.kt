package id.co.binar.secondhand.ui.product_add

import android.graphics.Bitmap
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.binar.secondhand.data.local.model.SellerProductPreviewLocal
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import id.co.binar.secondhand.model.seller.product.*
import id.co.binar.secondhand.repository.SellerRepository
import id.co.binar.secondhand.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProductAddViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
    state: SavedStateHandle
) : ViewModel() {
    val getTokenId = sellerRepository.store.getTokenId()
    val categoryProduct = sellerRepository.getCategory().asLiveData()

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap
    fun bitmap(bitmap: Bitmap) {
        _bitmap.postValue(bitmap)
    }

    private val _list = state.getLiveData<MutableList<GetCategoryResponse>>("LIST_CATEGORY_PRODUCT")
    val list: LiveData<MutableList<GetCategoryResponse>> = _list
    fun list(list: MutableList<GetCategoryResponse>) {
        _list.postValue(list.distinctBy { it.id }.toMutableList())
    }

    private val _lastList = state.getLiveData<MutableList<GetCategoryResponse>>("LAST_LIST_CATEGORY_PRODUCT")
    val lastList: LiveData<MutableList<GetCategoryResponse>> = _lastList
    fun lastList(list: MutableList<GetCategoryResponse>) {
        _lastList.postValue(list.distinctBy { it.id }.toMutableList())
    }

    private val _addProduct = MutableLiveData<Resource<AddProductResponse>>()
    val addProduct: LiveData<Resource<AddProductResponse>> = _addProduct
    fun addProduct(field: AddProductRequest, image: MultipartBody.Part) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.addProduct(field, image).collectLatest {
            _addProduct.postValue(it)
        }
    }

    private val _getIdProduct = state.getLiveData<Int>("ID_PRODUCT")
    fun getIdProduct(id_product: Int) {
        _getIdProduct.postValue(id_product)
    }

    val getProductById = _getIdProduct.switchMap {
        sellerRepository.getProductById(it).asLiveData()
    }

    private val _editProduct = MutableLiveData<Resource<UpdateProductByIdResponse>>()
    val editProduct: LiveData<Resource<UpdateProductByIdResponse>> = _editProduct
    fun editProduct(id_product: Int, field: UpdateProductByIdRequest, image: MultipartBody.Part) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.editProduct(id_product, field, image).collectLatest {
            _editProduct.postValue(it)
        }
    }

    fun setProductPreview(field: SellerProductPreviewLocal) = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.setProductPreview(field)
    }

    fun deleteProductPreview() = CoroutineScope(Dispatchers.IO).launch {
        sellerRepository.deleteProductPreview()
    }
}
