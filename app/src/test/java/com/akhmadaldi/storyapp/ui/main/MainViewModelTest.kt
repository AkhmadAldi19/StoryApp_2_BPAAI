package com.akhmadaldi.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.akhmadaldi.storyapp.DummyData
import com.akhmadaldi.storyapp.MainDispatcherRule
import com.akhmadaldi.storyapp.adapter.StoryAdapter
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.getOrAwaitValue
import com.akhmadaldi.storyapp.preference.UserModel
import com.akhmadaldi.storyapp.preference.UserPreference
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository : StoryRepository
    @Mock private lateinit var userPreference: UserPreference
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup(){
        mainViewModel = MainViewModel(userPreference,storyRepository)
    }

    @Test
    fun `when Get paging Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DummyData.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        val token ="token"
        expectedStory.value = data

        `when`(storyRepository.getStory(token)).thenReturn(expectedStory)

        val actualStory: PagingData<StoryEntity> = mainViewModel.story("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }


    @Test
    fun `when get account token not null and return success`(){
        val dummyUserData = DummyData.generateDummyUserDataLogin()
        val expectedToken = MutableLiveData<UserModel>()
        expectedToken.value = dummyUserData

        `when`(userPreference.getToken()).thenReturn(expectedToken.asFlow())

        val actualToken = mainViewModel.getToken().getOrAwaitValue()
        Mockito.verify(userPreference).getToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(dummyUserData, actualToken)
    }

    @Test
    fun `when logout from account and delete data account ensure logout method called`() = runTest{
        mainViewModel.logout()
        Mockito.verify (userPreference).logout()
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}