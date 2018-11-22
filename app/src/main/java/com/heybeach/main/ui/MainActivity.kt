package com.heybeach.main.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.heybeach.R
import com.heybeach.http.HttpParams
import com.heybeach.http.RequestMethod
import com.heybeach.http.executeHttpRequest
import com.heybeach.http.readResponseAndClose
import com.heybeach.main.data.BEACHES
import com.heybeach.main.data.MainModel
import com.heybeach.main.data.PROFILE
import com.heybeach.main.di.MainActivityInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        MainActivityInjector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.beaches -> viewModel.setSelectedFragment(BEACHES)
                R.id.profile -> viewModel.setSelectedFragment(PROFILE)
            }
            return@setOnNavigationItemSelectedListener true
        }

        setupViewModel()
        //testSignUp()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.mainModel.observe(this, Observer { mainModel ->
            setupFragments(mainModel)
        })
    }

    private fun setupFragments(mainModel: MainModel) {
        for (fragmentName in mainModel.fragmentNames) {
            val added = supportFragmentManager.findFragmentByTag(fragmentName)
            if (added == null) {
                val fragment = Fragment.instantiate(this, fragmentName, null)
                supportFragmentManager.transaction {
                    add(R.id.content, fragment, fragmentName)
                    setFragmentVisibility(fragment, mainModel)
                }
            } else {
                setFragmentVisibility(added, mainModel)
            }
        }
    }

    private fun setFragmentVisibility(fragment: Fragment, mainModel: MainModel) {
        supportFragmentManager.transaction {
            val fragmentName = fragment.javaClass.canonicalName!!
            if (fragmentName == mainModel.selectedFragmentName) {
                show(fragment)
            } else {
                hide(fragment)
            }

        }
    }

    private fun testSignUp() {
        val payload = JSONObject()
        payload.put("email", "tester@qwert.com")
        payload.put("password", "qwer1234")
        val params = HttpParams("user/register", RequestMethod.POST, body = payload.toString())

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val result = executeHttpRequest(params) {
                it.readResponseAndClose()
            }.await()
            Log.e("fikokurva", "signup " + result)
        }

    }

}
