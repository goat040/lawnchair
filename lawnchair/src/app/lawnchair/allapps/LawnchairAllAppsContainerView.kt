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

    private val activity = ActivityContext.lookupContext<ActivityContext>(getContext())
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (isSearching) {
                mSearchUiManager.resetSearch()
            } else {
                activity.stateManager.goToState(LauncherState.NORMAL)
            }
        }
    }

    override fun createSearchUiDelegate() = LawnchairSearchUiDelegate(this)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        activity.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onBackPressedCallback.remove()
    }

    override fun onStateOrTransitionChanged() {
        super.onStateOrTransitionChanged()
        val isAllApps = activity.stateManager.state.allAppsActivityState.progress > 0
        onBackPressedCallback.isEnabled = isAllApps
        if (!isAllApps) {
            mSearchUiManager.resetSearch()
        }
    }
}
