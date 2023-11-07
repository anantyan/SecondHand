package id.co.binar.secondhand.ui.product_add

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.data.local.model.SellerProductPreviewLocal
import id.co.binar.secondhand.databinding.ActivityProductAddBinding
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import id.co.binar.secondhand.model.seller.product.AddProductRequest
import id.co.binar.secondhand.model.seller.product.UpdateProductByIdRequest
import id.co.binar.secondhand.ui.login.LoginActivity
import id.co.binar.secondhand.ui.product.ARGS_PASSING_PREVIEW
import id.co.binar.secondhand.ui.product.ProductActivity
import id.co.binar.secondhand.ui.product_add.dialog.CategoryDialogFragment
import id.co.binar.secondhand.ui.product_add.dialog.TAG_CATEGORY_DIALOG
import id.co.binar.secondhand.util.*
import id.co.binar.secondhand.util.Constant.ARRAY_PERMISSION
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.constant.Mode
import io.github.anderscheow.validator.validator
import kotlinx.coroutines.launch

const val ARGS_PRODUCT_EDIT = "EDIT_PRODUCT"

@AndroidEntryPoint
class ProductAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductAddBinding
    private val viewModel by viewModels<ProductAddViewModel>()
    private var chooseList = mutableListOf<GetCategoryResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Lengkapi Detail Produk"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (viewModel.getTokenId.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        if (intent.hasExtra(ARGS_PRODUCT_EDIT)) {
            viewModel.getIdProduct(intent.getIntExtra(ARGS_PRODUCT_EDIT, -1))
        }

        viewModel.editProduct.observe(this) {
            when (it) {
                is Resource.Success -> {
                    this.onToast("${it.data?.name} berhasil diupdate")
                    onBackPressed()
                }
                is Resource.Loading -> {
                    this.onToast("Mohon menunggu...")
                }
                is Resource.Error -> {
                    this.onSnackError(binding.root, it.error?.message.toString())
                }
            }
        }

        viewModel.getProductById.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.categories?.forEach {
                        chooseList.add(
                            GetCategoryResponse(
                                name = it.name,
                                id = it.id,
                                check = true
                            )
                        )
                    }
                    viewModel.categoryProduct.observe(this) {
                        val lastList = mutableListOf<GetCategoryResponse>()
                        lastList.addAll(chooseList)
                        lastList.addAll(it.data.castFromLocalToRemote())
                        lastList.sortBy { it.id }
                        viewModel.lastList(lastList.distinctBy { it.id }.toMutableList())
                    }
                    binding.apply {
                        lifecycleScope.launch {
                            val bitmap = this@ProductAddActivity.bitmap(it.data?.imageUrl)
                            viewModel.bitmap(bitmap)
                        }
                        txtInputLayoutTitle.setText(it.data?.name)
                        txtInputLayoutPrice.setText(it.data?.basePrice.toString())
                        txtInputLocation.setText(it.data?.location)
                        txtInputLayoutDescription.setText(it.data?.description)
                        txtInputLayoutCategory.setText(it.data?.categories?.toNameOnly())
                    }
                }
                is Resource.Loading -> this.onToast("Mohon menunggu...")
                is Resource.Error -> this.onSnackError(binding.root, it.error?.message.toString())
            }
        }

        viewModel.bitmap.observe(this) {
            it?.let { bmp ->
                binding.imgView.setImageBitmap(bmp)
            }
        }
        viewModel.list.observe(this) {
            binding.txtInputLayoutCategory.setText(it.toNameOnly())
            chooseList = it
        }
        viewModel.addProduct.observe(this) {
            when (it) {
                is Resource.Success -> {
                    this.onToast("${it.data?.name} berhasil ditambahkan")
                    onBackPressed()
                }
                is Resource.Loading -> {
                    this.onToast("Mohon menunggu...")
                }
                is Resource.Error -> {
                    this.onSnackError(binding.root, it.error?.message.toString())
                }
            }
        }
    }

    private fun bindView() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)

        binding.imgView.setOnClickListener {
            requestPermission.launch(ARRAY_PERMISSION)
        }

        binding.btnSave.setOnClickListener {
            onValidate()
        }

        binding.btnPreview.setOnClickListener {
            onValidatePriview()
        }

        binding.txtInputLayoutCategory.setOnClickListener {
            val dialog = CategoryDialogFragment()
            dialog.show(supportFragmentManager, TAG_CATEGORY_DIALOG)
            viewModel.list(chooseList)
        }

        binding.txtInputLayoutPrice.addTextChangedListener(MoneyTextWatcher(binding.txtInputLayoutPrice))
    }

    private val choosePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val resultCode = it.resultCode
        val data = it.data
        try {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        lifecycleScope.launch {
                            val bitmap = this@ProductAddActivity.bitmap(it.data)
                            viewModel.bitmap(bitmap)
                        }
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    throw Exception(ImagePicker.getError(data))
                }
            }
        } catch (ex: Exception) {
            this@ProductAddActivity.onToast(ex.message.toString())
        }
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.entries.forEach {
            if (it.key == Manifest.permission.CAMERA) {
                if (it.value) {
                    choosePhotoPermission()
                } else {
                    SettingsDialog.Builder(this).build().show()
                }
            }
        }
    }

    private fun choosePhotoPermission() {
        ImagePicker.with(this)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .cropSquare()
            .compress(2048)
            .maxResultSize(2048, 2048)
            .createIntent {
                choosePhoto.launch(it)
            }
    }

    private fun onValidate() {
        validator(this) {
            mode = Mode.SINGLE
            listener = onProductSubmit
            validate(
                generalValid(binding.etTitleProduct),
                priceValid(binding.etPriceProduct),
                generalValid(binding.etCategoriProduct),
                generalValid(binding.etInputLayoutLocation),
                generalValid(binding.etDescription)
            )
        }
    }

    private fun onValidatePriview() {
        validator(this) {
            mode = Mode.SINGLE
            listener = onProductPreview
            validate(
                generalValid(binding.etTitleProduct),
                priceValid(binding.etPriceProduct),
                generalValid(binding.etCategoriProduct),
                generalValid(binding.etInputLayoutLocation),
                generalValid(binding.etDescription)
            )
        }
    }

    private val onProductPreview = object : Validator.OnValidateListener {
        override fun onValidateSuccess(values: List<String>) {
            val bitmap = viewModel.bitmap.value
            if (bitmap == null) {
                this@ProductAddActivity.onSnackError(binding.root, "File gambar tidak boleh kosong!")
            } else {
                val item = GetProductResponse(
                    id = viewModel.getProductById.value?.data?.id,
                    name = binding.txtInputLayoutTitle.text.toString(),
                    basePrice = MoneyTextWatcher.parseCurrencyValue(binding.txtInputLayoutPrice.text.toString()).toLong(),
                    categories = chooseList,
                    location = binding.txtInputLocation.text.toString(),
                    description = binding.txtInputLayoutDescription.text.toString()
                )
                val itemPreview = SellerProductPreviewLocal(
                    id = viewModel.getProductById.value?.data?.id,
                    imageUrl = bitmap
                )
                viewModel.setProductPreview(itemPreview)
                val intent = Intent(this@ProductAddActivity, ProductActivity::class.java)
                intent.putExtra(ARGS_PASSING_PREVIEW, item)
                startActivity(intent)
            }
        }

        override fun onValidateFailed(errors: List<String>) {}
    }

    private val onProductSubmit = object : Validator.OnValidateListener {
        override fun onValidateSuccess(values: List<String>) {
            val bitmap = viewModel.bitmap.value
            if (bitmap == null) {
                this@ProductAddActivity.onSnackError(binding.root, "File gambar tidak boleh kosong!")
            } else {
                if (intent.hasExtra(ARGS_PRODUCT_EDIT)) {
                    viewModel.editProduct(
                        viewModel.getProductById.value?.data?.id!!,
                        UpdateProductByIdRequest(
                            name = binding.txtInputLayoutTitle.text.toString(),
                            basePrice = MoneyTextWatcher.parseCurrencyValue(binding.txtInputLayoutPrice.text.toString()).toLong(),
                            categoryIds = chooseList.toIntOnly(),
                            location = binding.txtInputLocation.text.toString(),
                            description = binding.txtInputLayoutDescription.text.toString()
                        ),
                        this@ProductAddActivity.buildImageMultipart("image", bitmap)
                    )
                } else {
                    viewModel.addProduct(
                        AddProductRequest(
                            name = binding.txtInputLayoutTitle.text.toString(),
                            basePrice = MoneyTextWatcher.parseCurrencyValue(binding.txtInputLayoutPrice.text.toString()).toLong(),
                            categoryIds = chooseList.toIntOnly(),
                            location = binding.txtInputLocation.text.toString(),
                            description = binding.txtInputLayoutDescription.text.toString()
                        ),
                        this@ProductAddActivity.buildImageMultipart("image", bitmap)
                    )
                }
            }
        }

        override fun onValidateFailed(errors: List<String>) {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.deleteProductPreview()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}