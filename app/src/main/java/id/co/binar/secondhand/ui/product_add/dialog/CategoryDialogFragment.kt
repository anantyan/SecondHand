package id.co.binar.secondhand.ui.product_add.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.databinding.BottomSheetCategoryProductAddBinding
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import id.co.binar.secondhand.ui.product_add.ProductAddViewModel
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.castFromLocalToRemote
import id.co.binar.secondhand.util.onToast

const val TAG_CATEGORY_DIALOG = "CATEGORY_DIALOG"

@AndroidEntryPoint
class CategoryDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCategoryProductAddBinding? = null
    private var chooseList = mutableListOf<GetCategoryResponse>()
    private val binding get() = _binding!!
    private val adapter = CategoryDialogAdapter()
    private val viewModel by activityViewModels<ProductAddViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCategoryProductAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.categoryProduct.observe(viewLifecycleOwner) {
            adapter.asyncDiffer.submitList(it.data.castFromLocalToRemote())
            binding.list.adapter = adapter
            viewModel.list.observe(viewLifecycleOwner) {
                chooseList = it
            }
            viewModel.lastList.observe(viewLifecycleOwner) {
                adapter.asyncDiffer.submitList(it)
            }
            when(it) {
                is Resource.Success -> {}
                is Resource.Loading -> {}
                is Resource.Error -> {
                    requireContext().onToast(it.error?.message.toString())
                }
            }
        }
    }

    private fun bindView() {
        adapter.onClickAdapter { _, GetCategoryResponse ->
            if (GetCategoryResponse.check == true) {
                chooseList.add(GetCategoryResponse)
            } else {
                chooseList.remove(GetCategoryResponse)
            }
        }
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.list.setHasFixedSize(true)
        binding.list.itemAnimator = DefaultItemAnimator()
        binding.list.layoutManager = LinearLayoutManager(context)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.list(chooseList)
        viewModel.lastList(adapter.asyncDiffer.currentList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
