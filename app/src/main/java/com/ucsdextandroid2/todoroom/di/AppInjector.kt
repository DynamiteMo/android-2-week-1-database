package com.ucsdextandroid2.todoroom.di

import android.content.Context
import com.ucsdextandroid2.todoroom.App

/**
 * Created by rjaylward on 4/4/20
 */

val Context.appDependencies get() = (applicationContext as App).appDependencies