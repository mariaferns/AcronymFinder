package com.albertsons.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.albertsons.myapplication.adapter.FullFormAdapter
import com.albertsons.myapplication.api.models.Abbreviation
import com.albertsons.myapplication.api.models.Lf
import com.albertsons.myapplication.databinding.AcronymsLayoutBinding
import com.albertsons.myapplication.network.AbbreviationAPI
import com.albertsons.myapplication.utils.Constants
import com.albertsons.myapplication.viewmodels.AbbreviationViewModel
import com.albertsons.myapplication.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var fullFormAdapter: FullFormAdapter
    private lateinit var fullForms: List<Lf>
    private lateinit var mAbbreviationAPI: AbbreviationAPI
    private lateinit var mAbbreviationViewModel: AbbreviationViewModel
    private lateinit var homeBinding: AcronymsLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = DataBindingUtil.setContentView(this, R.layout.acronyms_layout)

        mAbbreviationViewModel = ViewModelProvider(
            this,
            ViewModelFactory()
        )[AbbreviationViewModel::class.java]

        val recyclerViewAcronyms = homeBinding.rvAcronyms
        fullFormAdapter = FullFormAdapter(emptyList())
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerViewAcronyms.adapter = fullFormAdapter
        recyclerViewAcronyms.layoutManager = layoutManager
        recyclerViewAcronyms.isNestedScrollingEnabled = false
        recyclerViewAcronyms.setHasFixedSize(false)

        homeBinding.button.setOnClickListener {
            if(homeBinding.editText.text.toString().isEmpty()) {
                homeBinding.editText.error = getString(R.string.empty_search_error)
            } else {
                getFullFormsForAcronyms(homeBinding.editText.text.toString())
            }
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    private fun getFullFormsForAcronyms(edit_query: String) {

        mAbbreviationAPI = retrofit.create(AbbreviationAPI::class.java)

        val call: Call<Abbreviation?>? = mAbbreviationAPI.abbreviations(edit_query)
        fullForms = emptyList()

        call?.enqueue(object : Callback<Abbreviation?> {
            override fun onResponse(call: Call<Abbreviation?>, response: Response<Abbreviation?>) {
                if (response.isSuccessful) {
                    Log.d("Main", "success!" + response.body().toString())
                    response.body()?.forEach {
                        fullForms = it.lfs
                    }
                    if(fullForms.isEmpty()) {
                        homeBinding.emptyDataParent.visibility = View.VISIBLE
                        homeBinding.rvAcronyms.visibility = View.GONE
                    } else {
                        homeBinding.emptyDataParent.visibility = View.GONE
                        homeBinding.rvAcronyms.visibility = View.VISIBLE
                    }
                    generateFullFormList(fullForms)
                }
            }

            override fun onFailure(call: Call<Abbreviation?>, t: Throwable) {
                disconnected()
                Log.e("Main", "Failed getting dataset " + t.message.toString())
            }
        })
    }

    private fun generateFullFormList(fullForms: List<Lf>) {
        fullFormAdapter = FullFormAdapter(fullForms)
        homeBinding.rvAcronyms.adapter = fullFormAdapter
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                .EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    private fun disconnected() {
        val snackbar = Snackbar
            .make(homeBinding.constraintLayout, R.string.network_issues_error, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun connected() {
        homeBinding.rvAcronyms.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }
}
