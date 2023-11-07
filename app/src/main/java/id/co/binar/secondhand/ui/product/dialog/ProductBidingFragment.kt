package id.co.binar.secondhand.ui.product.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.ViewSizeResolver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.BottomSheetBidingProductBinding
import id.co.binar.secondhand.model.buyer.order.AddOrderRequest
import id.co.binar.secondhand.ui.product.ProductViewModel
import id.co.binar.secondhand.util.MoneyTextWatcher
import id.co.binar.secondhand.util.convertRupiah
import id.co.binar.secondhand.util.priceValid
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.constant.Mode
import io.github.anderscheow.validator.validator

const val TAG_BIDING_PRODUCT_DIALOG = "BIDING_PRODUCT_DIALOG"

@AndroidEntryPoint
class ProductBidingFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBidingProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<ProductViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBidingProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.passToBiding.observe(viewLifecycleOwner) {
            binding.apply {
                imgView.load(it.imageUrl) {
                    crossfade(true)
                    placeholder(R.color.purple_100)
                    error(R.color.purple_100)
                    size(ViewSizeResolver(binding.imgView))
                }
                txtNamaBarang.text = it.name
                txtHargaBarang.text = it.basePrice?.convertRupiah()
            }
        }
    }

    private fun bindView() {
        binding.apply {
            txtInputBidHarga.addTextChangedListener(MoneyTextWatcher(binding.txtInputBidHarga))

            btnSubmit.setOnClickListener {
                onValidate()
            }
        }
    }

    private fun onValidate() {
        validator(requireContext()) {
            mode = Mode.SINGLE
            listener = onProductSubmit
            validate(
                priceValid(binding.etLayoutBidHarga)
            )
        }
    }

    private val onProductSubmit = object : Validator.OnValidateListener {
        override fun onValidateSuccess(values: List<String>) {
            viewModel.getProductId.observe(viewLifecycleOwner) {
                val item = AddOrderRequest(
                    it,
                    MoneyTextWatcher.parseCurrencyValue(binding.txtInputBidHarga.text.toString()).toLong()
                )
                viewModel.newOrder(item)
                dialog?.dismiss()
            }
        }

        override fun onValidateFailed(errors: List<String>) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}