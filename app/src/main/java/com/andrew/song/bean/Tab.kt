package com.andrew.song.bean

import com.andrew.song.base.BaseFragment
import com.andrew.song.constant.TabId
import kotlin.reflect.KClass

data class Tab(
    @TabId
    val id: String,
    val title: String,
    val icon: Int,
    val fragmentClz: KClass<out BaseFragment<*>>
)