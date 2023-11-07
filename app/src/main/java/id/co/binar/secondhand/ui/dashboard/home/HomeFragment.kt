package id.co.binar.secondhand.ui.dashboard.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.size.ViewSizeResolver
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.FragmentHomeBinding
import id.co.binar.secondhand.databinding.ListItemBannerHomeBinding
import id.co.binar.secondhand.databinding.ListItemCategoryHomeBinding
import id.co.binar.secondhand.databinding.ListItemProductHomeBinding
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.model.seller.banner.GetBannerResponse
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import id.co.binar.secondhand.ui.dashboard.home.adapter.HomeBannerAdapter
import id.co.binar.secondhand.ui.dashboard.home.adapter.HomeCategoryAdapter
import id.co.binar.secondhand.ui.dashboard.home.adapter.HomeDefaultAdapter
import id.co.binar.secondhand.ui.dashboard.home.adapter.diffUtilBanner
import id.co.binar.secondhand.ui.dashboard.home.adapter.diffUtilCategory
import id.co.binar.secondhand.ui.dashboard.home.adapter.diffUtillProduct
import id.co.binar.secondhand.ui.dashboard.home.dialog.HomeCategoryFragment
import id.co.binar.secondhand.ui.dashboard.home.dialog.HomeSearchFragment
import id.co.binar.secondhand.ui.dashboard.home.dialog.TAG_CATEGORY_HOME_DIALOG
import id.co.binar.secondhand.ui.dashboard.home.dialog.TAG_SEARCH_HOME_DIALOG
import id.co.binar.secondhand.ui.product.ARGS_PASSING_SEE_DETAIL
import id.co.binar.secondhand.ui.product.ProductActivity
import id.co.binar.secondhand.util.Constant.ARRAY_STATUS
import id.co.binar.secondhand.util.ItemDecoration
import id.co.binar.secondhand.util.ListAdapterBuilder
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.castFromLocalToRemote
import id.co.binar.secondhand.util.convertRupiah
import id.co.binar.secondhand.util.onSnackError
import id.co.binar.secondhand.util.toNameOnly
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val adapterProduct = ListAdapterBuilder(
        diffCallback = diffUtillProduct, inflater = ListItemProductHomeBinding::inflate
    )
    private val adapterCategory = ListAdapterBuilder(
        diffCallback = diffUtilCategory, inflater = ListItemCategoryHomeBinding::inflate
    )
    private val adapterBanner = ListAdapterBuilder(
        diffCallback = diffUtilBanner, inflater = ListItemBannerHomeBinding::inflate
    )
    private val sliderHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        setupSearch()
        setupBannerViewPager()
        setupBannerAdapter()
        setupCategoryRecyclerView()
        setupCategoryAdapter()
        setupProductRecyclerView()
        setupProductAdapter()
    }

    private fun setupSearch() {
        binding.etInputSearch.setOnClickListener {
            val dialog = HomeSearchFragment()
            dialog.show(parentFragmentManager, TAG_SEARCH_HOME_DIALOG)
        }
    }

    private fun setupBannerViewPager() {
        binding.vpBanner.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val compositeTransformer = CompositePageTransformer()
            compositeTransformer.addTransformer(MarginPageTransformer(21))
            compositeTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }

            setPageTransformer(compositeTransformer)
            registerOnPageChangeCallback(registerOnPageChange)
            adapter = adapterBanner
        }
    }

    private fun setupBannerAdapter() {
        adapterBanner.binder { binding, item, _ ->
            binding.root.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.root))
            }
        }
    }

    private fun setupCategoryRecyclerView() {
        binding.rvCategory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }
    }

    private fun setupCategoryAdapter() {
        adapterCategory.binder { binding, item, position ->
            binding.txtCategory.text = item.name
            val isSearchCategory = position == 0
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (isSearchCategory) R.color.purple_500 else R.color.purple_100
                )
            )
            binding.imgView.setImageResource(
                if (isSearchCategory) R.drawable.ic_round_search_white else R.drawable.ic_round_search_24
            )
            binding.txtCategory.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, if (isSearchCategory) R.color.white else R.color.black
                )
            )
        }
        adapterCategory.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        adapterCategory.onClick { item, _ ->
            val dialog = HomeCategoryFragment.newInstance(item)
            dialog.show(parentFragmentManager, TAG_CATEGORY_HOME_DIALOG)
        }
    }

    private fun setupProductRecyclerView() {
        binding.rvProduct.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(ItemDecoration(requireContext(), 2, 16))
            isNestedScrollingEnabled = false
        }
    }

    private fun setupProductAdapter() {
        adapterProduct.binder { binding, item, _ ->
            binding.ivImageProduct.load(item.imageUrl) {
                placeholder(R.color.purple_100)
                error(R.color.purple_100)
                size(ViewSizeResolver(binding.ivImageProduct))
            }
            binding.imgSold.isVisible = item.status == ARRAY_STATUS[5]
            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (item.status == ARRAY_STATUS[5]) R.color.neutral_2 else R.color.white
                )
            )
            binding.tvNamaProduct.text = item.name
            binding.tvHargaProduct.text = item.basePrice?.convertRupiah()
            binding.tvJenisProduct.text = item.categories?.toNameOnly()
        }
        adapterProduct.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        adapterProduct.onClick { item, _ ->
            val intent = Intent(requireContext(), ProductActivity::class.java)
            intent.putExtra(ARGS_PASSING_SEE_DETAIL, item.id)
            requireActivity().startActivity(intent)
        }
    }

    private fun bindObserver() {
        lifecycleScope.launch {
            viewModel.getCategory.collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val list = mutableListOf<GetCategoryResponse>()
                        list.apply {
                            add(GetCategoryResponse(name = "20 Produk Terbaru"))
                            addAll(it.data.castFromLocalToRemote())
                        }
                        adapterCategory.submitList(list)
                        binding.rvCategory.adapter = adapterCategory
                    }
                    is Resource.Error -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getBanner.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.apply {
                            progressBarBanner.isVisible = true
                            vpBanner.visibility = View.INVISIBLE
                        }
                    }

                    is Resource.Success -> {
                        binding.apply {
                            progressBarBanner.isVisible = false
                            vpBanner.visibility = View.VISIBLE
                            adapterBanner.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.apply {
                            progressBarBanner.isVisible = false
                            vpBanner.visibility = View.VISIBLE
                            requireContext().onSnackError(
                                binding.root, it.error?.message.toString()
                            )
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getProduct.collect {
                when (it) {
                    is Resource.Success -> {
                        binding.progressBar.isVisible = false
                        if (it.data.isNullOrEmpty()) {
                            binding.layoutEmpty.isVisible = true
                            binding.layoutError.isVisible = false
                            binding.rvProduct.isVisible = false
                        } else {
                            binding.layoutEmpty.isVisible = false
                            binding.layoutError.isVisible = false
                            binding.rvProduct.isVisible = true
                            adapterProduct.submitList(it.data)
                            binding.rvProduct.adapter = adapterProduct
                        }
                    }

                    is Resource.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.layoutEmpty.isVisible = false
                        binding.layoutError.isVisible = false
                        binding.rvProduct.isVisible = false
                    }

                    is Resource.Error -> {
                        binding.progressBar.isVisible = false
                        binding.layoutEmpty.isVisible = false
                        binding.layoutError.isVisible = true
                        binding.rvProduct.isVisible = false
                        binding.textView8.text = it.error?.message.toString()
                        requireContext().onSnackError(binding.root, it.error?.message.toString())
                    }
                }
            }
        }
    }

    private val sliderRunnable = Runnable {
        binding.apply {
            if (vpBanner.currentItem == adapterBanner.itemCount - 1) {
                vpBanner.currentItem = 0
            } else {
                binding.vpBanner.currentItem = binding.vpBanner.currentItem + 1
            }
        }
    }

    private val registerOnPageChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            sliderHandler.removeCallbacks(sliderRunnable)
            sliderHandler.postDelayed(sliderRunnable, 4500)
        }
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 4500)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}