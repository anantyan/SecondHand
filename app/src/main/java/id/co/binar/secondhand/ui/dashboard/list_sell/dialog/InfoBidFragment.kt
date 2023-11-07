package id.co.binar.secondhand.ui.dashboard.list_sell.dialog

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.BottomSheetInfoBidSellerBinding
import id.co.binar.secondhand.model.seller.order.UpdateOrderRequest
import id.co.binar.secondhand.util.*
import id.co.binar.secondhand.util.Constant.ARRAY_STATUS
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val TAG_INFO_BID_DIALOG = "INFO_BID_DIALOG"

@AndroidEntryPoint
class InfoBidFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetInfoBidSellerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<InfoBidViewModel>()

    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int): InfoBidFragment {
            return InfoBidFragment().apply {
                arguments = Bundle().apply {
                    putInt(TAG_INFO_BID_DIALOG, sectionNumber)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.args(arguments?.getInt(TAG_INFO_BID_DIALOG) ?: -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetInfoBidSellerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.apply {
            btnTerima.setOnClickListener {
                if (viewModel.getOrderById.value?.data?.status == ARRAY_STATUS[3]) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://wa.me/62${viewModel.getOrderById.value?.data?.user?.phoneNumber}")
                    startActivity(intent)
                } else {
                    val id = viewModel.getOrderById.value?.data?.id
                    val id1 = viewModel.getOrderById.value?.data?.productId
                    val field = UpdateOrderRequest(status = ARRAY_STATUS[3])
                    val field1 = UpdateOrderRequest(status = ARRAY_STATUS[1])
                    viewModel.updateOrder(id ?: -1, field)
                    viewModel.updateProduct(id1 ?: -1, field1)
                }
            }

            btnTolak.setOnClickListener {
                if (viewModel.getOrderById.value?.data?.status == ARRAY_STATUS[3]) {
                    val id = viewModel.getOrderById.value?.data?.id
                    val id1 = viewModel.getOrderById.value?.data?.id
                    val field = UpdateOrderRequest(status = ARRAY_STATUS[2])
                    val field1 = UpdateOrderRequest(status = ARRAY_STATUS[0])
                    viewModel.updateOrder(id ?: -1, field)
                    viewModel.updateProduct(id1 ?: -1, field1)
                } else {
                    val id = viewModel.getOrderById.value?.data?.id
                    val field = UpdateOrderRequest(status = ARRAY_STATUS[4])
                    id?.let {
                        viewModel.updateOrder(it, field)
                    }
                }
            }

            btnReload.setOnClickListener {
                viewModel.getOrderById.value?.data?.id?.let { viewModel.args(it) }
            }
        }
    }

    private fun bindObserver() {
        viewModel.sendNotif.observe(viewLifecycleOwner) { }

        viewModel.response1.observe(viewLifecycleOwner) { }

        viewModel.response.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    when (it.data?.status) {
                        ARRAY_STATUS[2] -> {
                            requireContext().onToast("Produk dibatalkan")
                        }
                        ARRAY_STATUS[3] -> {
                            viewModel.getOrderById.value?.data?.let {
                                val dialog = InfoBidSuccessFragment.newInstance(it)
                                dialog.show(childFragmentManager, TAG_INFO_BID_DIALOG_SUCCESS)
                            }
                        }
                        ARRAY_STATUS[4] -> {
                            viewModel.getOrderById.value?.data?.let {
                                viewModel.sendNotif(it.user?.id.toString(), "Penawaran ditolak! :(", "Maaf atas keinginanmu jangan kecewa coba cari produk yang cocok dengamu, semangat")
                            }
                            requireContext().onToast("Produk ditolak")
                        }
                    }
                }
                is Resource.Loading -> {
                    requireContext().onToast("Mohon Menunggu...")
                }
                is Resource.Error -> {
                    requireContext().onToast(it.error?.message.toString())
                }
            }
        }

        viewModel.getOrderById.observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    binding.apply {
                        progressBar.isVisible = false
                        layoutContent.isVisible = true
                        tvDaftarProduct.isVisible = true
                        layoutContent2.isVisible = true
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = false
                        textView3.text = it.data?.user?.fullName
                        textView4.text = it.data?.user?.city
                        imageView.load(it.data?.user?.imageUrl) {
                            placeholder(R.drawable.ic_profile_image)
                            error(R.drawable.ic_profile_image)
                            size(ViewSizeResolver(imageView))
                        }
                        imgProduct.load(it.data?.product?.imageUrl) {
                            placeholder(R.color.purple_100)
                            error(R.color.purple_100)
                            size(ViewSizeResolver(binding.imgProduct))
                        }
                        val formattedDate = it.data?.createdAt?.let {
                            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm, dd MMM yyy", Locale.ENGLISH)
                            val date = LocalDateTime.parse(it, inputFormatter)
                            outputFormatter.format(date)
                        }
                        val bidPrice = it.data?.price?.let {
                            "Ditawar ${it.convertRupiah()}"
                        }
                        val status = when (it.data?.status) {
                            "accepted" -> {
                                tvInfoHarga.paintFlags = tvInfoHarga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                btnTerima.text = "Whatsapp"
                                btnTolak.text = "Batal"
                                "Penawaran telah diterima"
                            }
                            "declined" -> {
                                tvInfoHarga.paintFlags = tvInfoHarga.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                                btnTolak.isEnabled = false
                                "Penawaran ditolak"
                            }
                            else -> {
                                tvInfoHarga.paintFlags = tvInfoHarga.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                                "Penawaran produk"
                            }
                        }
                        tvNotifTime.text = formattedDate
                        tvNotifProduct.text = status
                        tvNamaProduct.text = it.data?.productName
                        tvInfoHarga.text = it.data?.basePrice?.convertRupiah()
                        tvInfoTawar.text = bidPrice
                    }
                }
                is Resource.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        layoutContent.isVisible = false
                        tvDaftarProduct.isVisible = false
                        layoutContent2.isVisible = false
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.apply {
                        progressBar.isVisible = false
                        layoutContent.isVisible = false
                        tvDaftarProduct.isVisible = false
                        layoutContent2.isVisible = false
                        layoutEmpty.isVisible = false
                        layoutError.isVisible = true
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