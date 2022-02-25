package com.myz.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.myz.example.fragment.Fragment1
import com.myz.example.fragment.Fragment2
import kotlinx.android.synthetic.main.activity_fragment_mamager.*

/**
 * <pre>
 *     author: myz
 *     email : myz@huxijia.com
 *     time  : 2022/1/5 9:53
 *     desc  :
 * </pre>
 */
class FragmentManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_mamager)
        switchFragment(Fragment1())

        tv_left.setOnClickListener {
            switchFragment(Fragment1())
        }

        tv_right.setOnClickListener {
            switchFragment(Fragment2())
        }
    }

    private fun replaceToFragment(targetFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.ll_frag_layout, targetFragment)
        fragmentTransaction.commit()
    }

    private var currentFragment: Fragment = Fragment1()
    private fun switchFragment(targetFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (!targetFragment.isAdded) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            fragmentTransaction.hide(currentFragment).add(
                R.id.ll_frag_layout,
                targetFragment,
                targetFragment.javaClass.name
            )
        } else {
            fragmentTransaction.hide(currentFragment).show(targetFragment)
        }
        currentFragment = targetFragment
        fragmentTransaction.commit()
    }
}