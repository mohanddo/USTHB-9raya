package com.example.usthb9raya.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContributionViewModel: ViewModel() {
    var progress = MutableLiveData<Int>()
    var progressBarVisibility = MutableLiveData<Int>()
    var facultyText = MutableLiveData<String>()
    var typeText = MutableLiveData<String>()
    var contributeButtonVisibility = MutableLiveData<Int>()
    var addFileButtonText = MutableLiveData<String>()
    var enableAddFileButton = MutableLiveData<Boolean>()

    fun setProgress(value: Int) {
        progress.value = value
    }

    fun setProgressBarVisibility(visibility: Int) {
        progressBarVisibility.value = visibility
    }

    fun setFacultyText(faculty: String) {
        facultyText.value = faculty
    }

    fun setTypeText(type: String) {
        typeText.value = type
    }

    fun setContributeButtonVisibility(visibility: Int) {
        contributeButtonVisibility.value = visibility
    }

    fun setAddFileButtonText(text: String) {
        addFileButtonText.value = text
    }

    fun enableAddFileButton(isEnabled: Boolean) {
        enableAddFileButton.value = isEnabled
    }


}