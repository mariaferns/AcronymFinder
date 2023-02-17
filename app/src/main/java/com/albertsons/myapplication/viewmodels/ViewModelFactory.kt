package com.albertsons.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.albertsons.myapplication.repository.AbbreviationDefinitionsRepository

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AbbreviationViewModel::class.java)){
            return AbbreviationViewModel(AbbreviationDefinitionsRepository()) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}