package id.co.binar.secondhand.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ListItemBannerHomeBinding
import id.co.binar.secondhand.model.seller.banner.GetBannerResponse

class HomeBannerAdapter : ListAdapter<GetBannerResponse, RecyclerView.ViewHolder>(diffUtilBanner) {
    inner class ViewHolder(private val binding: ListItemBannerHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetBannerResponse) {
            binding.root.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.root))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemBannerHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        holder.bind(getItem(position))
    }
}

val diffUtilBanner = object : DiffUtil.ItemCallback<GetBannerResponse>() {
    override fun areItemsTheSame(oldItem: GetBannerResponse, newItem: GetBannerResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetBannerResponse, newItem: GetBannerResponse): Boolean {
        return oldItem == newItem
    }
}