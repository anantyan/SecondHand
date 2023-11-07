package id.co.binar.secondhand.ui.product_add.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ListItemCategoryProductAddBinding
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse

class CategoryDialogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val asyncDiffer = AsyncListDiffer(this, diffUtilCallback)
    private var _onClickAdapter: ((Int, GetCategoryResponse) -> Unit)? = null

    inner class ViewHolder(private val binding: ListItemCategoryProductAddBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (asyncDiffer.currentList[bindingAdapterPosition].check == true) {
                    asyncDiffer.currentList[bindingAdapterPosition].check = false
                    _onClickAdapter?.invoke(bindingAdapterPosition, asyncDiffer.currentList[bindingAdapterPosition])
                    binding.imgCheck.setImageResource(R.drawable.ic_round_check_circle_outline_24)
                } else {
                    asyncDiffer.currentList[bindingAdapterPosition].check = true
                    _onClickAdapter?.invoke(bindingAdapterPosition, asyncDiffer.currentList[bindingAdapterPosition])
                    binding.imgCheck.setImageResource(R.drawable.ic_round_check_circle_24)
                }
            }
        }

        fun bind(item: GetCategoryResponse) {
            binding.txtName.text = item.name
            if (asyncDiffer.currentList[bindingAdapterPosition].check == true) {
                binding.imgCheck.setImageResource(R.drawable.ic_round_check_circle_24)
            } else {
                binding.imgCheck.setImageResource(R.drawable.ic_round_check_circle_outline_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCategoryProductAddBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        holder.bind(asyncDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    fun onClickAdapter(listener: (Int, GetCategoryResponse) -> Unit) {
        _onClickAdapter = listener
    }
}

private val diffUtilCallback = object : DiffUtil.ItemCallback<GetCategoryResponse>() {
    override fun areItemsTheSame(oldItem: GetCategoryResponse, newItem: GetCategoryResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetCategoryResponse, newItem: GetCategoryResponse): Boolean {
        return oldItem == newItem
    }
}