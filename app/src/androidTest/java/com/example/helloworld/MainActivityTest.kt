package com.example.helloworld

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    companion object {
        private const val TIMEOUT_IN_SECONDS = 10L
    }

    @Before
    fun setup() {
        // Desabilitar animações para testes mais estáveis
        disableAnimations()
    }

    @Test
    fun testButtonClickChangesTextView() {
        // Registrar IdlingResources
        IdlingRegistry.getInstance().register(ViewMatchers.isRoot())
        
        try {
            // Esperar pela view root
            waitForRootView()

            // Verificar estado inicial
            checkInitialState()

            // Executar ação do botão
            performButtonClick()

            // Verificar estado final
            checkFinalState()
        } finally {
            // Sempre limpar os recursos
            IdlingRegistry.getInstance().unregister(ViewMatchers.isRoot())
        }
    }

    private fun waitForRootView() {
        onView(isRoot()).perform(
            waitForView(R.id.main, TimeUnit.SECONDS.toMillis(TIMEOUT_IN_SECONDS))
        )
    }

    private fun checkInitialState() {
        onView(withId(R.id.tvGreet))
            .check(matches(withText("")))
    }

    private fun performButtonClick() {
        onView(withId(R.id.btnGreet))
            .perform(click())
    }

    private fun checkFinalState() {
        onView(withId(R.id.tvGreet))
            .check(matches(withText(R.string.hello_android)))
    }

    private fun disableAnimations() {
        activityRule.scenario.onActivity { activity ->
            (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.refreshRate.let { refreshRate ->
                    activity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
        }
    }

    // ViewAction customizada para esperar pela view
    private fun waitForView(viewId: Int, timeout: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            
            override fun getDescription(): String = 
                "wait for view with id $viewId"
            
            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + timeout

                do {
                    // Procurar pela view
                    val viewToFind = view.findViewById<View>(viewId)
                    if (viewToFind != null && viewToFind.isShown) {
                        return
                    }
                    
                    // Esperar um pouco antes de tentar novamente
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                // Se chegou aqui, timeout aconteceu
                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException("Timeout of $timeout ms has expired"))
                    .build()
            }
        }
    }

    // Classe auxiliar para verificações customizadas
    class CustomViewAssertions {
        companion object {
            fun withCustomTimeout(
                matcher: Matcher<View>,
                timeoutInSeconds: Long = TIMEOUT_IN_SECONDS
            ): ViewAssertion {
                return ViewAssertion { view, noViewFoundException ->
                    if (noViewFoundException != null) {
                        throw noViewFoundException
                    }
                    
                    val startTime = System.currentTimeMillis()
                    val endTime = startTime + TimeUnit.SECONDS.toMillis(timeoutInSeconds)
                    
                    while (System.currentTimeMillis() < endTime) {
                        try {
                            assertThat(view, matcher)
                            return@ViewAssertion
                        } catch (e: AssertionError) {
                            Thread.sleep(50)
                        }
                    }
                    
                    // Se chegou aqui, falhou após timeout
                    assertThat(view, matcher)
                }
            }
        }
    }
}
