package com.xw.lib_common.play

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.xw.lib_common.R
import com.xw.lib_common.ext.gone
import com.xw.lib_common.utils.GlideUtils

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class RoundFragment : Fragment() {
    private var animator: ObjectAnimator? = null

    companion object {
        private const val ALBUM_PATH = "path"
        fun newInstance(albumPath: String?): RoundFragment {
            val roundFragment = RoundFragment()
            roundFragment.arguments = Bundle().apply { putString(ALBUM_PATH, albumPath) }
            return roundFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_roundimage, container, false)

        val string = arguments?.getString(ALBUM_PATH)
        val img = view.findViewById<AppCompatImageView>(R.id.image)
        if (string.isNullOrEmpty() || context == null) {
            img?.setImageResource(R.drawable.placeholder_disk_play_song)
        } else {
            GlideUtils.loadImageCircleCrop(
                context!!,
                img,
                string,
                171,
                171,
                R.drawable.placeholder_disk_play_program
            )
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
            .apply {
                repeatCount = Integer.MAX_VALUE
                duration = 25000L
                interpolator = LinearInterpolator()
            }

        view?.apply { setTag(R.id.tag_animator, animator); }
    }

    override fun onPause() {
        super.onPause()
        animator?.pause()
    }

    override fun onResume() {
        super.onResume()
        animator?.resume()

    }

    override fun onDestroy() {
        if (animator != null) {
            animator = null
        }
        super.onDestroy()
    }


}