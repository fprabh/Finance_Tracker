package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.Transaction
import com.example.ui.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import java.time.LocalDate
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
    val mockTransactions = listOf(
      Transaction(
        id = 1,
        date = LocalDate.of(2026, 7, 15),
        description = "Starbucks Coffee",
        cardMember = "John Doe",
        amount = 12.50,
        statementId = 1L
      ),
      Transaction(
        id = 2,
        date = LocalDate.of(2026, 7, 16),
        description = "Amazon US Marketplace",
        cardMember = "Jane Smith",
        amount = 145.99,
        statementId = 2L
      )
    )

    composeTestRule.setContent { 
      MyApplicationTheme { 
        DashboardScreen(
          transactions = mockTransactions,
          categories = emptyList(),
          filters = emptyList()
        ) 
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
