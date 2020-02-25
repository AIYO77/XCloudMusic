package com.xmusic.module_login.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.xmusic.module_login.R
import kotlinx.android.synthetic.main.fragment_select_login_mode.*

class SelectLoginModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_login_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPhone.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.login_mode_fragment) {
                val directions =
                    SelectLoginModeFragmentDirections.actionSelectLoginToPhoneLoginFragment()
                findNavController().navigate(directions)
            }

        }

        loginEmail.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.login_mode_fragment) {
                val directions =
                    SelectLoginModeFragmentDirections.actionSelectLoginToEmailLoginFragment()
                findNavController().navigate(directions)
            }
        }


    }
}
