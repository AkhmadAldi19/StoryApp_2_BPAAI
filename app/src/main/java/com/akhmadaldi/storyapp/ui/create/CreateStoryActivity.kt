package com.akhmadaldi.storyapp.ui.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akhmadaldi.storyapp.R
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.akhmadaldi.storyapp.data.ResultResponse
import com.akhmadaldi.storyapp.databinding.ActivityCreateStoryBinding
import com.akhmadaldi.storyapp.preference.UserPreference
import com.akhmadaldi.storyapp.ui.ViewModelFactory
import com.akhmadaldi.storyapp.ui.camera.CameraActivity
import com.akhmadaldi.storyapp.ui.main.MainActivity
import com.akhmadaldi.storyapp.ui.welcome.WelcomeActivity
import com.akhmadaldi.storyapp.utils.FileUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class CreateStoryActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var createViewModel: CreateViewModel
    private lateinit var token: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        createViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[CreateViewModel::class.java]

        createViewModel.getToken().observe(this) { session ->
            if (session.Login) {
                this.token = session.token
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        createViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonUpload.setOnClickListener { uploadStory() }
        binding.btnSwitch.setOnClickListener { getMyLocation() }

    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choosePic))
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = FileUtil.rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            ).apply {

                compress(
                    Bitmap.CompressFormat.JPEG,
                    FileUtil.compressQuality(myFile), FileOutputStream(getFile)
                )
            }

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
            result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = FileUtil.uriToFile(selectedImg, this@CreateStoryActivity)
            getFile = FileUtil.reduceFileImage(myFile)

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun uploadStory(){
        if (getFile != null) {
            val file = getFile as File

            val description = binding.edDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            createViewModel.uploadImageRepo(imageMultipart, description, token ,lat,lon ).observe(this) {
                    resultResponse ->
                if (resultResponse != null) {
                    when (resultResponse) {
                        is ResultResponse.Loading -> {}
                        is ResultResponse.Success -> {
                            Toast.makeText(this@CreateStoryActivity, getString(R.string.storyCreated), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is ResultResponse.Error -> {
                            when(resultResponse.error) {
                                getString(R.string.unable) -> {
                                    Toast.makeText(this@CreateStoryActivity, getString(R.string.FailureMessage), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@CreateStoryActivity, getString(R.string.noDescription), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            Toast.makeText(this@CreateStoryActivity, getString(R.string.noFile), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission
                    .ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Toast.makeText(
                        this,
                        getString(R.string.savedLocation),
                        Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(
                        this,
                        getString(R.string.noLocation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            (Manifest.permission.ACCESS_COARSE_LOCATION)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permissionDenied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}