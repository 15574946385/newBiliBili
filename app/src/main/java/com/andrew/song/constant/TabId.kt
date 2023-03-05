package com.andrew.song.constant

import androidx.annotation.StringDef

@StringDef(
    TabId.HOME,
    TabId.ACTIVE,
    TabId.PURCHASE,
    TabId.ACGN,
    TabId.SMALL_VIDEO,
    TabId.GOLD,
    TabId.MINE,
    TabId.DISCOVERY,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TabId {
    companion object {
        const val HOME = "home"
        const val ACTIVE = "active"
        const val PURCHASE = "purchase"
        const val ACGN = "acgn"
        const val SMALL_VIDEO = "small_video"
        const val GOLD = "gold"
        const val MINE = "mine"
        const val DISCOVERY = "discovery"
    }
}