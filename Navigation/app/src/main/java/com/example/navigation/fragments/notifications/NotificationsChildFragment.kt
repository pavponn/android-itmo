package com.example.navigation.fragments.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigation.R
import com.example.navigation.extensions.navigate
import com.example.navigation.fragments.dashboard.DashboardChildFragmentArgs
import com.example.navigation.fragments.dashboard.DashboardChildFragmentDirections
import com.example.navigation.fragments.home.HomeChildFragmentArgs
import com.example.navigation.fragments.home.HomeChildFragmentDirections
import kotlinx.android.synthetic.main.child_fragment.*
import kotlinx.android.synthetic.main.child_fragment.view.*

class NotificationsChildFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.child_fragment, container, false)
        view.child_button.setOnClickListener {
            navigate(
                NotificationsChildFragmentDirections.actionNotificationsChildToChild(
                    NotificationsChildFragmentArgs
                        .fromBundle(requireArguments()).depth + 1)
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        child_text.text = NotificationsChildFragmentArgs
            .fromBundle(requireArguments()).depth
            .toString()
    }
}