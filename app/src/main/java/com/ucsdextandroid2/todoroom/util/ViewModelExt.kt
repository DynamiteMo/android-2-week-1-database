package com.ucsdextandroid2.todoroom.util

import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by rjaylward on 4/5/20
 */

@Suppress("UNCHECKED_CAST")
@MainThread
inline fun <reified T : ViewModel> FragmentActivity.injectViewModel(crossinline factory: () -> T) = viewModels<T> {
    return@viewModels object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = factory() as T
    }
}

@Suppress("UNCHECKED_CAST")
@MainThread
inline fun <reified T : ViewModel> Fragment.injectViewModel(crossinline factory: () -> T) = viewModels<T> {
    return@viewModels object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = factory() as T
    }
}

@Suppress("UNCHECKED_CAST")
@MainThread
inline fun <reified T : ViewModel> Fragment.injectActivityViewModel(crossinline factory: () -> T) = activityViewModels<T> {
    return@activityViewModels object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = factory() as T
    }
}