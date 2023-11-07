package id.co.binar.secondhand.ui.dashboard.list_sell.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ListItemProductAddHomeBinding
import id.co.binar.secondhand.databinding.ListItemProductHomeBinding
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import id.co.binar.secondhand.model.seller.product.GetProductResponse
import id.co.binar.secondhand.util.convertRupiah

class ListSellProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val asyncDiffer = AsyncListDiffer(this, diffUtilCallback)
    private var _onClickAdapter: ((Int, GetProductResponse) -> Unit)? = null

    fun onClickAdapter(listener: (Int, GetProductResponse) -> Unit) {
        _onClickAdapter = listener
    }

    private fun List<GetCategoryResponse>.toNameOnly(): String {
        val str = mutableListOf<String>()
        this.forEach {
            str.add(it.name.toString())
        }
        return str.joinToString()
    }

    inner class ProductAddViewHolder(val binding: ListItemProductAddHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                _onClickAdapter?.invoke(bindingAdapterPosition, asyncDiffer.currentList[bindingAdapterPosition])
            }
        }
    }

    inner class ViewHolder(val binding: ListItemProductHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                _onClickAdapter?.invoke(bindingAdapterPosition, asyncDiffer.currentList[bindingAdapterPosition])
            }
        }

        fun bind(item: GetProductResponse) {
            binding.ivImageProduct.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.ivImageProduct))
            }
            binding.tvNamaProduct.text = item.name
            binding.tvHargaProduct.text = item.basePrice?.convertRupiah()
            binding.tvJenisProduct.text = item.categories?.toNameOnly()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.list_item_category_product_add -> ProductAddViewHolder(
                ListItemProductAddHomeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.list_item_category_home -> ViewHolder(
                ListItemProductHomeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(asyncDiffer.currentList[position])
            is ProductAddViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.list_item_category_product_add
            else -> R.layout.list_item_category_home
        }
    }
}

private val diffUtilCallback = object : DiffUtil.ItemCallback<GetProductResponse>() {
    override fun areItemsTheSame(oldItem: GetProductResponse, newItem: GetProductResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetProductResponse, newItem: GetProductResponse): Boolean {
        return oldItem == newItem
    }
}