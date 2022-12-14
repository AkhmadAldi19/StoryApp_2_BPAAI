package com.akhmadaldi.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.akhmadaldi.storyapp.DummyData
import com.akhmadaldi.storyapp.MainDispatcherRule
import com.akhmadaldi.storyapp.data.ResultResponse
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.getOrAwaitValue
import com.akhmadaldi.storyapp.network.auth.LoginResponse
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
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreference: UserPreference

    @Mock
    private lateinit var storyRepository: StoryRepository


    private lateinit var loginViewModel: LoginViewModel
    private val email = "email@gmail.com"
    private val password = "12345"

    @Before
    fun setup(){
        loginViewModel = LoginViewModel(userPreference,storyRepository)
    }

    @Test
    fun `when User Login should response not null return Success`() {
        val dummyData = DummyData.generateDummyLoginResponse()
        val expectedResponse = MutableLiveData<ResultResponse<LoginResponse>>()
        expectedResponse.value = ResultResponse.Success(dummyData)

        `when`(storyRepository.login(email, password, userPreference)).thenReturn(expectedResponse)

        val actualData = loginViewModel.loginRepo(email,password).getOrAwaitValue()
        Mockito.verify(storyRepository).login(email,password,userPreference)
        Assert.assertNotNull(actualData)
        Assert.assertTrue(actualData is ResultResponse.Success)
    }

    @Test
    fun `when User Login failed should response not null return Error`() {
        val expectedResponse = MutableLiveData<ResultResponse<LoginResponse>>()
        expectedResponse.value = ResultResponse.Error("error")

        `when`(storyRepository.login(email, password, userPreference)).thenReturn(expectedResponse)

        val actualData = loginViewModel.loginRepo(email,password).getOrAwaitValue()
        Mockito.verify(storyRepository).login(email,password,userPreference)
        Assert.assertNotNull(actualData)
        Assert.assertTrue(actualData is ResultResponse.Error)
    }


}



