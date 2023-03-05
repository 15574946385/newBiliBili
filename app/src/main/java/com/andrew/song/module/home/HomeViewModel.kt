package com.andrew.song.module.home

import androidx.lifecycle.viewModelScope
import com.andrew.song.base.list.base.BaseRecyclerViewModel
import com.andrew.song.base.list.base.BaseViewData
import com.andrew.song.bean.BannerBean
import com.andrew.song.bean.VideoBean
import com.andrew.song.constant.PageName
import com.andrew.song.constant.VideoType
import com.andrew.song.item.BannerViewData
import com.andrew.song.item.LargeVideoViewData
import com.andrew.song.item.VideoViewData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : BaseRecyclerViewModel() {

    override fun loadData(isLoadMore: Boolean, isReLoad: Boolean, page: Int) {
        viewModelScope.launch {
            delay(1000L)
            val viewDataList = mutableListOf<BaseViewData<*>>()
            if (!isLoadMore) {
                viewDataList.add(
                    BannerViewData(
                        BannerBean(
                            listOf(
                                "https://pic2.zhimg.com/v2-848ed6d4e1c845b128d2ec719a39b275_b.jpg",
                                "https://pic2.zhimg.com/80/v2-40c024ce464642fcab3bbf1b0a233174_hd.jpg",
                                "https://pic2.zhimg.com/v2-848ed6d4e1c845b128d2ec719a39b275_b.jpg"
                            )
                        )
                    )
                )
                for (i in 0..10) {
                    if (i != 0 && i % 6 == 0) {
                        viewDataList.add(LargeVideoViewData(VideoBean("aaa", "我是标题", "xxx", "aaa", "up", 10000L, VideoType.LARGE)))
                    } else {
                        viewDataList.add(VideoViewData(VideoBean("aaa", "我是标题", "xxx", "aaa", "up", 10000L, VideoType.NORMAL)))
                    }
                }
                postData(isLoadMore, viewDataList)
            } else {
                for (i in 0..10) {
                    if (i != 0 && i % 6 == 0) {
                        viewDataList.add(LargeVideoViewData(VideoBean("aaa", "我是标题", "xxx", "aaa", "up", 10000L, VideoType.LARGE)))
                    } else {
                        viewDataList.add(VideoViewData(VideoBean("aaa", "我是标题", "xxx", "aaa", "up", 10000L, VideoType.NORMAL)))
                    }
                }
                postData(isLoadMore, viewDataList)
            }
        }
    }

    @PageName
    override fun getPageName() = PageName.HOME
}