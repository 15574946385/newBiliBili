package com.andrew.song.item

import com.andrew.song.base.list.base.BaseViewData
import com.andrew.song.constant.LoadMoreState

class LoadMoreViewData(@LoadMoreState loadMoreState: Int) : BaseViewData<Int>(loadMoreState) {
}