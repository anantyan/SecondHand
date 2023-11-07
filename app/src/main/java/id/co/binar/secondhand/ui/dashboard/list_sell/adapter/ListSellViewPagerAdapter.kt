package id.co.binar.secondhand.ui.dashboard.list_sell.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.co.binar.secondhand.ui.dashboard.list_sell.child.ListSellByDiminatiFragment
import id.co.binar.secondhand.ui.dashboard.list_sell.child.ListSellByProductFragment
import id.co.binar.secondhand.ui.dashboard.list_sell.child.ListSellByTerjualFragment

class ListSellViewPagerAdapter(
    fm: FragmentManager,
    lifeCycle: Lifecycle,
) : FragmentStateAdapter(fm, lifeCycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListSellByProductFragment()
            1 -> ListSellByDiminatiFragment()
            2 -> ListSellByTerjualFragment()
            else -> ListSellByProductFragment()
        }
    }
}