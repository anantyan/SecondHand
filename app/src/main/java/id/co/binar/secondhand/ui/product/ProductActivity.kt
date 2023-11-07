package id.co.binar.secondhand.ui.product

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import coil.load
import coil.size.ViewSizeResolver
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ActivityProductBinding
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.model.seller.product.AddProductRequest
import id.co.binar.secondhand.model.seller.product.UpdateProductByIdRequest
import id.co.binar.secondhand.ui.product.dialog.ProductBidingFragment
import id.co.binar.secondhand.ui.product.dialog.TAG_BIDING_PRODUCT_DIALOG
import id.co.binar.secondhand.util.*
import id.co.binar.secondhand.util.Constant.ARRAY_STATUS

const val ARGS_PASSING_PREVIEW = "PREVIEW"
const val ARGS_PASSING_SEE_DETAIL = "SEE_DETAIL"

@AndroidEntryPoint
class ProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductBinding
    private val viewModel by viewModels<ProductViewModel>()
    private var item = GetProductResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_white)

        if (intent.hasExtra(ARGS_PASSING_SEE_DETAIL)) {
            viewModel.getProductById(intent.getIntExtra(ARGS_PASSING_SEE_DETAIL, 0))
            binding.apply {
                btnReload.setOnClickListener {
                    viewModel.getProductById(intent.getIntExtra(ARGS_PASSING_SEE_DETAIL, 0))
                }
                btnTerbitkan.text = "Di Nego Say"
                btnTerbitkan.setOnClickListener {
                    viewModel.passToBiding(item)
                    val dialog = ProductBidingFragment()
                    dialog.show(supportFragmentManager, TAG_BIDING_PRODUCT_DIALOG)
                }
            }
        }
        if (intent.hasExtra(ARGS_PASSING_PREVIEW)) {
            val item = intent.getParcelableExtra<GetProductResponse>(ARGS_PASSING_PREVIEW)
            viewModel.getAccount.observe(this) {
                binding.apply {
                    viewModel.getProductPreview().observe(this@ProductActivity) {
                        ivImageSeller18.setImageBitmap(it.imageUrl)
                    }

                    tvProductSeller18.text = item?.name
                    tvKotaPenjual.text = item?.location
                    tvHargaSeller18.text = item?.basePrice?.convertRupiah()
                    tvIsiDeskripsi.text = item?.description
                    tvJenisSeller18.text = item?.categories?.toNameOnly()

                    imageView.load(it.data?.imageUrl)
                    tvNamaPenjual.text = it.data?.fullName
                    tvKotaPenjual.text = it.data?.city

                    btnTerbitkan.setOnClickListener {
                        viewModel.getProductPreview().observe(this@ProductActivity) {
                            if (item?.id != null) {
                                viewModel.editProduct(
                                    item.id,
                                    UpdateProductByIdRequest(
                                        name = item.name,
                                        basePrice = MoneyTextWatcher.parseCurrencyValue(item.basePrice.toString()).toLong(),
                                        categoryIds = item.categories?.toIntOnly(),
                                        location = item.location,
                                        description = item.description
                                    ),
                                    this@ProductActivity.buildImageMultipart("image", it.imageUrl!!)
                                )
                            } else {
                                viewModel.addProduct(
                                    AddProductRequest(
                                        name = item?.name,
                                        basePrice = MoneyTextWatcher.parseCurrencyValue(item?.basePrice.toString()).toLong(),
                                        categoryIds = item?.categories?.toIntOnly(),
                                        location = item?.location,
                                        description = item?.description
                                    ),
                                    this@ProductActivity.buildImageMultipart("image", it.imageUrl!!)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bindObserver() {
        viewModel.sendNotif.observe(this) {
            when (it) {
                is Resource.Success -> { }
                is Resource.Loading -> { }
                is Resource.Error -> { }
            }
        }

        viewModel.editProduct.observe(this) {
            when (it) {
                is Resource.Success -> {
                    this.onToast("${it.data?.name} berhasil diupdate")
                    onBackPressed()
                }
                is Resource.Loading -> this.onToast("Mohon menunggu...")
                is Resource.Error -> this.onSnackError(binding.root, it.error?.message.toString())
            }
        }

        viewModel.addProduct.observe(this) {
            when (it) {
                is Resource.Success -> {
                    this.onToast("${it.data?.name} berhasil ditambahkan")
                    onBackPressed()
                }
                is Resource.Loading -> this.onToast("Mohon menunggu...")
                is Resource.Error -> this.onSnackError(binding.root, it.error?.message.toString())
            }
        }

        viewModel.newOrder.observe(this) {
            when (it) {
                is Resource.Success -> {
                    viewModel.sendNotif(item, it.data)
                    this.onSnackSuccess(
                        binding.root,
                        "Hore, harga tawaranmu berhasil dikirim ke penjual"
                    )
                }
                is Resource.Loading -> this.onToast("Mohon menunggu...")
                is Resource.Error -> this.onSnackError(binding.root, it.error?.message.toString())
            }
        }

        viewModel.getProductById.observe(this) {
            when (it) {
                is Resource.Success -> {

                    /** menyiapkan data untuk passing ke dialog biding harga */
                    it.data?.let {
                        item = it
                    }

                    binding.apply {
                        progressBar.isVisible = false
                        btnTerbitkan.isEnabled = true
                        scrollView3.isVisible = true
                        layoutError.isVisible = false
                        tvProductSeller18.text = it.data?.name
                        tvJenisSeller18.text = it.data?.categories?.toNameOnly()
                        tvHargaSeller18.text = it.data?.basePrice?.convertRupiah()
                        tvIsiDeskripsi.text = it.data?.description
                        ivImageSeller18.load(it.data?.imageUrl) {
                            placeholder(R.color.purple_100)
                            error(R.color.purple_100)
                            size(ViewSizeResolver(binding.ivImageSeller18))
                        }
                        imageView.load(it.data?.user?.imageUrl) {
                            placeholder(R.color.purple_100)
                            error(R.color.purple_100)
                            size(ViewSizeResolver(binding.imageView))
                        }
                        if (it.data?.status == ARRAY_STATUS[5]) {
                            btnTerbitkan.text = "Sold Out"
                            btnTerbitkan.isEnabled = false
                        } else {
                            btnTerbitkan.text = "Di Nego Say"
                        }
                        tvNamaPenjual.text = it.data?.user?.fullName
                        tvKotaPenjual.text = it.data?.user?.city
                    }
                }
                is Resource.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        btnTerbitkan.isEnabled = false
                        scrollView3.isVisible = false
                        layoutError.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.apply {
                        progressBar.isVisible = false
                        btnTerbitkan.isEnabled = false
                        scrollView3.isVisible = false
                        layoutError.isVisible = true
                        textView8.text = it.error?.message.toString()
                        this@ProductActivity.onSnackError(root, it.error?.message.toString())
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}