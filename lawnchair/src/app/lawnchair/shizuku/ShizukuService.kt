package app.lawnchair.shizuku

import android.os.IBinder
import com.android.systemui.shared.recents.ISystemUiProxy
import rikka.shizuku.ShizukuApiConstants.USER_SERVICE_ARG_TOKEN
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.ShizukuService

class ShizukuService : ShizukuService() {

    companion object {
        fun getSystemUiProxy(context: Context): ISystemUiProxy? {
            val bundle = ShizukuProvider.call(
                context.contentResolver,
                ShizukuApiConstants.METHOD_GET_USER_SERVICE,
                "systemui",
                Bundle().apply {
                    putParcelable(USER_SERVICE_ARG_TOKEN, ShizukuService.getToken())
                }
            ) ?: return null

            val binder = bundle.getBinder("binder") ?: return null
            return ISystemUiProxy.Stub.asInterface(binder)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // TODO: Get the binders from the SystemUI Dagger component using reflection.
    }

    private fun getBinder(className: String): IBinder? {
        try {
            val systemUi = ShizukuSystemUiProxy.getSystemUi(this)
            val component = systemUi.javaClass.getMethod("getComponent").invoke(systemUi)
            val method = component.javaClass.methods.find { it.returnType.name == className }
            return method?.invoke(component) as IBinder?
        } catch (e: Exception) {
            Log.e("ShizukuService", "Failed to get binder for $className", e)
            return null
        }
    }

    private fun getSystemUi(): ISystemUiProxy? {
        val binder = getBinder("com.android.systemui.shared.recents.ISystemUiProxy")
        return ISystemUiProxy.Stub.asInterface(binder)
    }

    private fun getPip(): IPip? {
        val binder = getBinder("com.android.wm.shell.common.pip.IPip")
        return IPip.Stub.asInterface(binder)
    }

    private fun getBubbles(): IBubbles? {
        val binder = getBinder("com.android.wm.shell.bubbles.IBubbles")
        return IBubbles.Stub.asInterface(binder)
    }

    private fun getSplitScreen(): ISplitScreen? {
        val binder = getBinder("com.android.wm.shell.splitscreen.ISplitScreen")
        return ISplitScreen.Stub.asInterface(binder)
    }

    private fun getOneHanded(): IOneHanded? {
        val binder = getBinder("com.android.wm.shell.onehanded.IOneHanded")
        return IOneHanded.Stub.asInterface(binder)
    }

    private fun getShellTransitions(): IShellTransitions? {
        val binder = getBinder("com.android.wm.shell.shared.IShellTransitions")
        return IShellTransitions.Stub.asInterface(binder)
    }

    private fun getStartingWindow(): IStartingWindow? {
        val binder = getBinder("com.android.wm.shell.startingsurface.IStartingWindow")
        return IStartingWindow.Stub.asInterface(binder)
    }

    private fun getRecentTasks(): IRecentTasks? {
        val binder = getBinder("com.android.wm.shell.recents.IRecentTasks")
        return IRecentTasks.Stub.asInterface(binder)
    }

    private fun getSysuiUnlockAnimationController(): ISysuiUnlockAnimationController? {
        val binder = getBinder("com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController")
        return ISysuiUnlockAnimationController.Stub.asInterface(binder)
    }

    private fun getBackAnimation(): IBackAnimation? {
        val binder = getBinder("com.android.wm.shell.back.IBackAnimation")
        return IBackAnimation.Stub.asInterface(binder)
    }

    private fun getDesktopMode(): IDesktopMode? {
        val binder = getBinder("com.android.wm.shell.desktopmode.IDesktopMode")
        return IDesktopMode.Stub.asInterface(binder)
    }

    private fun getUnfoldAnimation(): IUnfoldAnimation? {
        val binder = getBinder("com.android.systemui.unfold.progress.IUnfoldAnimation")
        return IUnfoldAnimation.Stub.asInterface(binder)
    }

    private fun getDragAndDrop(): IDragAndDrop? {
        val binder = getBinder("com.android.wm.shell.draganddrop.IDragAndDrop")
        return IDragAndDrop.Stub.asInterface(binder)
    }
}
