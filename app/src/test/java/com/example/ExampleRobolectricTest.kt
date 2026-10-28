package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AodConfigurationState
import com.example.model.AppFilterMode
import com.example.model.IslandAnchorPreset
import com.example.model.IslandShapeStyle
import com.example.model.IslandTapAction
import com.example.model.IslandVisibilityMode
import com.example.service.LuminaPermissionHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

    @Test
    fun `verify app name and v3 island weather and permission models`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lumina AOD", appName)

        // Verify permission status check works cleanly
        val permissionStatus = LuminaPermissionHelper.checkPermissions(context)
        assertNotNull(permissionStatus)

        // Verify all 11 Island shapes exist
        assertEquals(11, IslandShapeStyle.entries.size)

        // Verify v3.0 AodConfigurationState defaults
        val defaultState = AodConfigurationState()
        assertEquals(IslandShapeStyle.SPLIT_DROP, defaultState.islandShape)
        assertEquals(IslandAnchorPreset.TOP_CENTER, defaultState.islandAnchor)
        assertEquals(IslandVisibilityMode.ALWAYS_VISIBLE, defaultState.islandVisibilityMode)
        assertEquals(IslandTapAction.EXPAND_FIRST_THEN_OPEN_APP, defaultState.islandTapAction)
        assertEquals(AppFilterMode.HYBRID_SMART, defaultState.appFilterMode)
        assertTrue(defaultState.isIslandFreeDragEnabled)
        assertTrue(defaultState.islandMarqueeEnabled)
        assertTrue(defaultState.liquidMetaballMorphEnabled)
    }
}
