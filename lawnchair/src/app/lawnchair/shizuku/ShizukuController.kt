package app.lawnchair.shizuku

import android.content.Context
import com.android.quickstep.SystemUiProxy
import com.android.systemui.shared.recents.ISystemUiProxy

class ShizukuController(private val context: Context) {

    private var proxy: ShizukuSystemUiProxy? = null

    fun getSystemUiProxy(): SystemUiProxy {
        if (proxy == null) {
            proxy = ShizukuSystemUiProxy(context)
        }
        return proxy!!
    }

    fun getSysUIStateFlags(): Int {
        return proxy?.getSysUIStateFlags() ?: 0
    }
}
