package app.lawnchair.shizuku

import android.content.Context
import com.android.quickstep.SystemUiProxy
import com.android.systemui.shared.recents.ISystemUiProxy

class ShizukuSystemUiProxy(context: Context) : SystemUiProxy(context) {
    override fun getSystemUiProxy(): ISystemUiProxy? {
        return ShizukuService.getSystemUiProxy(context)
    }

    override fun getSysUIStateFlags(): Int {
        TODO("Not yet implemented")
    }
}
