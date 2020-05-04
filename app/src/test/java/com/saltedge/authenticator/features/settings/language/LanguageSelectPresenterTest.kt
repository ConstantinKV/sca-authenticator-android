/*
 * This file is part of the Salt Edge Authenticator distribution
 * (https://github.com/saltedge/sca-authenticator-android).
 * Copyright (c) 2019 Salt Edge Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 or later.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * For the additional permissions granted for Salt Edge Authenticator
 * under Section 7 of the GNU General Public License see THIRD_PARTY_NOTICES.md
 */
package com.saltedge.authenticator.features.settings.language

import com.saltedge.authenticator.models.repository.PreferenceRepositoryAbs
import com.saltedge.authenticator.testTools.TestAppTools
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LanguageSelectPresenterTest {

    @Test
    @Throws(Exception::class)
    fun initTest() {
        val presenterContract = createPresenter()

        Assert.assertNull(presenterContract.viewContract)
        assertThat(presenterContract.availableLanguages, equalTo(arrayOf("English")))
        assertThat(presenterContract.currentItemIndex, equalTo(0))

        presenterContract.viewContract = mockView

        Assert.assertNotNull(presenterContract.viewContract)
    }

    @Test
    @Throws(Exception::class)
    fun updateCurrentLanguageTest() {
        val presenterContract = createPresenter()

        presenterContract.onOkClick()

        Mockito.verifyNoMoreInteractions(mockView)

        presenterContract.viewContract = mockView

        presenterContract.onOkClick()

        Mockito.verify(mockView).closeView()
    }

    private val mockView = Mockito.mock(LanguageSelectContract.View::class.java)
    private val mockPreferenceRepository = Mockito.mock(PreferenceRepositoryAbs::class.java)

    private fun createPresenter(viewContract: LanguageSelectContract.View? = null): LanguageSelectPresenter {
        return LanguageSelectPresenter(TestAppTools.applicationContext, mockPreferenceRepository)
            .apply { this.viewContract = viewContract }
    }
}
