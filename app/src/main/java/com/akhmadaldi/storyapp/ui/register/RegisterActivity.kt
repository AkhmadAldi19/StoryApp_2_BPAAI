package com.akhmadaldi.storyapp.ui.register

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
import com.akhmadaldi.storyapp.databinding.ActivityRegisterBinding
import com.akhmadaldi.storyapp.network.auth.RegisterResult
import com.akhmadaldi.storyapp.preference.UserPreference
import com.akhmadaldi.storyapp.ui.ViewModelFactory
import com.akhmadaldi.storyapp.ui.login.LoginActivity
import com.akhmadaldi.storyapp.utils.EmailValidation.isValidEmail

class RegisterActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[RegisterViewModel::class.java]

        registerViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            binding.nameEditTextLayout.isErrorEnabled = false
            binding.emailEditTextLayout.isErrorEnabled = false

            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = getString(R.string.nameEmptyError)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.emailEmptyError)
                }
                !isValidEmail(email) -> {
                    binding.edRegisterEmail.error = getString(R.string.emailFormatError)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.setError(getString(R.string.passwordEmptyError), null)
                }
                password.length < 6 -> {
                    binding.edRegisterPassword.setError(getString(R.string.passwordLengthError), null)
                }
                else -> {
                    registerViewModel.registerRepo(RegisterResult(name, email, password)).observe(this) {
                            result ->
                        if (result != null) {
                            when (result) {
                                is ResultResponse.Loading -> {}
                                is ResultResponse.Success -> {
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.alertTitle))
                                        setMessage(getString(R.string.alertMessageRegister))
                                        setPositiveButton(getString(R.string.alertPositiveRegister)) { _, _ ->
                                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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
                                            Toast.makeText(this@RegisterActivity, getString(R.string.FailureMessage), Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            binding.emailEditTextLayout.error = getString(R.string.EmailError)
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
        ObjectAnimator.ofFloat(binding.imageView2, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.Title, View.ALPHA, 1f).setDuration(600)
        val name = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, email, password, register)
            start()
        }
    }


}