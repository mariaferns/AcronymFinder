package com.albertsons.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.albertsons.myapplication.repository.AbbreviationDefinitionsRepository

class AbbreviationViewModel(private val mDefinitionsRepository: AbbreviationDefinitionsRepository) : ViewModel(){

    fun getAllAcronyms(shortForm: String) = liveData {
        try{
            //emit(mDefinitionsRepository.getAbbreviationDefinations)

        } catch (e:Exception){
            emit( e.message.toString())
        }
    }

}