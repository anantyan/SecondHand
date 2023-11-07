package id.co.binar.secondhand.ui.dashboard.notification.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ListItemNotificationBinding
import id.co.binar.secondhand.model.notification.GetNotifResponse
import id.co.binar.secondhand.util.convertRupiah
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NotificationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val asyncDiffer = AsyncListDiffer(this, diffUtilCallback)
    private var _onClickAdapter: ((Int, GetNotifResponse) -> Unit)? = null

    fun onClickAdapter(listener: (Int, GetNotifResponse) -> Unit) {
        _onClickAdapter = listener
    }

    inner class ViewHolder(val binding: ListItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                _onClickAdapter?.invoke(bindingAdapterPosition, asyncDiffer.currentList[bindingAdapterPosition])
            }
        }

        fun bind(item: GetNotifResponse) {
            binding.imageView.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.imageView))
            }
            val formattedDate = item.transactionDate?.let {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                val outputFormatter = DateTimeFormatter.ofPattern("HH:mm, dd MMM yyy", Locale.ENGLISH)
                val date = LocalDateTime.parse(item.transactionDate, inputFormatter)
                outputFormatter.format(date)
            }
            val bidPrice = item.bidPrice?.let {
                "Ditawar ${item.bidPrice.convertRupiah()}"
            }
            val status = when (item.status) {
                "create" -> {
                    binding.tvNotifHarga.paintFlags = binding.tvNotifHarga.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    "Berhasil diterbitkan"
                }
                "accepted" -> {
                    binding.tvNotifHarga.paintFlags = binding.tvNotifHarga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    "Penawaran telah diterima"
                }
                "declined" -> {
                    binding.tvNotifHarga.paintFlags = binding.tvNotifHarga.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    "Penawaran ditolak"
                }
                else -> {
                    binding.tvNotifHarga.paintFlags = binding.tvNotifHarga.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    "Penawaran produk"
                }
            }
            when (item.read) {
                true -> {
                    binding.bulletNotif.setImageResource(R.drawable.ic_circle_notif_read)
                }
                false -> {
                    binding.bulletNotif.setImageResource(R.drawable.ic_circle_notif)
                }
                else -> {
                    binding.bulletNotif.isVisible = false
                }
            }
            binding.tvNotifTime.text = formattedDate
            binding.tvNotifProduct.text = status
            binding.tvNamaProduct.text = item.productName
            binding.tvNotifHarga.text = item.basePrice?.convertRupiah()
            binding.tvNotifTawar.isVisible = item.bidPrice != null
            binding.tvNotifTawar.text = bidPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
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
}

private val diffUtilCallback = object : DiffUtil.ItemCallback<GetNotifResponse>() {
    override fun areItemsTheSame(oldItem: GetNotifResponse, newItem: GetNotifResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetNotifResponse, newItem: GetNotifResponse): Boolean {
        return oldItem == newItem
    }
}