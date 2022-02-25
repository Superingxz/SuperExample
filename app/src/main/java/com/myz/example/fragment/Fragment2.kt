package com.myz.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myz.example.R

/**
 * <pre>
 *     author: myz
 *     email : myz@huxijia.com
 *     time  : 2022/1/5 9:43
 *     desc  :
 * </pre>
 */
class Fragment2: Fragment() {
    fun newInstance(): Fragment2 {
        val args = Bundle()

        val fragment = Fragment2()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment2, container, false)
    }
}