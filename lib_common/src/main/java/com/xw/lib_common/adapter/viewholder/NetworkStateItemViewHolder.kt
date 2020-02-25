package com.xw.lib_common.adapter.viewholder

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_common.R
import com.xw.lib_common.databinding.NetworkStateItemBinding
import com.xw.lib_common.ext.getString
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.isShow
import com.xw.lib_common.ext.show
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.Status

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class NetworkStateItemViewHolder(
    private val binding: NetworkStateItemBinding,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var animationDrawable: AnimationDrawable = binding.img.drawable as AnimationDrawable

    init {
        binding.root.setOnClickListener {
            if (binding.img.isShow().not())
                retryCallback()
        }
    }

    fun bindTo(networkState: NetworkState?) {
        if (networkState?.status == Status.RUNNING) {
            binding.img.show()
            start()
            binding.errorMsg.text = getString(R.string.loading)
        } else {
            stop()
            binding.img.gone()
            binding.errorMsg.text =
                networkState?.msg ?: getString(R.string.data_net_error_click_retry)
        }
    }

    private fun start() {
        if (animationDrawable.isRunning.not()) {
            animationDrawable.start()
        }
    }

    private fun stop() {
        if (animationDrawable.isRunning) {
            animationDrawable.stop()
        }
    }


    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val stateItemBinding =
                NetworkStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NetworkStateItemViewHolder(stateItemBinding, retryCallback)
        }
    }
}