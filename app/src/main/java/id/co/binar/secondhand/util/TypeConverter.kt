package id.co.binar.secondhand.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.graphics.drawable.toBitmap
import androidx.room.TypeConverter
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.binar.secondhand.data.local.model.SellerCategoryLocal
import id.co.binar.secondhand.data.local.model.SellerProductLocal
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import id.co.binar.secondhand.model.seller.product.GetProductResponse
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class TypeConverter {
    @TypeConverter
    fun categoriesFromListToString(list: List<GetCategoryResponse>?) : String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun categoriesFromStringToList(string: String?) : List<GetCategoryResponse>? {
        val list = object : TypeToken<List<GetCategoryResponse>?>() {}.type
        return Gson().fromJson(string, list)
    }

    @TypeConverter
    fun convertFromBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val bStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bStream)
        return bStream.toByteArray()
    }

    @TypeConverter
    fun convertFromByteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}

fun List<GetCategoryResponse>?.castFromRemoteToLocal(): List<SellerCategoryLocal> {
    val list = mutableListOf<SellerCategoryLocal>()
    this?.forEach {
        list.add(
            SellerCategoryLocal(
                name = it.name,
                id = it.id!!,
            )
        )
    }
    return list
}

fun List<SellerCategoryLocal>?.castFromLocalToRemote(): List<GetCategoryResponse> {
    val list = mutableListOf<GetCategoryResponse>()
    this?.forEach {
        list.add(
            GetCategoryResponse(
                name = it.name,
                id = it.id
            )
        )
    }
    return list
}

@JvmName("castFromRemoteToLocalGetProductResponse")
fun List<GetProductResponse>?.castFromRemoteToLocal(): List<SellerProductLocal> {
    val list = mutableListOf<SellerProductLocal>()
    this?.forEach {
        list.add(
            SellerProductLocal(
                imageName = it.imageName,
                userId = it.userId,
                imageUrl = it.imageUrl,
                name = it.name,
                description = it.description,
                basePrice = it.basePrice,
                location = it.location,
                id = it.id!!,
                categories = it.categories
            )
        )
    }
    return list
}

@JvmName("castFromLocalToRemoteSellerProductLocal")
fun List<SellerProductLocal>?.castFromLocalToRemote(): List<GetProductResponse> {
    val list = mutableListOf<GetProductResponse>()
    this?.forEach {
        list.add(
            GetProductResponse(
                imageName = it.imageName,
                userId = it.userId,
                imageUrl = it.imageUrl,
                name = it.name,
                description = it.description,
                basePrice = it.basePrice,
                location = it.location,
                id = it.id,
                categories = it.categories
            )
        )
    }
    return list
}

fun List<GetCategoryResponse>.toNameOnly(): String {
    val str = mutableListOf<String>()
    this.forEach {
        str.add(it.name.toString())
    }
    return str.joinToString()
}

fun List<GetCategoryResponse>.toIntOnly(): String {
    val int = mutableListOf<Int>()
    this.forEach {
        it.id?.let { i ->
            int.add(i)
        }
    }
    return int.joinToString()
}

fun Any.convertRupiah(): String {
    val localId = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localId)
    return formatter.format(this)
}

class MoneyTextWatcher(editText: EditText?) : TextWatcher {
    private val editTextWeakReference: WeakReference<EditText>
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        val editText: EditText? = editTextWeakReference.get()
        if (editText == null || editText.text.toString() == "") {
            return
        }
        editText.removeTextChangedListener(this)
        val parsed: BigDecimal = parseCurrencyValue(editText.text.toString())
        val formatted: String = numberFormat.format(parsed)
        editText.setText(formatted)
        editText.setSelection(formatted.length)
        editText.addTextChangedListener(this)
    }

    companion object {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        fun parseCurrencyValue(value: String): BigDecimal {
            try {
                val replaceRegex = java.lang.String.format(
                    "[%s,.\\s]", Objects.requireNonNull(
                        numberFormat.currency
                    ).displayName
                )
                val currencyValue = value.replace(replaceRegex.toRegex(), "")
                return BigDecimal(currencyValue)
            } catch (e: Exception) {}
            return BigDecimal.ZERO
        }
    }

    init {
        editTextWeakReference = WeakReference(editText)
        numberFormat.maximumFractionDigits = 0
        numberFormat.roundingMode = RoundingMode.FLOOR
    }
}

suspend fun Context.bitmap(data: Any?): Bitmap {
    val loader = ImageLoader(this)
    val req = ImageRequest.Builder(this)
        .data(data)
        .build()
    val result = (loader.execute(req) as SuccessResult).drawable
    return result.toBitmap()
}