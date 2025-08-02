package app.lawnchair.allapps

import android.content.Context
import android.util.AttributeSet
import androidx.activity.OnBackPressedCallback
import app.lawnchair.search.LawnchairSearchUiDelegate
import com.android.launcher3.LauncherState
import com.android.launcher3.allapps.LauncherAllAppsContainerView
import com.android.launcher3.views.ActivityContext

open class LawnchairAllAppsContainerView(
    context: Context,
    attrs: AttributeSet?,
) : LauncherAllAppsContainerView(context, attrs) {

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (isSearching) {
                mSearchUiManager.resetSearch()
            } else {
                mActivityContext.stateManager.goToState(LauncherState.NORMAL)
            }
        }
    }

    override fun createSearchUiDelegate() = LawnchairSearchUiDelegate(this)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mActivityContext.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onBackPressedCallback.remove()
    }

    fun onStateOrTransitionChanged() {
        val isAllApps = mActivityContext.stateManager.state.allAppsActivityState.progress > 0
        onBackPressedCallback.isEnabled = isAllApps
        if (!isAllApps) {
            mSearchUiManager.resetSearch()
        }
    }
}
