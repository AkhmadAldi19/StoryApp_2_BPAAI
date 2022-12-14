package com.akhmadaldi.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akhmadaldi.storyapp.R
import com.akhmadaldi.storyapp.adapter.LoadingAdapter
import com.akhmadaldi.storyapp.adapter.StoryAdapter
import com.akhmadaldi.storyapp.databinding.ActivityMainBinding
import com.akhmadaldi.storyapp.preference.UserPreference
import com.akhmadaldi.storyapp.ui.ViewModelFactory
import com.akhmadaldi.storyapp.ui.create.CreateStoryActivity
import com.akhmadaldi.storyapp.ui.maps.MapsActivity
import com.akhmadaldi.storyapp.ui.welcome.WelcomeActivity


class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvStory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        rvStory = binding.rvListStory
        rvStory.setHasFixedSize(true)
        rvStory.scrollToPosition(0)
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[MainViewModel::class.java]

        mainViewModel.getToken().observe(this) { session ->
            if (session.Login) {
                getData(session.token)

            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

    }

    private fun setupAction() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            swipeRefresh.isRefreshing = false
        }
        binding.ivAddStory.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMenu(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMenu(itemId: Int) {
        when (itemId) {
            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                val alert = builder.create()
                builder
                    .setTitle(getString(R.string.alertTittleLogout))
                    .setMessage(getString(R.string.alertMassageLogout))
                    .setPositiveButton(getString(R.string.positiveLogout)) { _, _ ->
                        mainViewModel.logout()
                    }
                    .setNegativeButton(getString(R.string.negativeLogout)) { _, _ ->
                        alert.cancel()
                    }
                    .show()
            }
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.action_action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
    }

    private fun getData(token: String) {
        rvStory.layoutManager = LinearLayoutManager(this)

        val adapter = StoryAdapter()

        binding.rvListStory.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingAdapter {
                adapter.retry()
            },
            footer = LoadingAdapter {
                adapter.retry()
            }
        )
        mainViewModel.story(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
}
