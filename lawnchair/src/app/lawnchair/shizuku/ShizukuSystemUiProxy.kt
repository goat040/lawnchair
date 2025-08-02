package app.lawnchair.shizuku

import android.content.Context
import com.android.quickstep.SystemUiProxy
import com.android.systemui.shared.recents.ISystemUiProxy

class ShizukuSystemUiProxy(context: Context) : SystemUiProxy(context) {
    override fun getSystemUiProxy(): ISystemUiProxy? {
        return ShizukuService.getSystemUiProxy(context)
    }

    override fun setProxy(
        proxy: ISystemUiProxy?,
        pip: IPip?,
        bubbles: IBubbles?,
        splitScreen: ISplitScreen?,
        oneHanded: IOneHanded?,
        shellTransitions: IShellTransitions?,
        startingWindow: IStartingWindow?,
        recentTasks: IRecentTasks?,
        sysuiUnlockAnimationController: ISysuiUnlockAnimationController?,
        backAnimation: IBackAnimation?,
        desktopMode: IDesktopMode?,
        unfoldAnimation: IUnfoldAnimation?,
        dragAndDrop: IDragAndDrop?
    ) {
        super.setProxy(
            getSystemUiProxy(),
            ShizukuService.getPip(context),
            ShizukuService.getBubbles(context),
            ShizukuService.getSplitScreen(context),
            ShizukuService.getOneHanded(context),
            ShizukuService.getShellTransitions(context),
            ShizukuService.getStartingWindow(context),
            ShizukuService.getRecentTasks(context),
            ShizukuService.getSysuiUnlockAnimationController(context),
            ShizukuService.getBackAnimation(context),
            ShizukuService.getDesktopMode(context),
            ShizukuService.getUnfoldAnimation(context),
            ShizukuService.getDragAndDrop(context)
        )
    }

    override fun getSysUIStateFlags(): Int {
        TODO("Not yet implemented")
    }
}
