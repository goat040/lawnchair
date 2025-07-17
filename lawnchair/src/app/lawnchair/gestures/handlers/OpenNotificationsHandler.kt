package app.lawnchair.gestures.handlers

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import app.lawnchair.LawnchairLauncher
import java.lang.reflect.InvocationTargetException

class OpenNotificationsHandler(context: Context) : GestureHandler(context) {

    @SuppressLint("WrongConstant")
    override suspend fun onTrigger(launcher: LawnchairLauncher) {
        try {
            Class.forName("android.app.StatusBarManager")
                .getMethod("expandNotificationsPanel")
                .apply { isAccessible = true }
                .invoke(context.getSystemService("statusbar"))
        } catch (e: IllegalAccessException) {
            Log.e("OpenNotifications", "Failed to expand notifications", e)
        } catch (e: InvocationTargetException) {
            Log.e("OpenNotifications", "Failed to expand notifications", e)
        } catch (e: NoSuchMethodException) {
            Log.e("OpenNotifications", "Failed to expand notifications", e)
        } catch (e: ClassNotFoundException) {
            Log.e("OpenNotifications", "Failed to expand notifications", e)
        }
    }
}
