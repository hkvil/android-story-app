package com.example.dicodingstoryapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.data.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val PAGE_SIZE = 10 // Adjust the page size as needed
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        return try {
            val response = apiService.getStories("Bearer $token", position)
            val dataList = response.body()?.listStory!!
            val nextKey = if (dataList.isNullOrEmpty()) null else position + 1
            val prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1
            LoadResult.Page(
                data = dataList,
                nextKey = nextKey,
                prevKey = prevKey
            )
        } catch (e: Exception) {
            // Handle exceptions here
            LoadResult.Error(e)
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}
