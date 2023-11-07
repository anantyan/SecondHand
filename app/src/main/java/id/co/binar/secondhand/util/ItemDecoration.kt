package id.co.binar.secondhand.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(
    private val context: Context,
    private val grid: Int? = null,
    private val dp: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val space = dp * context.resources.displayMetrics.density

        if (grid != null) {
            outRect.bottom = space.toInt()
            outRect.left = space.toInt()
            outRect.right = space.toInt()

            val inTop = parent.getChildLayoutPosition(view) < grid

            if (inTop) {
                outRect.top = space.toInt()
            } else {
                outRect.top = 0
            }

            val inLeft = (parent.getChildLayoutPosition(view) + 1) % grid != 0
            val inRight = !inLeft

            if (inLeft) {
                outRect.right = (space/2).toInt()
            }

            if (inRight) {
                outRect.left = (space/2).toInt()
            }
        } else {
            outRect.left = space.toInt()
            outRect.right = space.toInt()
            outRect.bottom = space.toInt()
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space.toInt()
            } else {
                outRect.top = 0;
            }
        }
    }
}