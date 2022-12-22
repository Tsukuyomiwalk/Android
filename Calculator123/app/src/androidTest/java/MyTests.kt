package сalculator123

import android.app.ApplicationErrorReport
import android.content.pm.InstrumentationInfo
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import govno.example.calculator123.MainActivity
import govno.example.calculator123.R
import kotlinx.coroutines.withContext
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MyTests {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private var device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("example.calculator123", appContext.packageName)
    }

    @Test
    fun testAdd() {
        onView(withId(R.id.butt_2)).perform(click())
        onView(withId(R.id.butt_plus)).perform(click())
        onView(withId(R.id.butt_5)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("7")))
    }

    @Test
    fun testMulti() {
        onView(withId(R.id.butt_3)).perform(click())
        onView(withId(R.id.butt_multi)).perform(click())
        onView(withId(R.id.butt_6)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("18")))
    }

    @Test
    fun testSubtract() {
        onView(withId(R.id.butt_6)).perform(click())
        onView(withId(R.id.butt_minus)).perform(click())
        onView(withId(R.id.butt_9)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("-3")))
    }

    @Test
    fun testDivide() {
        onView(withId(R.id.butt_9)).perform(click())
        onView(withId(R.id.butt_divide)).perform(click())
        onView(withId(R.id.butt_3)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("3")))
    }

    @Test
    fun testNegate() {
        onView(withId(R.id.butt_9)).perform(click())
        onView(withId(R.id.butt_negate)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("-9")))
    }

    @Test
    fun testTrickyLand() {
        onView(withId(R.id.butt_9)).perform(click())
        onView(withId(R.id.butt_plus)).perform(click())
        onView(withId(R.id.butt_0)).perform(click())
        device.setOrientationLeft()
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("9")))
    } //landscape InstanceState check

    @Test
    fun tooManyRotates() {
        onView(withId(R.id.butt_9)).perform(click())
        device.setOrientationLeft()
        device.setOrientationNatural()
        device.setOrientationRight()
        device.setOrientationNatural()
        device.setOrientationLeft()
        device.setOrientationNatural()
        device.pressHome()
        device.pressRecentApps()
        val allAppsButton = device.findObject(
            UiSelector().description("")
        )
        allAppsButton.click()
    } //wild area

    @Test
    fun testTricky0() {
        onView(withId(R.id.butt_9)).perform(click())
        onView(withId(R.id.butt_divide)).perform(click())
        onView(withId(R.id.butt_0)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("")))
    }

    @Test
    fun test0() {
        onView(withId(R.id.butt_0)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("0")))
    }

    @Test
    fun test1() {
        onView(withId(R.id.butt_1)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("1")))
    }

    @Test
    fun test2() {
        onView(withId(R.id.butt_2)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("2")))
    }

    @Test
    fun test3() {
        onView(withId(R.id.butt_3)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("3")))
    }

    @Test
    fun test4() {
        onView(withId(R.id.butt_4)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("4")))
    }

    @Test
    fun test5() {
        onView(withId(R.id.butt_5)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("5")))
    }

    @Test
    fun personalTest() {
        device.openQuickSettings()
        device.pressBack()
        device.pressBack()
        device.pressBack()
        device.pressRecentApps()
        val allAppsButton = device.findObject(
            UiSelector().description("")
        )
        allAppsButton.click()
        onView(withId(R.id.butt_5)).perform(click())
        onView(withId(R.id.butt_multi)).perform(click())
        onView(withId(R.id.butt_5)).perform(click())
        onView(withId(R.id.butt_negate)).perform(click())
        onView(withId(R.id.butt_res)).perform(click())
        onView(withId(R.id.result_txt)).check(matches(withText("-25")))
    } // personalTest

    @Test
    fun test6() {
        onView(withId(R.id.butt_6)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("6")))
    }

    @Test
    fun ipTest() {
        onView(withId(R.id.butt_6)).perform(click())
        onView(withId(R.id.butt_comma)).perform(click())
        onView(withId(R.id.butt_comma)).perform(click())
        onView(withId(R.id.butt_6)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("6.6")))
    } //ip test for commas

    @Test
    fun test7() {
        onView(withId(R.id.butt_7)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("7")))
    }

    @Test
    fun test8() {
        onView(withId(R.id.butt_8)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("8")))
    }

    @Test
    fun test9() {
        onView(withId(R.id.butt_9)).perform(click())
        onView(withId(R.id.operation)).check(matches(withText("9")))
    }
}