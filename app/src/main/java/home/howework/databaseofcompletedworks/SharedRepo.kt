package home.howework.databaseofcompletedworks

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.widget.Toast

private const val PREFERENCE_NAME = "specialStorage"
private const val SHARED_PREF_KEY = "SavedDate"
private const val SHARED_PREF_KEY2 = "SavedName"
private const val SHARED_PREF_KEY3 = "SavedCabinetCount"
private const val SHARED_PREF_KEY4 = "SavedDayState"
private const val SHARED_PREF_KEY5 = "ResumeDayState"
private const val SHARED_PREF_KEY6 = "TurnGadjet"
private const val SHARED_PREF_KEY7 = "PauseDayState"

class SharedRepo {
    var date: Long?
    var nameUser: String?
    var cabcount:Int
    var dayState:Boolean
    var resumeDay:Boolean
    var turn:Boolean
    var pauseDay:Boolean

    init {
        date = null
        nameUser = null
        cabcount=0
        dayState=false
        resumeDay=false
        turn=false
        pauseDay=false
    }
    fun getDate(context: Context): Long? {
        return when {
            getDateFromLocalVariable() != null -> getDateFromLocalVariable()
            getDateFromSharedPreference(context) != null -> getDateFromSharedPreference(context)
            else -> {
                null
            }
        }
    }

    fun getName(context: Context): String? {
        return when {
            getNameUserFromLocalVariable() != null -> getNameUserFromLocalVariable()
            getNameUserFromSharedPreference(context) != null -> getNameUserFromSharedPreference(context)
            else -> {
                null
            }
        }
    }
    fun getCabCount(context: Context): Int {
        return getLastCabCountForThisDayWorkFromSharedPreference(context)
    }
    fun getDayState(context: Context): Boolean {
        return  getWorkDayState(context)
    }
    fun getPauseState(context: Context): Boolean {
        return  getPauseDayState(context)
    }
    fun getTurnGadjetState(context: Context): Boolean {
        return  getTurnState(context)
    }
    fun getResumeDayState(context: Context): Boolean {
        return  getResumeDayStateSharedPreference(context)
    }

    fun saveResumeDayState(resumeState: Boolean, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(SHARED_PREF_KEY5, resumeState)
        editor.commit()
        resumeDay = resumeState
    }
    fun saveNewDate(newDate: Long, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong(SHARED_PREF_KEY, newDate)
        editor.commit()
        date = newDate
    }

    fun saveName(name: String, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(SHARED_PREF_KEY2, name)
        editor.commit()
        nameUser = name
    }
    fun saveCabCount(count: Int, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(SHARED_PREF_KEY3, count)
        editor.commit()
        cabcount = count
    }
    fun saveDayState(state: Boolean, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(SHARED_PREF_KEY4, state)
        editor.commit()
        dayState = state
    }
    fun saveTurnState(state: Boolean, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(SHARED_PREF_KEY6, state)
        editor.commit()
        turn = state
    }
    fun savePauseState(state: Boolean, context: Context) {
        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(SHARED_PREF_KEY7, state)
        editor.commit()
        pauseDay = state
    }


    //        fun clearOldDate(context: Context) {
//            var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
//            val editor = prefs.edit()
//            editor.clear()
//            editor.apply()
//            date = null
//        }
//    fun clearOldName(context: Context) {
//        var prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
//        val editor = prefs.edit()
//        editor.clear()
//        editor.apply()
//        date = null
//    }


    private fun getDateFromLocalVariable(): Long? {
        return date
    }

    private fun getNameUserFromLocalVariable(): String? {
        return nameUser
    }
    private fun getCabCountFromLocalVariable(): Int {
        return cabcount
    }
    private fun getDayStateFromLocalVariable(): Boolean {
        return dayState
    }
    private fun getWorkDayState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getBoolean(SHARED_PREF_KEY4, false)
    }
    private fun getPauseDayState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getBoolean(SHARED_PREF_KEY7, false)
    }
    private fun getTurnState(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getBoolean(SHARED_PREF_KEY6, false)
    }
    private fun getDateFromSharedPreference(context: Context): Long? {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getLong(SHARED_PREF_KEY, 1L)
    }

    private fun getNameUserFromSharedPreference(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getString(SHARED_PREF_KEY2, null)
    }
    private fun getLastCabCountForThisDayWorkFromSharedPreference(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getInt(SHARED_PREF_KEY3, 0)
    }
    private fun getResumeDayStateSharedPreference(context: Context): Boolean{
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getBoolean(SHARED_PREF_KEY5, false)
    }
}
