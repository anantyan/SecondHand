package id.co.binar.secondhand.ui.dashboard.list_sell.child

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
import id.co.binar.secondhand.databinding.FragmentListSellByDiminatiBinding
import id.co.binar.secondhand.ui.dashboard.list_sell.adapter.ListSellOrderAdapter
import id.co.binar.secondhand.ui.dashboard.list_sell.dialog.InfoBidFragment
import id.co.binar.secondhand.ui.dashboard.list_sell.dialog.TAG_INFO_BID_DIALOG
import id.co.binar.secondhand.util.ItemDecoration
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.onSnackError

@AndroidEntryPoint
class ListSellByDiminatiFragment : Fragment() {

    private var _binding: FragmentListSellByDiminatiBinding? = null
    private val binding get() = _binding!!
    private val adapter = ListSellOrderAdapter()
    private val viewModel by viewModels<ListSellByDiminatiViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSellByDiminatiBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.apply {
            rvList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(ItemDecoration(requireContext(),null,16))
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.getOrder()
            }

            btnReload.setOnClickListener {
                viewModel.getOrder()
            }
        }

        adapter.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onClickAdapter { _, item ->
                val dialog = item.id?.let { InfoBidFragment.newInstance(it) }
                dialog?.show(childFragmentManager, TAG_INFO_BID_DIALOG)
            }
        }
    }

    private fun bindObserver() {
        viewModel.getOrder()

        viewModel.getOrder.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.apply {
                        swipeRefresh.isRefreshing = false
                        if (it.data.isNullOrEmpty()) {
                            rvList.isVisible = false
                            layoutEmpty.isVisible = true
                            layoutError.isVisible = false
                        } else {
                            rvList.isVisible = true
                            layoutError.isVisible = false
                            layoutEmpty.isVisible = false
                            adapter.asyncDiffer.submitList(it.data)
                            rvList.adapter = adapter
                        }
                    }
                }
                is Resource.Loading -> {
                    binding.apply {
                        swipeRefresh.isRefreshing = true
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = false
                        rvList.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.apply {
                        swipeRefresh.isRefreshing = false
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = true
                        rvList.isVisible = false
                        textView8.text = it.error?.message.toString()
                        requireContext().onSnackError(root, it.error?.message.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}