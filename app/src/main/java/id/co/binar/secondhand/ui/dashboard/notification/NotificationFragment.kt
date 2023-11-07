package id.co.binar.secondhand.ui.dashboard.notification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.databinding.FragmentNotificationBinding
import id.co.binar.secondhand.model.notification.GetNotifResponse
import id.co.binar.secondhand.ui.dashboard.notification.adapter.NotificationAdapter
import id.co.binar.secondhand.ui.product.ARGS_PASSING_SEE_DETAIL
import id.co.binar.secondhand.ui.product.ProductActivity
import id.co.binar.secondhand.util.ItemDecoration
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.onSnackError

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NotificationViewModel>()
    private val adapterNotif = NotificationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.getNotif()

        viewModel.updateNotif.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val list = mutableListOf<GetNotifResponse>()
                    it.data?.let {
                        list.add(it)
                        list.addAll(adapterNotif.asyncDiffer.currentList)
                        adapterNotif.asyncDiffer.submitList(list.distinctBy { it.id }.sortedByDescending { it.id })
                    }
                }
                is Resource.Loading -> {}
                is Resource.Error -> requireContext().onSnackError(binding.root, it.error?.message.toString())
            }
        }

        viewModel.getNotif.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.apply {
                        swipeRefresh.isRefreshing = false
                        if (it.data.isNullOrEmpty()) {
                            rvMovie.isVisible = false
                            layoutEmpty.isVisible = true
                            layoutError.isVisible = false
                        } else {
                            rvMovie.isVisible = true
                            layoutError.isVisible = false
                            layoutEmpty.isVisible = false
                            adapterNotif.asyncDiffer.submitList(it.data)
                            rvMovie.adapter = adapterNotif
                        }
                    }
                }
                is Resource.Loading -> {
                    binding.apply {
                        swipeRefresh.isRefreshing = true
                        rvMovie.isVisible = false
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.apply {
                        swipeRefresh.isRefreshing = false
                        rvMovie.isVisible = false
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = true
                        textView8.text = it.error?.message.toString()
                        requireContext().onSnackError(root, it.error?.message.toString())
                    }
                }
            }
        }
    }

    private fun bindView() {
        binding.rvMovie.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(ItemDecoration(requireContext(),null,16))
        }

        adapterNotif.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onClickAdapter { _, item ->
                item.id?.let { viewModel.updateNotif(it) }
                if (item.status == "create") {
                    val intent = Intent(requireContext(), ProductActivity::class.java)
                    intent.putExtra(ARGS_PASSING_SEE_DETAIL, item.productId)
                    requireActivity().startActivity(intent)
                }
            }
        }

        binding.btnReload.setOnClickListener {
            viewModel.getNotif()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getNotif()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}