package com.example.apod_fetch.ui

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.apod_fetch.BuildConfig
import com.example.apod_fetch.data.ApodDatabase
import com.example.apod_fetch.data.ApodPhoto
import com.example.apod_fetch.data.ApodRepository
import com.example.apod_fetch.network.ApodApi
import com.example.apod_fetch.utils.DateUtils
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

/**
 * ViewModel for managing APOD (Astronomy Picture of the Day) data.
 * It interacts with the repository to fetch, update, and manage APOD data from the database and API.
 */
class ApodViewModel(private val apodRepository: ApodRepository) : ViewModel() {

    // State holding a single APOD photo.
    private val _apodsState: MutableLiveData<ApodPhoto?> = MutableLiveData()
    val apodsState: LiveData<ApodPhoto?> get() = _apodsState

    // State holding a list of APOD photos.
    private val _apodsListState: MutableLiveData<List<ApodPhoto>> = MutableLiveData()
    val apodsListState: LiveData<List<ApodPhoto>> get() = _apodsListState

    // UI state to hold the selected date range.
    private val _apodUiState = MutableLiveData(ApodUiState())
    var apodUiState: LiveData<ApodUiState> = _apodUiState

    // API service instance for network requests.
    private val apodApiService = ApodApi.retrofitService

    @SuppressLint("NewApi")
    val today = LocalDate.now() // Today's date.
    val formattedDate = DateUtils.formatDate(today) // Today's date formatted as a string.

    // Initialize ViewModel by inserting today's APOD photo into the database.
    init {
        insertPhotoOfTheDay(BuildConfig.NASA_API_KEY)
    }

    /**
     * Fetches a random list of APOD photos and inserts them into the database.
     * Updates the list state with the random photos.
     *
     * @param count Number of random photos to fetch.
     */
    fun getRandomPhotos(count: Int) {
        getAndInsertApods(BuildConfig.NASA_API_KEY, count)
        viewModelScope.launch {
            apodRepository.getRandomPhotos(count).collect { apods ->
                _apodsListState.value = apods
            }
        }
    }

    /**
     * Fetches all favorite APOD photos and updates the list state.
     */
    fun getFavouritePhotos() {
        viewModelScope.launch {
            apodRepository.getFavouritePhotos().collect { apods ->
                _apodsListState.value = apods
            }
        }
    }

    /**
     * Fetches APOD photos for a specified date range and updates the list state.
     *
     * @param startDate Start date of the range (inclusive).
     * @param endDate End date of the range (inclusive).
     */
    fun getApodsByRange(startDate: String, endDate: String) {
        fetchAndInsertApods(BuildConfig.NASA_API_KEY, startDate, endDate)
        viewModelScope.launch {
            apodRepository.getApodsByRange(startDate, endDate).collect { apods ->
                _apodsListState.value = apods
            }
        }
    }

    /**
     * Fetches today's APOD photo and updates the single photo state.
     */
    fun getApodPhotoOfTheDay() {
        insertPhotoOfTheDay(BuildConfig.NASA_API_KEY)
        viewModelScope.launch {
            apodRepository.getApodByDate(formattedDate).collect { apod ->
                _apodsState.value = apod
            }
        }
    }

    /**
     * Inserts today's APOD photo into the database by fetching it from the API.
     *
     * @param apiKey NASA API key.
     */
    fun insertPhotoOfTheDay(apiKey: String) {
        viewModelScope.launch {
            val apod = apodApiService.getPhotoOfTheDay(apiKey)
            insertPhoto(apod)
        }
    }

    /**
     * Saves a selected date range in the UI state.
     *
     * @param start Start date of the range.
     * @param end End date of the range.
     */
    fun saveSelectedDateRange(start: String, end: String) {
        viewModelScope.launch {
            _apodUiState.value = ApodUiState(startDate = start, endDate = end)
        }
    }

    /**
     * Fetches a specified number of random APOD photos from the API and inserts them into the database.
     *
     * @param apiKey NASA API key.
     * @param count Number of photos to fetch.
     */
    fun getAndInsertApods(apiKey: String, count: Int) {
        viewModelScope.launch {
            val apodList = apodApiService.getRandomPhotos(apiKey, count)
            if (apodList.isNotEmpty()) {
                insertApods(apodList)
            }
        }
    }

    /**
     * Fetches APOD photos for a specific date range from the API and inserts them into the database.
     *
     * @param apiKey NASA API key.
     * @param startDate Start date of the range.
     * @param endDate End date of the range.
     */
    fun fetchAndInsertApods(apiKey: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val apodList = apodApiService.getPhotosByDate(apiKey, startDate, endDate)
            if (apodList.isNotEmpty()) {
                insertApods(apodList)
            }
        }
    }

    /**
     * Updates the favorite status of an APOD photo and refreshes the list state.
     *
     * @param photo The APOD photo to update.
     */
    fun updateFavoriteStatus(photo: ApodPhoto) {
        viewModelScope.launch {
            apodRepository.updatePhoto(photo)
            refreshApodsList()
        }
    }

    /**
     * Refreshes the list of APOD photos by fetching all photos from the database.
     */
    private suspend fun refreshApodsList() {
        val currentPhotos = apodRepository.getAllApods()
        _apodsListState.postValue(currentPhotos)
    }

    /**
     * Inserts a single APOD photo into the database.
     *
     * @param Apod The APOD photo to insert.
     */
    fun insertPhoto(Apod: ApodPhoto) {
        viewModelScope.launch {
            apodRepository.insertPhoto(Apod)
        }
    }

    /**
     * Inserts a list of APOD photos into the database.
     *
     * @param listOfApods The list of APOD photos to insert.
     */
    fun insertApods(listOfApods: List<ApodPhoto>) {
        viewModelScope.launch {
            apodRepository.insertApods(listOfApods)
        }
    }
}
