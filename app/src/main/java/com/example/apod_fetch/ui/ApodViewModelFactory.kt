package com.example.apod_fetch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apod_fetch.data.ApodRepository

class ApodViewModelFactory(
    private val apodRepository: ApodRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
            return ApodViewModel(apodRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}