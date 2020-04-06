package com.ucsdextandroid2.todoroom.di

import android.content.Context
import com.ucsdextandroid2.todoroom.database.AppDatabase
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by rjaylward on 4/4/20
 */

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    val database: AppDatabase

}

@Module
object AppModule {

    @Singleton
    @JvmStatic
    @Provides
    fun providesAppDatabase(context: Context) = AppDatabase.buildDatabase(context)

}