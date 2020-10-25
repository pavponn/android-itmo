package com.example.navigation.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigation.R
import com.example.navigation.extensions.navigate
import com.example.navigation.fragments.dashboard.DashboardFragmentDirections
import kotlinx.android.synthetic.main.root_fragment.view.*

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.root_fragment, container, false)
        view.root_button.setOnClickListener {
            navigate(HomeFragmentDirections.actionHomeRootToChild(1))
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}