package com.andrew.song.base.list

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrew.song.R
import com.andrew.song.base.list.base.BaseRecyclerViewModel
import com.andrew.song.base.list.base.BaseViewData
import com.andrew.song.base.list.loadmore.LoadMoreAdapter
import com.andrew.song.base.list.loadmore.LoadMoreRecyclerView
import com.andrew.song.base.list.pullrefresh.PullRefreshLayout
import com.andrew.song.bean.LoadError
import com.andrew.song.bean.exception.GlobalException
import com.andrew.song.constant.LoadMoreState
import com.andrew.song.constant.PageState
import com.andrew.song.databinding.ViewXRecyclerBinding
import com.andrew.song.item.LoadMoreViewData
import com.andrew.song.util.NetworkHelper

class XRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val viewBinding = ViewXRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
    private var activity: AppCompatActivity = context as AppCompatActivity
    private lateinit var config: Config
    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentPageState = PageState.NORMAL
    private val retryOnClickListener by lazy {
        OnClickListener {
            loadData(isLoadMore = false, isReLoad = true, showLoading = true)
        }
    }
    private val showLoadingRunnable by lazy {
        Runnable {
            viewBinding.refreshLayout.visibility = View.GONE
            viewBinding.promptView.showLoading()
        }
    }
    private var interceptTouchEvent = false
    private val resumeTouchRunnable by lazy {
        Runnable {
            interceptTouchEvent = false
        }

    }

    companion object {
        const val DELAY_SHOW_LOADING = 500L
        const val DELAY_RESUME_TOUCH_EVENT = 500L
    }

    fun init(config: Config) {
        config.check(context)
        this.config = config
        initView()
        initData()
    }

    private fun initView() {
        // ??????RecyclerView
        viewBinding.loadMoreRecyclerView.run {
            // ?????????
            isVerticalScrollBarEnabled = config.showScrollBar
            // LayoutManager
            layoutManager = config.layoutManager
            // ItemDecoration
            config.itemDecoration?.let {
                addItemDecoration(it)
            }
            // ItemAnimator
            config.itemAnimator?.let {
                itemAnimator = it
            }
            // Adapter
            adapter = config.adapter
        }
    }

    private fun initData() {
        // ????????????(?????????)??????????????????
        config.viewModel.firstViewDataLiveData.observe(activity) { viewData ->
            // ?????????????????????????????????????????????
            interceptTouchEventTemporarily()
            viewBinding.loadMoreRecyclerView.scrollToPosition(0)
            // ????????????????????????
            viewBinding.refreshLayout.run {
                if (isRefreshing) {
                    refreshComplete()
                }
            }
            viewBinding.loadMoreRecyclerView.resetLoadMoreListener()
            if (viewData === LoadError) {
                // ????????????????????????????????????????????????????????????
                viewBinding.loadMoreRecyclerView.setCanLoadMore(false)
                if (NetworkHelper.isNetworkConnect()) {
                    showPageState(PageState.LOAD_ERROR)
                } else {
                    showPageState(PageState.NO_NETWORK)
                }
            } else if (viewData.isEmpty()) {
                // ?????????????????????????????????
                viewBinding.loadMoreRecyclerView.setCanLoadMore(false)
                showPageState(PageState.EMPTY)
            } else {
                // ?????????????????????????????????Adapter
                config.adapter.setViewData(viewData)
                if (config.pullUploadMoreEnable) {
                    viewBinding.loadMoreRecyclerView.setCanLoadMore(true)
                    config.adapter.setLoadMoreState(LoadMoreState.LOADING)
                } else {
                    viewBinding.loadMoreRecyclerView.setCanLoadMore(false)
                    config.adapter.setLoadMoreState(LoadMoreState.GONE)
                }
                showPageState(PageState.NORMAL)
            }
        }

        // ??????????????????
        viewBinding.refreshLayout.isEnabled = config.pullRefreshEnable
        if (config.pullRefreshEnable) {
            viewBinding.refreshLayout.setOnPullRefreshListener(object : PullRefreshLayout.OnPullRefreshListener {
                override fun onRefreshBegin() {
                    loadData(isLoadMore = false, isReLoad = false)
                }
            })
        }

        if (config.pullUploadMoreEnable) {
            // ?????????????????????????????????????????????
            interceptTouchEventTemporarily()
            // ??????????????????????????????
            config.viewModel.moreViewDataLiveData.observe(activity) { viewData ->
                if (viewData === LoadError) {
                    // ????????????????????????????????????????????????????????????
                    viewBinding.loadMoreRecyclerView.setCanLoadMore(false)
                    if (NetworkHelper.isNetworkConnect()) {
                        config.adapter.setLoadMoreState(LoadMoreState.ERROR)
                    } else {
                        config.adapter.setLoadMoreState(LoadMoreState.NO_NETWORK)
                    }
                } else if (viewData.isEmpty()) {
                    // ????????????????????????????????????
                    viewBinding.loadMoreRecyclerView.setCanLoadMore(false)
                    if (config.viewModel.getCurrentPage() == 1) {
                        // ???????????????????????????????????????????????????????????????
                        config.adapter.setLoadMoreState(LoadMoreState.GONE)
                    } else {
                        config.adapter.setLoadMoreState(LoadMoreState.NO_MORE)
                    }
                } else {
                    // ????????????????????????Adapter????????????
                    viewBinding.loadMoreRecyclerView.setCanLoadMore(true)
                    config.adapter.setLoadMoreState(LoadMoreState.GONE)
                    config.adapter.addViewData(viewData)
                    config.adapter.setLoadMoreState(LoadMoreState.LOADING)
                }
            }
            // ??????????????????
            viewBinding.loadMoreRecyclerView.setOnLoadMoreListener(object : LoadMoreRecyclerView.OnLoadMoreListener {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    loadData(isLoadMore = true, isReLoad = false)
                }
            })
        }

        // ??????????????????
        NetworkHelper.observerNetworkState(activity) { connect: Boolean ->
            if (!isAttachedToWindow || !config.viewModel.needNetwork()) {
                return@observerNetworkState
            }
            if (connect) {
                // ??????????????????????????????????????????????????????????????????
                if (currentPageState == PageState.LOAD_ERROR || currentPageState == PageState.NO_NETWORK) {
                    loadData(isLoadMore = false, isReLoad = true, showLoading = true)
                } else if (currentPageState == PageState.NORMAL && (config.adapter.getLoadMoreState() == LoadMoreState.ERROR || config.adapter.getLoadMoreState() == LoadMoreState.NO_NETWORK)) {
                    config.adapter.setLoadMoreState(LoadMoreState.LOADING)
                    loadData(isLoadMore = true, isReLoad = true, showLoading = false)
                }
            } else {
                // ??????????????????????????????????????????????????????
                if (currentPageState == PageState.LOAD_ERROR || currentPageState == PageState.LOADING) {
                    showPageState(PageState.NO_NETWORK)
                } else if (currentPageState == PageState.NORMAL && config.adapter.getLoadMoreState() == LoadMoreState.ERROR) {
                    config.adapter.setLoadMoreState(LoadMoreState.NO_NETWORK)
                }
            }
        }

        // ??????????????????
        loadData(isLoadMore = false, isReLoad = false, showLoading = true)
    }

    private fun interceptTouchEventTemporarily() {
        interceptTouchEvent = true
        mainHandler.postDelayed(resumeTouchRunnable, DELAY_RESUME_TOUCH_EVENT)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (interceptTouchEvent) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onDetachedFromWindow() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }

    fun showPageState(@PageState pageState: Int) {
        currentPageState = pageState
        mainHandler.removeCallbacks(showLoadingRunnable)
        when (currentPageState) {
            PageState.NORMAL -> {
                viewBinding.refreshLayout.visibility = View.VISIBLE
                viewBinding.promptView.hide()
            }
            PageState.LOADING -> {
                // ??????????????????500????????????
                mainHandler.postDelayed(showLoadingRunnable, DELAY_SHOW_LOADING)
            }
            PageState.LOAD_ERROR -> {
                viewBinding.refreshLayout.visibility = View.GONE
                viewBinding.promptView.showNetworkError(retryOnClickListener)
            }
            PageState.NO_NETWORK -> {
                viewBinding.refreshLayout.visibility = View.GONE
                viewBinding.promptView.showNoNetwork()
            }
            PageState.EMPTY -> {
                viewBinding.refreshLayout.visibility = View.GONE
                viewBinding.promptView.showEmpty(config.emptyIcon, config.emptyMessage)
            }
        }
    }

    private fun loadData(isLoadMore: Boolean, isReLoad: Boolean, showLoading: Boolean = false) {
        if (showLoading) {
            showPageState(PageState.LOADING)
        }
        config.viewModel.loadDataInternal(isLoadMore, isReLoad)
    }

    /**
     * ????????????????????????
     */
    fun startRefresh() {
        viewBinding.refreshLayout.autoRefresh()
    }

    /**
     * ??????????????????
     */
    fun refreshList() {
        loadData(isLoadMore = false, isReLoad = true)
    }

    fun removeData(position: Int) {
        val removedViewData = config.adapter.removeViewData(position)
        if (null != removedViewData) {
            config.onItemDeleteListener?.onItemDelete(viewBinding.loadMoreRecyclerView, mutableListOf(removedViewData))
        }
    }

    fun removeData(viewData: BaseViewData<*>) {
        val removedViewData = config.adapter.removeViewData(viewData)
        if (null != removedViewData) {
            config.onItemDeleteListener?.onItemDelete(viewBinding.loadMoreRecyclerView, mutableListOf(removedViewData))
        }
    }

    fun performItemClick(view: View, viewData: BaseViewData<*>, position: Int, id: Long) {
        // ????????????(???????????????item????????????)
        if (viewData is LoadMoreViewData) {
            // ????????????item??????
            when (config.adapter.getLoadMoreState()) {
                LoadMoreState.ERROR -> {
                    // ????????????????????????????????????????????????????????????
                    config.adapter.setLoadMoreState(LoadMoreState.LOADING)
                    loadData(isLoadMore = true, isReLoad = true)
                }
                LoadMoreState.NO_NETWORK -> {
                    // ???????????????????????????
                    NetworkHelper.toNetworkSetting(context)
                }
            }
        } else {
            // ??????item??????
            config.onItemClickListener?.onItemClick(viewBinding.loadMoreRecyclerView, view, viewData, position, id)
        }
    }

    fun performItemLongClick(view: View, viewData: BaseViewData<*>, position: Int, id: Long): Boolean {
        // ????????????
        var consumed = false
        if (viewData !is LoadMoreViewData) {
            consumed = config.onItemLongClickListener?.onItemLongClick(viewBinding.loadMoreRecyclerView, view, viewData, position, id) ?: false
        }
        return consumed
    }

    fun performItemChildViewClick(view: View, viewData: BaseViewData<*>, position: Int, id: Long, extra: Any?) {
        config.onItemSubViewClickListener?.onItemChildViewClick(viewBinding.loadMoreRecyclerView, view, viewData, position, id, extra)
    }

    interface OnItemClickListener {
        fun onItemClick(parent: RecyclerView, view: View, viewData: BaseViewData<*>, position: Int, id: Long)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(parent: RecyclerView, view: View, viewData: BaseViewData<*>, position: Int, id: Long): Boolean
    }

    interface OnItemChildViewClickListener {
        fun onItemChildViewClick(parent: RecyclerView, view: View, viewData: BaseViewData<*>, position: Int, id: Long, extra: Any?)
    }

    interface OnItemDeleteListener {
        fun onItemDelete(parent: RecyclerView, viewData: List<BaseViewData<*>>)
    }

    class Config {

        lateinit var viewModel: BaseRecyclerViewModel
        lateinit var adapter: LoadMoreAdapter
        lateinit var layoutManager: RecyclerView.LayoutManager
        var itemDecoration: RecyclerView.ItemDecoration? = null
        var itemAnimator: RecyclerView.ItemAnimator? = null
        var pullRefreshEnable = true
        var pullUploadMoreEnable = true
        var showScrollBar = true

        // ???????????????
        var emptyMessage: String = ""

        // ???????????????
        @DrawableRes
        var emptyIcon: Int = -1
        var onItemClickListener: OnItemClickListener? = null
        var onItemLongClickListener: OnItemLongClickListener? = null
        var onItemSubViewClickListener: OnItemChildViewClickListener? = null
        var onItemDeleteListener: OnItemDeleteListener? = null

        fun setViewModel(viewModel: BaseRecyclerViewModel): Config {
            this.viewModel = viewModel
            return this
        }

        fun setAdapter(adapter: LoadMoreAdapter): Config {
            this.adapter = adapter
            return this
        }

        fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): Config {
            this.layoutManager = layoutManager
            return this
        }

        fun setItemDecoration(itemDecoration: RecyclerView.ItemDecoration): Config {
            this.itemDecoration = itemDecoration
            return this
        }

        fun setItemAnimator(itemAnimator: RecyclerView.ItemAnimator): Config {
            this.itemAnimator = itemAnimator
            return this
        }

        fun setPullRefreshEnable(pullRefreshEnable: Boolean): Config {
            this.pullRefreshEnable = pullRefreshEnable
            return this
        }

        fun setPullUploadMoreEnable(pullUploadMoreEnable: Boolean): Config {
            this.pullUploadMoreEnable = pullUploadMoreEnable
            return this
        }

        fun setShowScrollBar(showScrollBar: Boolean): Config {
            this.showScrollBar = showScrollBar
            return this
        }

        fun setEmptyMessage(message: String): Config {
            this.emptyMessage = message
            return this
        }

        fun setEmptyIcon(@DrawableRes icon: Int): Config {
            this.emptyIcon = icon
            return this
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener): Config {
            this.onItemClickListener = onItemClickListener
            return this
        }

        fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener): Config {
            this.onItemLongClickListener = onItemLongClickListener
            return this
        }

        fun setOnItemChildViewClickListener(onItemSubViewClickListener: OnItemChildViewClickListener): Config {
            this.onItemSubViewClickListener = onItemSubViewClickListener
            return this
        }

        fun setOnItemDeleteListener(onItemDeleteListener: OnItemDeleteListener): Config {
            this.onItemDeleteListener = onItemDeleteListener
            return this
        }

        fun check(context: Context) {
            if (!::viewModel.isInitialized) {
                throw GlobalException.of("you should set a ViewModel")
            }
            if (!::adapter.isInitialized) {
                adapter = LoadMoreAdapter()
            }
            if (!::layoutManager.isInitialized) {
                layoutManager = LinearLayoutManager(context)
            }
            if (TextUtils.isEmpty(emptyMessage)) {
                emptyMessage = context.resources.getString(R.string.page_state_empty)
            }
            if (emptyIcon == -1) {
                emptyIcon = R.drawable.icon_empty
            }
        }
    }
}