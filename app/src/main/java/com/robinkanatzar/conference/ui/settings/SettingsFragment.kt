package com.robinkanatzar.conference.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import kotlinx.android.synthetic.main.settings_fragment.view.*

class SettingsFragment : Fragment(), Injectable {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.settings_fragment, null)
        v.cl_settings_login.setOnClickListener { onLoginClicked() }

        return v
    }

    private fun onLoginClicked() {
        val action = SettingsFragmentDirections.nextActionLogin(null)
        findNavController().navigate(action)
    }
}