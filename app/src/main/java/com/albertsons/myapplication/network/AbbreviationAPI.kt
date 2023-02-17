package com.albertsons.myapplication.network

import com.albertsons.myapplication.api.models.Abbreviation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AbbreviationAPI {
    @GET("/software/acromine/dictionary.py")
    fun abbreviations(@Query("sf") ids: String?): Call<Abbreviation?>?
}
