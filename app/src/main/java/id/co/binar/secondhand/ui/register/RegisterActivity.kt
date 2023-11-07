package id.co.binar.secondhand.ui.register

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ActivityRegisterBinding
import id.co.binar.secondhand.model.auth.AddAuthRequest
import id.co.binar.secondhand.util.*
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.constant.Mode
import io.github.anderscheow.validator.validator

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.register.observe(this) {
            when(it) {
                is Resource.Success -> {
                    this.onToast("Data berhasil disimpan")
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
        binding.btnDaftar.setOnClickListener {
            onValidate()
        }
        binding.tvMasukDiSini.setOnClickListener {
            onBackPressed()
        }
    }

    private fun onValidate() {
        validator(this) {
            mode = Mode.SINGLE
            listener = onSignUp
            validate(
                generalValid(binding.etNama),
                emailValid(binding.etEmail),
                passwordValid(binding.etPassword)
            )
        }
    }

    private val onSignUp= object : Validator.OnValidateListener {
        override fun onValidateSuccess(values: List<String>) {
            viewModel.register(
                AddAuthRequest(
                    fullName = binding.txtInputLayoutNama.text.toString(),
                    email = binding.txtInputLayoutEmail.text.toString(),
                    password = binding.txtInputLayoutPassword.text.toString()
                )
            )
        }

        override fun onValidateFailed(errors: List<String>) {}
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}