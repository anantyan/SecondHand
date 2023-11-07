package id.co.binar.secondhand.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import id.co.binar.secondhand.R

fun Context.onSnackError(view: View, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
    snackbar.show()
}

fun Context.onSnackSuccess(view: View, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.success))
    snackbar.show()
}

fun Context.onToast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

class ListAdapterBuilder<T: Any, VB : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : ListAdapter<T, ListAdapterBuilder.ViewHolder<VB>>(diffCallback) {

    private var _binder: ((VB, T, Int) -> Unit)? = null
    private var _onClick: ((T, Int) -> Unit)? = null
    private var _onLongClick: ((T, Int) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        val item = getItem(position)
        item?.let {
            _binder?.invoke(holder.binding, item, position)
            holder.binding.root.setOnClickListener { _onClick?.invoke(item, position) }
            holder.binding.root.setOnLongClickListener { _onLongClick?.invoke(item, position) ?: false }
        }
    }

    class ViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    fun onClick(listener: (T, Int) -> Unit) { _onClick = listener }
    fun onLong(listener: (T, Int) -> Boolean) { _onLongClick = listener }
    fun binder(listener: (VB, T, Int) -> Unit) { _binder = listener }
}

class PagingAdapterBuilder<T : Any, VB : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : PagingDataAdapter<T, PagingAdapterBuilder.ViewHolder<VB>>(diffCallback) {

    private var _onClick: ((T, Int) -> Unit)? = null
    private var _onLongClick: ((T, Int) -> Boolean)? = null
    private var _binder: ((VB, T, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        val item = getItem(position)
        item?.let {
            _binder?.invoke(holder.binding, item, position)
            holder.binding.root.setOnClickListener { _onClick?.invoke(item, position) }
            holder.binding.root.setOnLongClickListener { _onLongClick?.invoke(item, position) ?: false }
        }
    }

    class ViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    fun onClick(listener: (T, Int) -> Unit) {
        _onClick = listener
    }
    fun onLong(listener: (T, Int) -> Boolean) {
        _onLongClick = listener
    }
    fun binder(listener: (VB, T, Int) -> Unit) {
        _binder = listener
    }
}