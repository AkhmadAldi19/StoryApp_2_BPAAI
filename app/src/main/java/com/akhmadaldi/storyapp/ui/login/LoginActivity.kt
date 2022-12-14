package com.akhmadaldi.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.akhmadaldi.storyapp.R
import com.akhmadaldi.storyapp.data.ResultResponse
import com.akhmadaldi.storyapp.databinding.ActivityLoginBinding
import com.akhmadaldi.storyapp.preference.UserPreference
import com.akhmadaldi.storyapp.ui.ViewModelFactory
import com.akhmadaldi.storyapp.ui.main.MainActivity
import com.akhmadaldi.storyapp.utils.EmailValidation.isValidEmail

class LoginActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[LoginViewModel::class.java]

        loginViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {

            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.emailEmptyError)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.setError(getString(R.string.passwordEmptyError), null)
                }
                !isValidEmail(email) -> {
                    binding.edLoginEmail.error = getString(R.string.emailFormatError)
                }
                password.length < 6 -> {
                    binding.edLoginPassword.setError(getString(R.string.passwordLengthError), null)
                }
                else -> {
                    loginViewModel.loginRepo(email, password).observe(this) {
                            result ->
                        if (result != null) {
                            when (result) {
                                is ResultResponse.Loading -> {}
                                is ResultResponse.Success -> {
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.alertTitle))
                                        setMessage(R.string.alertMessage)
                                        setPositiveButton(R.string.alertPositive) { _, _ ->
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                }
                                is ResultResponse.Error -> {
                                    when(result.error) {
                                        "Unable to resolve host \"story-api.dicoding.dev\": No address associated with hostname" -> {
                                            Toast.makeText(this@LoginActivity, getString(R.string.FailureMessage), Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            Toast.makeText(this@LoginActivity, getString(R.string.wrong_email_password), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.TitleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, email, password, login)
            start()
        }
    }

}