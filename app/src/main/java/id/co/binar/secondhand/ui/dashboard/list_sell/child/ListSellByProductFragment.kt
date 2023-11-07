package id.co.binar.secondhand.ui.dashboard.list_sell.child

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.databinding.FragmentListSellByProductBinding
import id.co.binar.secondhand.model.seller.product.GetProductResponse
import id.co.binar.secondhand.ui.dashboard.list_sell.adapter.ListSellProductAdapter
import id.co.binar.secondhand.ui.product_add.ARGS_PRODUCT_EDIT
import id.co.binar.secondhand.ui.product_add.ProductAddActivity
import id.co.binar.secondhand.util.*

@AndroidEntryPoint
class ListSellByProductFragment : Fragment() {

    private var _binding: FragmentListSellByProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ListSellByProductViewModel>()
    private val adapterProduct = ListSellProductAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSellByProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        viewModel.getProduct()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getProduct()
        }

        binding.rvList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(ItemDecoration(requireContext(), 2, 16))
            isNestedScrollingEnabled = false
        }

        adapterProduct.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onClickAdapter { i, GetCategoryResponse ->
                when(i) {
                    0 -> {
                        val intent = Intent(requireContext(), ProductAddActivity::class.java)
                        requireActivity().startActivity(intent)
                    }
                    else -> dialogChooseProduct(GetCategoryResponse)
                }
            }
        }
    }

    private fun bindObserver() {
        viewModel.deleteProduct.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> binding.swipeRefresh.isRefreshing = false
                is Resource.Loading -> binding.swipeRefresh.isRefreshing = true
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    requireContext().onSnackError(binding.root, it.error?.message.toString())
                }
            }
        }

        viewModel.getProduct.observe(viewLifecycleOwner) {
            val list = mutableListOf<GetProductResponse>()
            list.apply {
                add(GetProductResponse())
                addAll(it.data.castFromLocalToRemote())
            }
            adapterProduct.asyncDiffer.submitList(list)
            binding.rvList.adapter = adapterProduct
            when (it) {
                is Resource.Success -> binding.swipeRefresh.isRefreshing = false
                is Resource.Loading -> binding.swipeRefresh.isRefreshing = true
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    requireContext().onSnackError(binding.root, it.error?.message.toString())
                }
            }
        }
    }

    private fun dialogChooseProduct(item: GetProductResponse) {
        val action = arrayOf("Edit Produk","Hapus Produk")
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.apply {
            setTitle("Pilih aksi untuk - ${item.name}")
            setItems(action) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(requireContext(), ProductAddActivity::class.java)
                        intent.putExtra(ARGS_PRODUCT_EDIT, item.id)
                        requireActivity().startActivity(intent)
                    }
                    1 -> dialogRemoveProduct(item)
                }
            }
            show()
        }
    }

    private fun dialogRemoveProduct(item: GetProductResponse) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.apply {
            setTitle("Hapus - ${item.name}")
            setMessage("Apakah anda yakin ingin menghapus product tersebut?")
            setPositiveButton("Iya", null)
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog = builder.show()
        val btnPositif = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositif.setOnClickListener {
            item.id?.let { viewModel.deleteProduct(it) }
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}