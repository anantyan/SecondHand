package id.co.binar.secondhand.ui.dashboard.list_sell.dialog

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.BottomSheetInfoBidSuccessSellerBinding
import id.co.binar.secondhand.model.seller.order.GetOrderResponse
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.convertRupiah
import java.util.*

const val TAG_INFO_BID_DIALOG_SUCCESS = "INFO_BID_DIALOG_SUCCESS"

@AndroidEntryPoint
class InfoBidSuccessFragment : BottomSheetDialogFragment() {

    private var _binding : BottomSheetInfoBidSuccessSellerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<InfoBidSuccessViewModel>()

    companion object {
        @JvmStatic
        fun newInstance(item: GetOrderResponse): InfoBidSuccessFragment {
            return InfoBidSuccessFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TAG_INFO_BID_DIALOG_SUCCESS, item)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.args(arguments?.getParcelable(TAG_INFO_BID_DIALOG_SUCCESS) ?: GetOrderResponse())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetInfoBidSuccessSellerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.sendNotif.observe(viewLifecycleOwner) { }

        viewModel.args.observe(viewLifecycleOwner) {
            viewModel.sendNotif(it.user?.id.toString(), "Selamat! Penawaran diterima", "Segera bayar produk kamu untuk memenuhi keinginanmu")
            binding.apply {
                textView3.text = it.user?.fullName
                textView4.text = it.user?.city
                imageView.load(it.user?.imageUrl) {
                    placeholder(R.drawable.ic_profile_image)
                    error(R.drawable.ic_profile_image)
                    size(ViewSizeResolver(imageView))
                }
                imgProduct.load(it.product?.imageUrl) {
                    placeholder(R.color.purple_100)
                    error(R.color.purple_100)
                    size(ViewSizeResolver(imgProduct))
                }
                val bidPrice = it.price?.let {
                    "Ditawar ${it.convertRupiah()}"
                }
                tvNamaProduct.text = it.productName
                tvInfoHarga.text = it.basePrice?.convertRupiah()
                tvInfoHarga.paintFlags = binding.tvInfoHarga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvInfoTawar.text = bidPrice
            }
        }
    }

    private fun bindView() {
        binding.apply {
            btnWa.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://wa.me/62${viewModel.args.value?.user?.phoneNumber}")
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}