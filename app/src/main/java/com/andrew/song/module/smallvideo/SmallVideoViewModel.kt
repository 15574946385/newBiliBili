package com.andrew.song.module.smallvideo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andrew.song.base.BaseViewModel
import com.andrew.song.bean.VideoBean
import com.andrew.song.constant.PageName
import com.andrew.song.network.NetworkApi
import com.andrew.song.util.d
import kotlinx.coroutines.launch

class SmallVideoViewModel : BaseViewModel() {

    val TAG = "SmallVideoViewModel"
    val helloWorldLiveData = MutableLiveData<Result<VideoBean>>()

    fun requestVideoDetail(id: String) {
        viewModelScope.launch {
            val result = NetworkApi.requestVideoDetail(id)
            d(TAG, "little song $result")
            helloWorldLiveData.value = result
        }
    }

    @PageName
    override fun getPageName() = PageName.SMALL_VIDEO
}