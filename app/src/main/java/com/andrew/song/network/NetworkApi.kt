package com.andrew.song.network

import com.andrew.song.network.base.BaseNetworkApi

/**
 * 网络请求具体实现
 * 需要部署服务端：https://github.com/huannan/XArchServer
 */
object NetworkApi : BaseNetworkApi<INetworkService>("http://192.168.1.6:8080/") {

    suspend fun requestVideoDetail(id: String) = getResult {
        service.requestVideoDetail(id)
    }
}