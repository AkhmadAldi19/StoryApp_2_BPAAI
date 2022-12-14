package com.akhmadaldi.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.akhmadaldi.storyapp.DummyData
import com.akhmadaldi.storyapp.MainDispatcherRule
import com.akhmadaldi.storyapp.data.ResultResponse
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.getOrAwaitValue
import com.akhmadaldi.storyapp.network.auth.RegisterResponse
import com.akhmadaldi.storyapp.network.auth.RegisterResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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
class RegisterViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository


    private lateinit var registerViewModel: RegisterViewModel
    private val name = "name"
    private val email = "email@gmail.com"
    private val password = "1213233"
    private val dummyResponse = DummyData.generateDummyRegisterResponse()

    @Before
    fun setup(){
        registerViewModel = RegisterViewModel (storyRepository)
    }

    @Test
    fun `when user register success should response not null and Return Success`() = runTest {
        val expectedResponse = MutableLiveData<ResultResponse<RegisterResponse>>()
        expectedResponse.value = ResultResponse.Success(dummyResponse)

        `when`(storyRepository.register(RegisterResult(name, email, password))).thenReturn(expectedResponse)

        val actualResponse = registerViewModel.registerRepo(RegisterResult(name, email, password)).getOrAwaitValue()
        Mockito.verify(storyRepository).register(RegisterResult(name, email, password))
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultResponse.Success)
    }

    @Test
    fun `when User Register failed should Not Null and return Error`(){
        val expectedResponse = MutableLiveData<ResultResponse<RegisterResponse>>()
        expectedResponse.value = ResultResponse.Error("error")

        `when`(storyRepository.register(RegisterResult(name, email, password))).thenReturn(expectedResponse)

        val actualResponse = registerViewModel.registerRepo(RegisterResult(name, email, password)).getOrAwaitValue()
        Mockito.verify(storyRepository).register(RegisterResult(name, email, password))
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultResponse.Error)
    }
}