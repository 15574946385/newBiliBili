package com.andrew.song.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.song.base.list.base.BaseItemViewDelegate
import com.andrew.song.databinding.ItemGoodsBinding

class GoodsViewDelegate : BaseItemViewDelegate<GoodsViewData, GoodsViewDelegate.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemGoodsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GoodsViewData) {
        super.onBindViewHolder(holder, item)
    }

    class ViewHolder(val viewBinding: ItemGoodsBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    }
}