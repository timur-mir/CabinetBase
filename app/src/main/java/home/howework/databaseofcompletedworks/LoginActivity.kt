package home.howework.databaseofcompletedworks

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


import android.animation.PropertyValuesHolder
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import home.howework.databaseofcompletedworks.databinding.LoginActivityBinding
import java.util.*


class LoginActivity : AppCompatActivity() {
    var userName = ""
    private val dateRepository = SharedRepo()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)
/////////////////////////////////////////////////////////////////////////////////////////
        dateRepository.saveTurnState(true, this)

        val animTextView = binding.notifyView
        if (dateRepository.getCabCount(this) == 1) {
            binding.notifyView.text =
                "За сегодняшний день собран ${dateRepository.getCabCount(this)} кабинет???"
        }
        if (dateRepository.getCabCount(this) == 5) {
            binding.notifyView.text =
                "За сегодняшний день собрано ${dateRepository.getCabCount(this)} кабинетов!"
        }
        if (dateRepository.getCabCount(this) == 6 ||
            dateRepository.getCabCount(this) == 7 || dateRepository.getCabCount(this) == 8
        ) {
            binding.notifyView.text =
                "За сегодня ${dateRepository.getCabCount(this)} кабинетов Отлично!"
        }
        if (dateRepository.getCabCount(this) == 9 ||
            dateRepository.getCabCount(this) == 10 || dateRepository.getCabCount(this) >= 11
        ) {
            binding.notifyView.text =
                "За сегодня ${dateRepository.getCabCount(this)} кабинетов!!! Ты великолепен!!!"
        }
        if (dateRepository.getCabCount(this) == 2 || dateRepository.getCabCount(this) == 3 || dateRepository.getCabCount(
                this
            ) == 4
        ) {
            binding.notifyView.text =
                "За сегодняшний день собрано ${dateRepository.getCabCount(this)} кабинета"
        }
///////////////////////////////////////////////////////////////////////////////////////////
        var date: Date = Date()
        dateRepository.saveNewDate(date.time, this)

        val animation: Animation =
            AnimationUtils.loadAnimation(this, R.anim.sequential_animation)
        val textShader = LinearGradient(
            0f, 0f,
            animTextView.paint.measureText(animTextView.text.toString()),
            animTextView.textSize,
            intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE),
            null,
            Shader.TileMode.CLAMP
        )
        animTextView.paint.shader = textShader
        animTextView.invalidate()
        ValueAnimator.ofObject(
            GradientArgbEvaluator,
            intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE),
            intArrayOf(Color.WHITE, Color.WHITE, Color.YELLOW),
            intArrayOf(Color.WHITE, Color.YELLOW, Color.RED),
            intArrayOf(Color.YELLOW, Color.RED, Color.BLUE),
            intArrayOf(Color.YELLOW, Color.WHITE, Color.MAGENTA),
            intArrayOf(Color.GRAY, Color.WHITE, Color.YELLOW),
            intArrayOf(Color.WHITE, Color.RED, Color.WHITE),
            intArrayOf(Color.BLUE, Color.YELLOW, Color.CYAN),
            intArrayOf(Color.WHITE, Color.RED, Color.CYAN),
            intArrayOf(Color.GRAY, Color.MAGENTA, Color.WHITE)
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 3000
            addUpdateListener {
                val procShader = LinearGradient(
                    0f, 0f,
                    animTextView.paint.measureText(animTextView.text.toString()),
                    animTextView.textSize,
                    it.animatedValue as IntArray,
                    null,
                    Shader.TileMode.CLAMP
                )
                animTextView.paint.shader = procShader
                animTextView.invalidate()
            }
            start()

        }

        if (dateRepository.getName(this) != null) {

            val userNameInSP = dateRepository.getName(this)

            binding.editName.visibility = View.GONE
            binding.buttonEnter.visibility = View.GONE
            binding.imageApp17.text = "Специалист по сборке кабинетов и экранов: ${userNameInSP}"
            binding.imageApp17.textSize = 30F
            binding.buttonNext.isEnabled = true
            binding.buttonNext.visibility = View.VISIBLE
            binding.buttonArhive.isEnabled = true
            binding.buttonArhive.visibility = View.VISIBLE

            val rotationX = PropertyValuesHolder.ofFloat(View.ROTATION_X, 0f, 720f)
            val textColor = PropertyValuesHolder.ofInt(
                "textColor",
                Color.parseColor("#FF00F000"),
                Color.parseColor("#FF0000FF")
            ).apply {
                setEvaluator(ArgbEvaluator())
            }
            val buttonAnim = binding.buttonArhive
            ObjectAnimator.ofPropertyValuesHolder(buttonAnim, rotationX, textColor).apply {
                duration = 5000
                interpolator = AccelerateInterpolator()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                start()
            }
            if (dateRepository.getDayState(this) && dateRepository.getResumeDayState(this)) {
                binding.buttonNext.text = "Продолжить день"

                dateRepository.savePauseState(true, this)

            } else if (dateRepository.getDayState(this) == false) {

                dateRepository.saveDayState(true, this)

                binding.buttonNext.text = "Начать день"
            }
        }
        val textAnim = binding.buttonNext
        ObjectAnimator.ofArgb(
            textAnim,
            "textColor",
            Color.parseColor("#FF00F000"),
            Color.parseColor("#FF0000FF")
        ).apply {
            duration = 5000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        binding.buttonNext.setOnClickListener {
            Handler().postDelayed(
                {
                    dateRepository.saveDayState(true, this)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                },

                500

            )
        }


        binding.buttonEnter.setOnClickListener {
            userName = binding.editName.text.toString()
            dateRepository.saveName(binding.editName.text.toString(), this)
            binding.editName.visibility = View.INVISIBLE
            binding.buttonEnter.visibility = View.INVISIBLE
            binding.imageApp17.visibility = View.INVISIBLE
            Toast.makeText(this, "${userName}: Вы зарегистрированы!", Toast.LENGTH_SHORT)
                .show()
            Handler().postDelayed(
                {
                    finish()
                    val intent = Intent(this, LoginActivity::class.java)
                    //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                },

                1000

            )

        }
        binding.buttonArhive.setOnClickListener {
            Handler().postDelayed(
                {
                    finish()
                    val intent = Intent(this, ArhiveActivity::class.java)
                    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                },

                500

            )
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            finish()
        } else {
        }
    }

    override fun onStop() {
        super.onStop()
        //dateRepository.saveTurnState(false,this)
    }

    object GradientArgbEvaluator : TypeEvaluator<IntArray> {
        private val argbEvaluator = ArgbEvaluator()
        override fun evaluate(
            fraction: Float,
            startValue: IntArray,
            endValue: IntArray
        ): IntArray {
            return startValue.mapIndexed { index, item ->
                argbEvaluator.evaluate(fraction, item, endValue[index]) as Int
            }.toIntArray()
        }
    }


}
