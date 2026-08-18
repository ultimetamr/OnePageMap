package com.pico.swan.onepagemap.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.pico.swan.onepagemap.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
