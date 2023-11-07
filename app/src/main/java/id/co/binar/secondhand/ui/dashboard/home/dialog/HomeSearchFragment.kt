package id.co.binar.secondhand.ui.dashboard.home.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.BottomSheetSearchHomeBinding
import id.co.binar.secondhand.databinding.ListItemProductHomeBinding
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.ui.dashboard.home.adapter.HomeProductAdapter
import id.co.binar.secondhand.ui.dashboard.home.adapter.HomeProductLoadStateAdapter
import id.co.binar.secondhand.ui.dashboard.home.HomeViewModel
import id.co.binar.secondhand.ui.dashboard.home.adapter.diffUtillProduct
import id.co.binar.secondhand.ui.product.ARGS_PASSING_SEE_DETAIL
import id.co.binar.secondhand.ui.product.ProductActivity
import id.co.binar.secondhand.util.Constant
import id.co.binar.secondhand.util.ItemDecoration
import id.co.binar.secondhand.util.PagingAdapterBuilder
import id.co.binar.secondhand.util.convertRupiah
import id.co.binar.secondhand.util.toNameOnly
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG_SEARCH_HOME_DIALOG = "SEARCH_HOME_DIALOG"

@AndroidEntryPoint
class HomeSearchFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSearchHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<HomeViewModel>()
    private lateinit var adapterProduct: PagingAdapterBuilder<GetProductResponse, ListItemProductHomeBinding>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSearchHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.txtSearch.doAfterTextChanged {
            lifecycleScope.launch {
                delay(700)
                viewModel.getProductBySearch(it.toString())
            }
        }

        adapterProduct = PagingAdapterBuilder(
            diffCallback = diffUtillProduct,
            inflater = ListItemProductHomeBinding::inflate
        )
        adapterProduct.binder { binding, item, _ ->
            binding.ivImageProduct.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.ivImageProduct))
            }
            if (item.status == Constant.ARRAY_STATUS[5]) {
                binding.imgSold.isVisible = true
                binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.neutral_2))
            } else {
                binding.imgSold.isVisible = false
                binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }
            binding.tvNamaProduct.text = item.name
            binding.tvHargaProduct.text = item.basePrice?.convertRupiah()
            binding.tvJenisProduct.text = item.categories?.toNameOnly()
        }
        adapterProduct.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        adapterProduct.addLoadStateListener {
            binding.progressBar.isVisible = it.source.refresh is LoadState.Loading
            binding.rvList.isVisible = it.source.refresh is LoadState.NotLoading
            binding.layoutError.isVisible = it.source.refresh is LoadState.Error
            binding.textView8.text = (it.source.refresh as? LoadState.Error)?.error?.message.toString()
        }
        adapterProduct.onClick { item, _ ->
            val intent = Intent(requireContext(), ProductActivity::class.java)
            intent.putExtra(ARGS_PASSING_SEE_DETAIL, item.id)
            requireActivity().startActivity(intent)
        }

        val loadStateHeader = HomeProductLoadStateAdapter { adapterProduct.retry() }
        val loadStateFooter = HomeProductLoadStateAdapter { adapterProduct.retry() }
        val concatAdapter = adapterProduct.withLoadStateHeaderAndFooter(
            header = loadStateHeader,
            footer = loadStateFooter
        )

        val gridLayout = GridLayoutManager(requireContext(), 2)
        gridLayout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0 && loadStateHeader.itemCount > 0) {
                    2
                } else if (position == concatAdapter.itemCount - 1 && loadStateFooter.itemCount > 0) {
                    2
                } else {
                    1
                }
            }
        }
        binding.rvList.setHasFixedSize(true)
        binding.rvList.layoutManager = gridLayout
        binding.rvList.itemAnimator = DefaultItemAnimator()
        binding.rvList.addItemDecoration(ItemDecoration(requireContext(), 2, 16))
        binding.rvList.isNestedScrollingEnabled = true
        binding.rvList.adapter = concatAdapter
    }

    private fun bindObserver() {
        viewModel.getProductBySearch()

        viewModel.getProductBySearch.observe(viewLifecycleOwner) {
            adapterProduct.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
