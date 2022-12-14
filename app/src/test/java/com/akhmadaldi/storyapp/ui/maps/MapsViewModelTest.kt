package com.akhmadaldi.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.akhmadaldi.storyapp.DummyData
import com.akhmadaldi.storyapp.MainDispatcherRule
import com.akhmadaldi.storyapp.data.ResultResponse
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.getOrAwaitValue
import com.akhmadaldi.storyapp.preference.UserModel
import com.akhmadaldi.storyapp.preference.UserPreference
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
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var userPreference: UserPreference

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var mapsViewModel: MapsViewModel
    private val token = "Token"

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DummyData.generateDummyStoryEntity()
        val expectedStory = MutableLiveData<ResultResponse<List<StoryEntity>>>()
        expectedStory.value = ResultResponse.Success(dummyStory)

        `when`(storyRepository.getStoryLocation(token)).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getStory(token).getOrAwaitValue()
        Mockito.verify(storyRepository).getStoryLocation(token)
        assertNotNull(actualStory)
        assertTrue(actualStory is ResultResponse.Success)
    }

    @Test
    fun `when Get Story failed Should Not Null and Return Error`() = runTest {
        val expectedStory = MutableLiveData<ResultResponse<List<StoryEntity>>>()
        expectedStory.value = ResultResponse.Error("error")

        `when`(storyRepository.getStoryLocation(token)).thenReturn(expectedStory)

        val actualListStory = mapsViewModel.getStory(token).getOrAwaitValue ()
        Mockito.verify(storyRepository).getStoryLocation(token)
        assertNotNull(actualListStory)
        assertTrue(actualListStory is ResultResponse.Error)
    }

    @Test
    fun `when get account token not null and return success`(){
        val dummyUserData = DummyData.generateDummyUserDataLogin()
        val expectedToken = MutableLiveData<UserModel>()
        expectedToken.value = dummyUserData

        `when`(userPreference.getToken()).thenReturn(expectedToken.asFlow())

        val actualToken = mapsViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userPreference).getToken()
        assertNotNull(actualToken)
        assertEquals(dummyUserData, actualToken)
    }
}