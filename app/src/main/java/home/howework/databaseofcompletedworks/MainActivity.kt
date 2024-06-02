package home.howework.databaseofcompletedworks

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import home.howework.databaseofcompletedworks.databinding.ActivityMainBinding
import home.howework.databaseofcompletedworks.databinding.ArhiveActivityBinding

class MainActivity : AppCompatActivity() {
    private val dateRepository = SharedRepo()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        dateRepository.saveTurnState(true,this)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

//        if (count == 0) {
//            super.onBackPressed()
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
//            finish()
//        } else {
//          //supportFragmentManager.popBackStack()
//        }
    }
    object CallMenu {
        var newWorkDay=false
        var arhiveMode=false
        var timeChange=false
        var cabinetCount:Int=0
        var newCabinetFlag=false
        var flag = false
        var orientationChange=false
        lateinit var menu2: Menu
    }
}