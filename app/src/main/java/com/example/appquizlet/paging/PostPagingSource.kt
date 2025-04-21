package com.example.appquizlet.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.newfeature.Post

/*
* Truyền apiService vào PostPagingSource là đúng ✅
PagingSource chịu trách nhiệm tải dữ liệu từ API, nên nó cần gọi trực tiếp apiService.
Repository sẽ quản lý PagingSource, không cần truyền chính nó vào PostPagingSource.
* */
class PostPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val response = apiService.getPosts(page, pageSize)
            val posts = response.body() ?: emptyList()


            LoadResult.Page(
                data = posts,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (posts.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
