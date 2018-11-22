package com.heybeach.beaches.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.heybeach.R
import com.heybeach.beaches.di.BeachesFragmentInjector
import com.heybeach.http.Response
import kotlinx.android.synthetic.main.fragment_beaches.*

class BeachesFragment : Fragment() {

    lateinit var beachesViewModelFactory: BeachesViewModelFactory
    lateinit var beachesAdapter: BeachesAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var viewModel: BeachesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_beaches, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel = ViewModelProviders.of(this, beachesViewModelFactory).get(BeachesViewModel::class.java)
        viewModel.beaches.observe(this, Observer {
            when (it) {
                is Response.Success -> beachesAdapter.beaches = it.data
                is Response.Error -> Log.e("API ERROR", it.exception.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            }
            addItemDecoration(dividerItemDecoration)

            layoutManager = this@BeachesFragment.layoutManager
            adapter = beachesAdapter
        }
    }

    override fun onAttach(context: Context?) {
        BeachesFragmentInjector.inject(this)
        super.onAttach(context)
    }

}