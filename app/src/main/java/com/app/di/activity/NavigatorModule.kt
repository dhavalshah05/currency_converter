package com.app.di.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.app.services.navigator.AppNavigator
import com.app.services.navigator.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class NavigatorModule {

    @Provides
    @ActivityScoped
    fun provideNavigator(activity: Activity): Navigator {
        val appCompatActivity = (activity as AppCompatActivity)
        return AppNavigator(appCompatActivity)
    }

}