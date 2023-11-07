package id.co.binar.secondhand.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ActivityLoginBinding
import id.co.binar.secondhand.model.auth.GetAuthRequest
import id.co.binar.secondhand.ui.register.RegisterActivity
import id.co.binar.secondhand.util.*
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.constant.Mode
import io.github.anderscheow.validator.validator

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.login.observe(this) {
            when(it) {
                is Resource.Success -> {
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
        binding.apply {
            toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            btnMasuk.setOnClickListener {
                onValidate()
            }
            tvDaftarDiSini.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun onValidate() {
        validator(this) {
            mode = Mode.SINGLE
            listener = onSignIn
            validate(
                emailValid(binding.etEmail),
                passwordValid(binding.etPassword)
            )
        }
    }

    private val onSignIn = object : Validator.OnValidateListener {
        override fun onValidateSuccess(values: List<String>) {
            viewModel.login(
                GetAuthRequest(
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