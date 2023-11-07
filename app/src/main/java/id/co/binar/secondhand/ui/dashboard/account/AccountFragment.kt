package id.co.binar.secondhand.ui.dashboard.account

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.BuildConfig
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.FragmentAccountBinding
import id.co.binar.secondhand.ui.login.LoginActivity
import id.co.binar.secondhand.ui.profile.PASSING_FROM_ACCOUNT_TO_PROFILE
import id.co.binar.secondhand.ui.profile.ProfileActivity
import id.co.binar.secondhand.util.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.tvEditAccount.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra(PASSING_FROM_ACCOUNT_TO_PROFILE, true)
            startActivity(intent)
        }

        binding.tvLogoutAccount.setOnClickListener {
            dialogLogout {
                viewModel.logout()
                it.dismiss()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnMasuk.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvVersionAccount.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    private fun bindObserver() {
        viewModel.getAccount().observe(viewLifecycleOwner) {
            binding.ivImageAccount.load(it.data?.imageUrl.toString()) {
                placeholder(R.drawable.ic_profile_image)
                error(R.drawable.ic_profile_image)
                size(ViewSizeResolver(binding.ivImageAccount))
            }
            when (it) {
                is Resource.Success -> {}
                is Resource.Loading -> {}
                is Resource.Error -> {
                    requireContext().onSnackError(binding.root, it.error?.message.toString())
                }
            }
        }

        lifecycleScope.launch {
            requireContext().dataStore.getValue(TOKEN_ID, "").collectLatest {
                if (it.isEmpty()) {
                    binding.apply {
                        ivEditAccount.visibility = View.INVISIBLE
                        ivLogoutAccount.visibility = View.INVISIBLE
                        ivOptionAccount.visibility = View.INVISIBLE
                        tvEditAccount.visibility = View.INVISIBLE
                        tvLogoutAccount.visibility = View.INVISIBLE
                        tvOptionAccount.visibility = View.INVISIBLE
                        viewEdit.visibility = View.INVISIBLE
                        viewLogout.visibility = View.INVISIBLE
                        viewOption.visibility = View.INVISIBLE
                        btnMasuk.visibility = View.VISIBLE
                    }
                } else {
                    binding.apply {
                        btnMasuk.visibility = View.INVISIBLE
                        ivEditAccount.visibility = View.VISIBLE
                        ivLogoutAccount.visibility = View.VISIBLE
                        ivOptionAccount.visibility = View.VISIBLE
                        tvEditAccount.visibility = View.VISIBLE
                        tvLogoutAccount.visibility = View.VISIBLE
                        tvOptionAccount.visibility = View.VISIBLE
                        viewEdit.visibility = View.VISIBLE
                        viewLogout.visibility = View.VISIBLE
                        viewOption.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun dialogLogout(listener: (dialog: AlertDialog) -> Unit) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.apply {
            setCancelable(false)
            setTitle("Attention!")
            setMessage("Apakah anda ingin keluar dari akun ini?")
            setPositiveButton("Iya", null)
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog = builder.show()
        val btnPositif = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositif.setOnClickListener {
            listener.invoke(dialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}