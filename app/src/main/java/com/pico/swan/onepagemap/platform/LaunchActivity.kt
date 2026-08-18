package com.pico.swan.onepagemap.platform

import android.os.Bundle
import com.pico.spatial.ui.platform.stub.SpatialLaunchActivity

object DemoLaunch {
    var mode: String? = null
}

class LaunchActivity : SpatialLaunchActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DemoLaunch.mode = intent?.getStringExtra("demo_mode")
        super.onCreate(savedInstanceState)
    }
}
