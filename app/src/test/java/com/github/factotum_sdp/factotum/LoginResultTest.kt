package com.github.factotum_sdp.factotum
import com.github.factotum_sdp.factotum.ui.login.LoggedInUserView
import com.github.factotum_sdp.factotum.ui.login.LoginResult
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class LoginResultTest {

    @Test
    fun `login success`() {
        // given
        val userView = LoggedInUserView("Alice", "alice@gmail.com")

        // when
        val result = LoginResult(success = userView)

        // then
        assertThat(result.success, `is`(userView))
        assertThat(result.error, `is`(nullValue()))
    }

    @Test
    fun `login error`() {
        // given
        val errorCode = 401

        // when
        val result = LoginResult(error = errorCode)

        // then
        assertThat(result.success, `is`(nullValue()))
        assertThat(result.error, `is`(errorCode))
    }

    @Test
    fun `login no data`() {
        // when
        val result = LoginResult()

        // then
        assertThat(result.success, `is`(nullValue()))
        assertThat(result.error, `is`(nullValue()))
    }

}