package com.ucsdextandroid2.todoroom

import android.app.Application
import com.ucsdextandroid2.todoroom.di.AppComponent
import com.ucsdextandroid2.todoroom.di.DaggerAppComponent

/**
 * Created by rjaylward on 4/4/20
 */

class App : Application() {

    val appDependencies: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

}