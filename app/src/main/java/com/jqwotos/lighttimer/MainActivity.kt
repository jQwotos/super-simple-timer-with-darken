package com.jqwotos.lighttimer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val msPerHour = 3600000
    private val msPerMinute = 60000
    private val msPerSecond = 1000

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondPicker: NumberPicker
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var stopAlarmButton: Button
    private lateinit var darkenButton: Button
    private lateinit var baseViewCompat: RelativeLayout
    private lateinit var blackScreen: ConstraintLayout
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    private fun timeToMs (hours: Int, minutes: Int, seconds: Int): Long {
        return (hours * msPerHour + minutes * msPerMinute + seconds * msPerSecond).toLong()
    }

    fun setPickerEnabled(isEnabled: Boolean) {
        hourPicker.isEnabled = isEnabled
        minutePicker.isEnabled = isEnabled
        secondPicker.isEnabled = isEnabled
    }

    fun getPickerTimeInMs(): Long {
        val hours = hourPicker.value
        val minutes = minutePicker.value
        val seconds = secondPicker.value

        return timeToMs(hours, minutes, seconds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide Content
        supportActionBar?.hide()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        hourPicker = findViewById(R.id.hour_picker)
        minutePicker = findViewById(R.id.minute_picker)
        secondPicker = findViewById(R.id.second_picker)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        resetButton = findViewById(R.id.reset_button)
        stopAlarmButton = findViewById(R.id.stop_alarm_button)
        darkenButton = findViewById(R.id.darken_button)
        baseViewCompat = findViewById(R.id.main_background)
        blackScreen = findViewById(R.id.black_screen)

        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI)

        blackScreen.visibility = View.GONE

        hourPicker.maxValue = 12
        hourPicker.minValue = 0

        minutePicker.maxValue = 60
        minutePicker.minValue = 0

        secondPicker.maxValue = 60
        secondPicker.minValue = 0

        startButton.setOnClickListener {
            setPickerEnabled(false)

            Log.d("TAG", "Start button clicked!")

            countDownTimer = object : CountDownTimer(getPickerTimeInMs(), msPerSecond.toLong()) {
                override fun onTick(millisUntilFinished: Long) {
                    val hours = (millisUntilFinished / (1000 * 60 * 60) % 24)
                    val minutes = (millisUntilFinished / (1000 * 60) % 60)
                    val seconds = (millisUntilFinished / 1000).toInt() % 60


                    hourPicker.value = hours.toInt()
                    minutePicker.value = minutes.toInt()
                    secondPicker.value = seconds
                }

                override fun onFinish() {
                    setPickerEnabled(true)
                    mediaPlayer.start()
                    stopAlarmButton.visibility = View.VISIBLE
                }
            }.start()
        }

        stopButton.setOnClickListener {
            if(this::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            setPickerEnabled(true)
        }

        resetButton.setOnClickListener {
            setPickerEnabled(true)

            if (this::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }

            hourPicker.value = 0
            minutePicker.value = 0
            secondPicker.value = 0
        }

        stopAlarmButton.setOnClickListener {
            mediaPlayer.seekTo(0)
            mediaPlayer.pause()
            stopAlarmButton.visibility = View.GONE
        }

        darkenButton.setOnClickListener {
            blackScreen.visibility = View.VISIBLE
        }

        blackScreen.setOnClickListener {
            blackScreen.visibility = View.GONE
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val lightSensorVal = event.values[0]

        Log.d("SENSOR", "Sensor is now $lightSensorVal")

        if (lightSensorVal > 10) {
            blackScreen.visibility = View.GONE
        } else {
            blackScreen.visibility = View.VISIBLE
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SENSOR", "Accuracy has changed to $accuracy")
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}