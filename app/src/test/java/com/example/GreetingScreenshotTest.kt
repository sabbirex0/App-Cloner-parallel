package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.CloneInstance
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleClones = listOf(
      CloneInstance(
        id = 1L,
        packageName = "com.whatsapp",
        appName = "WhatsApp",
        instanceName = "WhatsApp (Personal)",
        instanceNumber = 1,
        badgeColorHex = "#10B981",
        badgeLabel = "1"
      ),
      CloneInstance(
        id = 2L,
        packageName = "com.whatsapp",
        appName = "WhatsApp",
        instanceName = "WhatsApp (Work)",
        instanceNumber = 2,
        badgeColorHex = "#0284C7",
        badgeLabel = "WORK",
        isPinProtected = true
      )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        HomeScreen(
          clones = sampleClones,
          runningClones = emptyList(),
          onCloneClick = {},
          onCloneMoreClick = {},
          onLaunchDualSplit = { _, _ -> },
          onOpenDualRunner = {},
          onOpenStorageManager = {},
          onCreateClone = {},
          onTemplateSelect = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

