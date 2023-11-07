package id.co.binar.secondhand.ui.dashboard.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.co.binar.secondhand.databinding.ListItemLostConnectionBinding

class HomeProductLoadStateAdapter(
    private val onClick : () -> Unit
) : LoadStateAdapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(private val binding: ListItemLostConnectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.txtRetry.isVisible = loadState is LoadState.Error
            binding.btnRetry.isVisible = loadState is LoadState.Error
            /*binding.txtRetry.text = (loadState as? LoadState.Error)?.error?.message.toString()*/
            binding.btnRetry.setOnClickListener {
                onClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        holder as ViewHolder
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemLostConnectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}