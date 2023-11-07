package id.co.binar.secondhand.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.co.binar.secondhand.data.remote.BuyerApi
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import retrofit2.HttpException
import java.io.IOException

class ProductPagingSource(
    private val buyerApi: BuyerApi,
    private val search: String?,
    private val category: Int?
) : PagingSource<Int, GetProductResponse>() {
    override fun getRefreshKey(state: PagingState<Int, GetProductResponse>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetProductResponse> {
        val position = params.key ?: 1
        return try {
            val response = buyerApi.getProduct(search = search, category = category, page = position)
            val items = response.body() ?: listOf()
            LoadResult.Page(
                data = items,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (items.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}