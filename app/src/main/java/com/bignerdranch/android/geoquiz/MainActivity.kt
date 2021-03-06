package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")


        // this is a listener. event listener
        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            changeButtonState("disable")
        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            changeButtonState("disable")
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            changeButtonState("enable")
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            //create intent to pass it into the component() to communicate with the OS
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.prevButton.setOnClickListener {
            if (quizViewModel.currentIndex != 0) {
                quizViewModel.moveToPrevious()
                updateQuestion()
            }
        }

        updateQuestion()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurCheatButton()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        when {
            quizViewModel.isCheater -> Snackbar.make(binding.cheatButton, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
            userAnswer == correctAnswer -> Snackbar.make(binding.trueButton, R.string.correct_toast, Toast.LENGTH_SHORT).show()
            else -> Snackbar.make(binding.falseButton, R.string.incorrect_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeButtonState(state: String) {
        if (state == "enable") {
            binding.trueButton.isEnabled = true
            binding.falseButton.isEnabled = true

        } else if (state == "disable") {
            binding.trueButton.isEnabled = false
            binding.falseButton.isEnabled = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }

}