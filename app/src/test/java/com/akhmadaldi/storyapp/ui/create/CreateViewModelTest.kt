package com.akhmadaldi.storyapp.ui.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.akhmadaldi.storyapp.DummyData
import com.akhmadaldi.storyapp.MainDispatcherRule
import com.akhmadaldi.storyapp.data.ResultResponse
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.getOrAwaitValue
import com.akhmadaldi.storyapp.network.story.FileUploadResponse
import com.akhmadaldi.storyapp.preference.UserModel
import com.akhmadaldi.storyapp.preference.UserPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CreateViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var userPreference: UserPreference
    @Mock
    private lateinit var storyRepository: StoryRepository


    private lateinit var createViewModel: CreateViewModel
    private val imageFile = DummyData.multipartFile()
    private val description = DummyData.description()
    private val token = "Token"
    private val lat = 36.8546
    private val lon = -120.7286

    @Before
    fun setUp() {
        createViewModel = CreateViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when user upload Image should response not null and Return Success`() {
        val dummySuccess = DummyData.generateDummyFileUploadSuccess()
        val response = MutableLiveData<ResultResponse<FileUploadResponse>>()
        response.value = ResultResponse.Success(dummySuccess)

        `when`(storyRepository.uploadImage(imageFile, description, token, lat, lon)).thenReturn(
            response
        )
        val successResponse =
            createViewModel.uploadImageRepo(imageFile, description, token, lat, lon).getOrAwaitValue()
        Mockito.verify(storyRepository).uploadImage(imageFile, description, token, lat, lon)
        Assert.assertNotNull(successResponse)
        Assert.assertTrue(successResponse is ResultResponse.Success)
    }

    @Test
    fun `when user upload image failed should response not null and Return Error`() {
        val dummyError = DummyData.generateDummyFileUploadError()
        val erorrResponse = MutableLiveData<ResultResponse<FileUploadResponse>>()
        erorrResponse.value = ResultResponse.Error(dummyError.message)

        `when`(storyRepository.uploadImage(imageFile, description, token, lat, lon)).thenReturn(erorrResponse)

        val errorResponse =
            createViewModel.uploadImageRepo(imageFile, description, token, lat, lon).getOrAwaitValue()
        Mockito.verify(storyRepository).uploadImage(imageFile, description, token, lat, lon)
        Assert.assertNotNull(errorResponse)
        Assert.assertTrue(errorResponse is ResultResponse.Error)
    }

    @Test
    fun `when get account token not null and return success`(){
        val dummyUserData = DummyData.generateDummyUserDataLogin()
        val expectedToken = MutableLiveData<UserModel>()
        expectedToken.value = dummyUserData

        `when`(userPreference.getToken()).thenReturn(expectedToken.asFlow())

        val actualToken = createViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userPreference).getToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(dummyUserData, actualToken)
    }

}