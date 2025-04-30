package com.example.helloworld

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf // Mantenha este import se usar allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    // ActivityScenarioRule inicia a activity antes de cada teste e a fecha depois.
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenActivityStarts_textViewShouldBeEmpty() {
        // REMOVED: Thread.sleep(2000)
        // O Espresso espera automaticamente o lançamento da activity e a estabilização da UI.
        // Não é necessário Thread.sleep().
        onView(withId(R.id.tvGreet))
            .check(matches(withText(""))) // Verifica se o TextView está inicialmente vazio
    }

    @Test
    fun whenButtonClicked_textViewShouldShowHelloAndroid() {
        // Given - Verificação do estado inicial (opcional, mas boa prática)
        // REMOVED: Thread.sleep(2000)
        // O Espresso espera a view estar presente antes de verificar.
        onView(withId(R.id.tvGreet))
            .check(matches(withText("")))

        // When - Ação
        // O Espresso espera o botão estar pronto (visível, clicável) antes de realizar o clique.
        onView(withId(R.id.btnGreet))
            .perform(click())

        // Then - Verificação do resultado
        // O Espresso espera a UI thread processar o clique e atualizar o TextView.
        onView(withId(R.id.tvGreet))
            .check(matches(withText(R.string.hello_android))) // Verifica se o texto corresponde ao recurso de string
            // Alternativa usando a string literal se necessário, mas o recurso é preferível:
            // .check(matches(withText("Hello Android!")))
    }

    @Test
    fun buttonShouldBeVisible_andClickable() {
        // REMOVED: Thread.sleep(2000)
        // O Espresso espera a view atender às condições (exibida, clicável).
        // Não é necessário Thread.sleep().
        onView(withId(R.id.btnGreet))
            .check(matches(
                allOf( // Use allOf para combinar múltiplos matchers
                    isDisplayed(), // Verifica se o botão está visível na tela
                    isClickable()  // Verifica se o botão está habilitado e pode ser clicado
                )
            ))
    }

    @Test
    fun buttonShouldHaveCorrectText() {
        // REMOVED: Thread.sleep(2000)
        // O Espresso espera a view estar presente antes de verificar seu texto.
        // Não é necessário Thread.sleep().
        onView(withId(R.id.btnGreet))
            .check(matches(withText(R.string.press_me))) // Verifica se o texto do botão corresponde ao recurso de string
            // Alternativa usando a string literal se necessário:
            // .check(matches(withText("Press Me!")))
    }
}
