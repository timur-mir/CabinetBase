package home.howework.databaseofcompletedworks

import android.app.Application
import android.content.Context

class MainApp : Application() {
    companion object {
        var appContext: Context? = null
    }
    override fun onCreate() {
        super.onCreate()
        CabinetRepository.initialize(this)
        appContext=applicationContext
    }
}