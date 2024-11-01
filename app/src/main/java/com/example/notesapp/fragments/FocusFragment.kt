package com.example.notesapp.fragments

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.notesapp.R

class FocusFragment : Fragment() {

    private lateinit var countdownText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var setFocusButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var rootView: View
    private var totalTimeInMillis: Long = 0L
    private var remainingTimeInMillis: Long = 0L
    private var countdownTimer: CountDownTimer? = null
    private var isPaused = false
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_focus, container, false)

        countdownText = view.findViewById(R.id.countdown_text)
        progressBar = view.findViewById(R.id.progress_bar)
        setFocusButton = view.findViewById(R.id.set_focus_button)
        stopButton = view.findViewById(R.id.set_focus_stop)
        rootView = view.findViewById(R.id.root_layout)

        countdownText.setOnClickListener {
            showCustomTimePickerDialog()
        }

        setFocusButton.setOnClickListener {
            startCountdown()
        }

        stopButton.setOnClickListener {
            togglePauseResume()
        }

        val dayNightSwitch = view.findViewById<Switch>(R.id.daynight)

        dayNightSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dayNightSwitch.setBackgroundResource(R.drawable.night_background)
                dayNightSwitch.setThumbResource(R.drawable.iconsnewmoon)
                rootView.setBackgroundResource(R.color.black)
                countdownText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                dayNightSwitch.setBackgroundResource(R.drawable.day_background)
                dayNightSwitch.setThumbResource(R.drawable.iconssun)
                rootView.setBackgroundResource(R.color.white)
                countdownText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }

        return view
    }

    private fun showCustomTimePickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.time_picker_dialog, null)
        val minutePicker: NumberPicker = dialogView.findViewById(R.id.minute_picker)
        val secondPicker: NumberPicker = dialogView.findViewById(R.id.second_picker)

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        AlertDialog.Builder(requireContext())
            .setTitle("Süre Seçin")
            .setView(dialogView)
            .setPositiveButton("Tamam") { _, _ ->
                val minutes = minutePicker.value
                val seconds = secondPicker.value
                totalTimeInMillis = (minutes * 60 + seconds) * 1000L
                remainingTimeInMillis = totalTimeInMillis
                countdownText.text = String.format("%02d:%02d", minutes, seconds)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun startCountdown() {
        countdownTimer?.cancel()

        setFocusButton.isEnabled = false
        countdownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60

                countdownText.text = String.format("%02d:%02d", minutes, seconds)
                progressBar.progress = ((millisUntilFinished * 100) / totalTimeInMillis).toInt()
            }

            override fun onFinish() {
                countdownText.text = "00:00"
                progressBar.progress = 0
                remainingTimeInMillis = totalTimeInMillis
                isPaused = false
                stopButton.setImageResource(R.drawable.iconsplay)
                setFocusButton.isEnabled = true

                showFocusEndDialog()
                playAlarmSound()
            }
        }.start()
        isPaused = false
    }

    private fun togglePauseResume() {
        if (isPaused) {
            startCountdown()
            stopButton.setImageResource(R.drawable.iconspause)
        } else {
            countdownTimer?.cancel()
            isPaused = true
            stopButton.setImageResource(R.drawable.iconsplay)
        }
    }

    private fun showFocusEndDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_focus_end, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialog_button)

        dialogButton.setOnClickListener {
            dialog.dismiss()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        dialog.show()
    }


    private fun playAlarmSound() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm_sound)
        mediaPlayer?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
