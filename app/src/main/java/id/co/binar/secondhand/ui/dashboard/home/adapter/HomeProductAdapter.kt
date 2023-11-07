package id.co.binar.secondhand.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ListItemProductHomeBinding
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.util.Constant
import id.co.binar.secondhand.util.convertRupiah
import id.co.binar.secondhand.util.toNameOnly

class HomeProductAdapter : PagingDataAdapter<GetProductResponse, RecyclerView.ViewHolder>(
    diffUtillProduct
) {

    private var _onClickAdapter: ((Int, GetProductResponse) -> Unit)? = null

    fun onClickAdapter(listener: (Int, GetProductResponse) -> Unit) {
        _onClickAdapter = listener
    }

    inner class ViewHolder(private val binding: ListItemProductHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                getItem(bindingAdapterPosition)?.let {
                    _onClickAdapter?.invoke(bindingAdapterPosition, it)
                }
            }
        }

        fun bind(item: GetProductResponse) {
            binding.ivImageProduct.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.ivImageProduct))
            }
            if (item.status == Constant.ARRAY_STATUS[5]) {
                binding.imgSold.isVisible = true
                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.neutral_2))
            } else {
                binding.imgSold.isVisible = false
                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
            binding.tvNamaProduct.text = item.name
            binding.tvHargaProduct.text = item.basePrice?.convertRupiah()
            binding.tvJenisProduct.text = item.categories?.toNameOnly()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemProductHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }
}