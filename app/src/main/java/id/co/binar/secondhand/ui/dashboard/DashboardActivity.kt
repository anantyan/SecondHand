package id.co.binar.secondhand.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import id.co.binar.secondhand.R
import id.co.binar.secondhand.databinding.ActivityDashboardBinding
import id.co.binar.secondhand.ui.dashboard.account.AccountFragment
import id.co.binar.secondhand.ui.dashboard.home.HomeFragment
import id.co.binar.secondhand.ui.dashboard.list_sell.ListSellFragment
import id.co.binar.secondhand.ui.dashboard.notification.NotificationFragment
import id.co.binar.secondhand.ui.product_add.ProductAddActivity

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(),
    NavigationBarView.OnItemSelectedListener {

    private lateinit var sectionViewPager: DashboardViewPagerAdapter
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel by viewModels<DashboardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        val itemFragment = listOf(
            HomeFragment(),
            NotificationFragment(),
            ListSellFragment(),
            AccountFragment()
        )

        sectionViewPager = DashboardViewPagerAdapter(
            supportFragmentManager,
            lifecycle,
            itemFragment
        )

        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.title.observe(this) {
            supportActionBar?.title = it
        }
    }

    private fun bindView() {
        binding.apply {
            viewPager.adapter = sectionViewPager
            viewPager.isUserInputEnabled = false
            toolbar.isVisible = false
            bottomNavbar.setOnItemSelectedListener(this@DashboardActivity)
        }
    }

    private fun onSetViewPager(it: Int) {
        binding.viewPager.setCurrentItem(it, false)
        when (binding.viewPager.currentItem) {
            0 -> {
                onSetBottomNavigation(R.id.homeFragment)
                binding.toolbar.isVisible = false
                viewModel.title("")
            }
            1 -> {
                onSetBottomNavigation(R.id.notificationFragment)
                binding.toolbar.isVisible = true
                viewModel.title("Notifikasi")
            }
            2 -> {
                onSetBottomNavigation(R.id.listSellFragment)
                binding.toolbar.isVisible = true
                viewModel.title("Daftar Jual Saya")
            }
            3 -> {
                onSetBottomNavigation(R.id.accountFragment)
                binding.toolbar.isVisible = true
                viewModel.title("Akun")
            }
            else -> {
                onSetBottomNavigation(R.id.homeFragment)
                binding.toolbar.isVisible = false
                viewModel.title("")
            }
        }
    }

    private fun onSetBottomNavigation(resId: Int) {
        binding.bottomNavbar.menu.findItem(resId).isChecked = true
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem > 0 && binding.viewPager.currentItem <= sectionViewPager.itemCount - 1) {
            onSetViewPager(0)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.homeFragment -> {
                onSetViewPager(0)
                true
            }
            R.id.notificationFragment -> {
                onSetViewPager(1)
                true
            }
            R.id.productAddFragment -> {
                val intent = Intent(this, ProductAddActivity::class.java)
                startActivity(intent)
                false
            }
            R.id.listSellFragment -> {
                onSetViewPager(2)
                true
            }
            R.id.accountFragment -> {
                onSetViewPager(3)
                true
            }
            else -> false
        }
    }
}