package com.andrew.song.module.main

import com.andrew.song.base.BaseViewModel
import com.andrew.song.constant.PageName

class MainViewModel : BaseViewModel() {

    @PageName
    override fun getPageName() = PageName.HOME
}