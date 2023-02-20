package com.andrew.song.network

import com.andrew.song.bean.VideoBean
import com.andrew.song.network.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface INetworkService {

    @GET("videodetail")
    suspend fun requestVideoDetail(@Query("id") id: String): BaseResponse<VideoBean>
}