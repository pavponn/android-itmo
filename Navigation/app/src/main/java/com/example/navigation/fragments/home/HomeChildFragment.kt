package com.example.navigation.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigation.R
import com.example.navigation.extensions.navigate
import kotlinx.android.synthetic.main.child_fragment.*
import kotlinx.android.synthetic.main.child_fragment.view.*

class HomeChildFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.child_fragment, container, false)
        view.child_button.setOnClickListener {
            navigate(
                HomeChildFragmentDirections.actionHomeChildToChild(
                    HomeChildFragmentArgs
                    .fromBundle(requireArguments()).depth + 1)
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        child_text.text = HomeChildFragmentArgs
            .fromBundle(requireArguments()).depth
            .toString()
    }
}