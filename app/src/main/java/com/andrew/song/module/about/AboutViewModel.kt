package com.andrew.song.module.about

import com.andrew.song.base.BaseViewModel
import com.andrew.song.constant.PageName

class AboutViewModel : BaseViewModel() {

    @PageName
    override fun getPageName() = PageName.ABOUT
}