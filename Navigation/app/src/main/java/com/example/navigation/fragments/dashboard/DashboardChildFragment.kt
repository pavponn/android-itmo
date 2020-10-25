package com.example.navigation.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigation.R
import com.example.navigation.extensions.navigate
import kotlinx.android.synthetic.main.child_fragment.*
import kotlinx.android.synthetic.main.child_fragment.view.*

class DashboardChildFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.child_fragment, container, false)

        view.child_button.setOnClickListener {
            navigate(
                DashboardChildFragmentDirections.actionDashboardChildToChild(
                    DashboardChildFragmentArgs
                        .fromBundle(requireArguments()).depth + 1)
            )
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        child_text.text = DashboardChildFragmentArgs
            .fromBundle(requireArguments()).depth
            .toString()
    }
}