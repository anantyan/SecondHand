package id.co.binar.secondhand.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ListItemCategoryHomeBinding
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse

class HomeCategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val asyncDiffer = AsyncListDiffer(this, diffUtilCategory)
    private val setPosition: Int = 0
    private var _onClickAdapter: ((Int, GetCategoryResponse) -> Unit)? = null

    inner class ViewHolder(val binding: ListItemCategoryHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                _onClickAdapter?.invoke(bindingAdapterPosition, asyncDiffer.currentList[bindingAdapterPosition])
            }
        }

        fun bind(item: GetCategoryResponse) {
            binding.txtCategory.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemCategoryHomeBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        holder.bind(asyncDiffer.currentList[position])
        if (setPosition == position) {
            holder.binding.root.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_500))
            holder.binding.imgView.setImageResource(R.drawable.ic_round_search_white)
            holder.binding.txtCategory.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.binding.root.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_100))
            holder.binding.imgView.setImageResource(R.drawable.ic_round_search_24)
            holder.binding.txtCategory.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    fun onClickAdapter(listener: (Int, GetCategoryResponse) -> Unit) {
        _onClickAdapter = listener
    }
}

val diffUtilCategory = object : DiffUtil.ItemCallback<GetCategoryResponse>() {
    override fun areItemsTheSame(oldItem: GetCategoryResponse, newItem: GetCategoryResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetCategoryResponse, newItem: GetCategoryResponse): Boolean {
        return oldItem == newItem
    }
}