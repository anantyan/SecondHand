package id.co.binar.secondhand.ui.profile

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ActivityProfileBinding
import id.co.binar.secondhand.model.auth.UpdateAuthByTokenRequest
import id.co.binar.secondhand.ui.login.LoginActivity
import id.co.binar.secondhand.util.*
import id.co.binar.secondhand.util.Constant.ARRAY_PERMISSION
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.constant.Mode
import io.github.anderscheow.validator.validator
import kotlinx.coroutines.launch

const val PASSING_FROM_ACCOUNT_TO_PROFILE = "PASSING_FROM_ACCOUNT_TO_PROFILE"

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Lengkapi Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.getAccount()

        viewModel.updateAccount.observe(this) {
            when(it) {
                is Resource.Success -> {
                    this.onToast("Data berhasil diupdate")
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
        viewModel.bitmap.observe(this) {
            it?.let { bmp ->
                binding.ivImageProfile.setImageBitmap(bmp)
            }
        }
    }

    private fun bindView() {
        if (viewModel.getTokenId.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (intent.hasExtra(PASSING_FROM_ACCOUNT_TO_PROFILE)) {
            intent.extras?.getBoolean(PASSING_FROM_ACCOUNT_TO_PROFILE)?.let {
                if (it) {
                    binding.txtInputLayoutEmail.visibility = View.VISIBLE
                    binding.txtInputLayoutPassword.visibility = View.VISIBLE
                    viewModel.getAccount.observe(this) {
                        binding.apply {
                            lifecycleScope.launch {
                                it.data?.imageUrl?.let {
                                    val bitmap = this@ProfileActivity.bitmap(it)
                                    viewModel.bitmap(bitmap)
                                }
                            }
                            txtInputEmail.setText(it.data?.email)
                            txtInputLayoutNama.setText(it.data?.fullName)
                            txtInputLayoutKota.setText(it.data?.city)
                            txtInputLayoutAlamat.setText(it.data?.address)
                            txtInputLayoutNoHandphone.setText(it.data?.phoneNumber.toString())
                        }
                        when (it) {
                            is Resource.Success -> {}
                            is Resource.Loading -> {}
                            is Resource.Error -> {
                                this.onSnackError(binding.root, it.error?.message.toString())
                            }
                        }
                    }
                }
            }
        }

        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.ivImageProfile.setOnClickListener {
            requestPermission.launch(ARRAY_PERMISSION)
        }
        binding.btnDaftar.setOnClickListener {
            onValidate()
        }
    }

    private val choosePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val resultCode = it.resultCode
        val data = it.data
        try {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        lifecycleScope.launch {
                            val bitmap = this@ProfileActivity.bitmap(it.data)
                            viewModel.bitmap(bitmap)
                        }
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    throw Exception(ImagePicker.getError(data))
                }
            }
        } catch (ex: Exception) {
            this@ProfileActivity.onToast(ex.message.toString())
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
        if (intent.hasExtra(PASSING_FROM_ACCOUNT_TO_PROFILE)) {
            validator(this) {
                mode = Mode.SINGLE
                listener = onProfileSubmit
                validate(
                    emailValid(binding.txtInputLayoutEmail),
                    passwordValid(binding.txtInputLayoutPassword),
                    generalValid(binding.etNamaProfile),
                    generalValid(binding.etKotaProfile),
                    generalValid(binding.etAlamatProfile),
                    phoneValid(binding.etNoHandphoneProfile)
                )
            }
        }
    }

    private val onProfileSubmit = object : Validator.OnValidateListener {
        override fun onValidateSuccess(values: List<String>) {
            val bitmap = viewModel.bitmap.value
            if (bitmap == null) {
                this@ProfileActivity.onSnackError(binding.root, "File gambar tidak boleh kosong!")
            } else {
                if (intent.hasExtra(PASSING_FROM_ACCOUNT_TO_PROFILE)) {
                    viewModel.updateAccount(
                        UpdateAuthByTokenRequest(
                            password = binding.txtInputPassword.text.toString(),
                            email = binding.txtInputEmail.text.toString(),
                            fullName = binding.txtInputLayoutNama.text.toString(),
                            phoneNumber = binding.txtInputLayoutNoHandphone.text.toString().toLong(),
                            address = binding.txtInputLayoutAlamat.text.toString(),
                            city = binding.txtInputLayoutKota.text.toString()
                        ),
                        this@ProfileActivity.buildImageMultipart("image", bitmap)
                    )
                } else {
                    this@ProfileActivity.onSnackError(binding.root, "Tidak valid untuk memasukan data")
                }
            }
        }

        override fun onValidateFailed(errors: List<String>) {}
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}