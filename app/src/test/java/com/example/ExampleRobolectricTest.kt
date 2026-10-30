// File: app/src/test/java/com/example/ExampleRobolectricTest.kt
package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LuminaDatabase
import com.example.data.LuminaRepository
import com.example.model.AodColorPalette
import com.example.model.AodConfigurationState
import com.example.model.AppFilterMode
import com.example.model.ClockFaceStyle
import com.example.model.IslandAnchorPreset
import com.example.model.IslandBubbleExpandMode
import com.example.model.IslandEdgeSnapMode
import com.example.model.IslandEntranceAnimation
import com.example.model.IslandShapeStyle
import com.example.model.IslandTapAction
import com.example.model.IslandVisibilityMode
import com.example.model.SmartProfilePreset
import com.example.service.LuminaPermissionHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `verify app name and v4 island bubble edge snap animations and granular toggles`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lumina AOD", appName)

        // Verify permission status check works cleanly
        val permissionStatus = LuminaPermissionHelper.checkPermissions(context)
        assertNotNull(permissionStatus)

        // Verify all 12 Island shapes (including FLOATING_BUBBLE), 9 entrance animations, 4 edge snap modes, 3 bubble expand modes, and 5 Smart Profiles exist
        assertEquals(12, IslandShapeStyle.entries.size)
        assertEquals(9, IslandEntranceAnimation.entries.size)
        assertEquals(4, IslandEdgeSnapMode.entries.size)
        assertEquals(3, IslandBubbleExpandMode.entries.size)
        assertEquals(5, SmartProfilePreset.entries.size)

        // Verify v4.0 AodConfigurationState defaults and granular toggles
        val defaultState = AodConfigurationState()
        assertEquals(IslandShapeStyle.SPLIT_DROP, defaultState.islandShape)
        assertEquals(IslandAnchorPreset.TOP_CENTER, defaultState.islandAnchor)
        assertEquals(IslandVisibilityMode.ALWAYS_VISIBLE, defaultState.islandVisibilityMode)
        assertEquals(IslandTapAction.EXPAND_THEN_OPEN_APP, defaultState.islandTapAction)
        assertEquals(AppFilterMode.HYBRID_SMART, defaultState.appFilterMode)
        assertTrue(defaultState.isIslandFreeDragEnabled)
        assertTrue(defaultState.islandMarqueeEnabled)
        assertTrue(defaultState.liquidMetaballMorphEnabled)
        assertTrue(defaultState.isQuickDockEnabled)
        assertTrue(defaultState.showBarometerAndCompassOnAod)
        assertTrue(defaultState.iceCardEnabled)
        assertFalse(defaultState.isPreviewCollapsed)
        assertEquals(25, defaultState.countEnabledVisualElements())

        // Verify Bubble Mode & JSON Export / Import round-trip
        val repo = LuminaRepository(
            dao = LuminaDatabase.getInstance(context).luminaDao(),
            appContext = context
        )
        val customized = defaultState.copy(
            clockStyle = ClockFaceStyle.SYNTHWAVE_LED_GRID,
            palette = AodColorPalette.AURORA_EMERALD,
            pinnedMemoText = "Тестовая заметка",
            standByNightModeEnabled = true,
            isBubbleModeEnabled = true,
            bubbleSizeDp = 58f,
            bubbleExpandMode = IslandBubbleExpandMode.MARQUEE_TICKER_STRIP,
            islandEdgeSnapMode = IslandEdgeSnapMode.ALL_FOUR_EDGES_AND_CORNERS,
            islandEntranceAnimation = IslandEntranceAnimation.SUPERNOVA_FLASH_PULSE,
            showClockOnAod = false
        )
        assertTrue(customized.isEffectiveBubbleMode)
        val json = repo.exportConfigToJson(customized)
        assertTrue(json.contains("synthwave_led_grid"))
        val imported = repo.importConfigFromJson(json, defaultState)
        assertNotNull(imported)
        assertEquals(ClockFaceStyle.SYNTHWAVE_LED_GRID, imported?.clockStyle)
        assertEquals(AodColorPalette.AURORA_EMERALD, imported?.palette)
        assertEquals("Тестовая заметка", imported?.pinnedMemoText)
        assertEquals(true, imported?.standByNightModeEnabled)
        assertEquals(true, imported?.isBubbleModeEnabled)
        assertEquals(58f, imported?.bubbleSizeDp ?: 0f, 0.01f)
        assertEquals(IslandBubbleExpandMode.MARQUEE_TICKER_STRIP, imported?.bubbleExpandMode)
        assertEquals(IslandEdgeSnapMode.ALL_FOUR_EDGES_AND_CORNERS, imported?.islandEdgeSnapMode)
        assertEquals(IslandEntranceAnimation.SUPERNOVA_FLASH_PULSE, imported?.islandEntranceAnimation)
        assertEquals(false, imported?.showClockOnAod)
    }
}

