/*
 * This file is part of the Salt Edge Authenticator distribution
 * (https://github.com/saltedge/sca-authenticator-android).
 * Copyright (c) 2020 Salt Edge Inc.
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
package com.saltedge.authenticator.features.launcher

import com.saltedge.authenticator.features.main.MainActivity
import com.saltedge.authenticator.features.onboarding.OnboardingSetupActivity
import com.saltedge.authenticator.model.realm.RealmManagerAbs
import com.saltedge.authenticator.model.repository.PreferenceRepositoryAbs
import com.saltedge.authenticator.testTools.TestAppTools
import com.saltedge.authenticator.tool.secure.PasscodeToolsAbs
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LauncherViewModelTest {

    private lateinit var viewModel: LauncherViewModel
    private val mockPreferenceRepository = Mockito.mock(PreferenceRepositoryAbs::class.java)
    private val mockPasscodeTools = Mockito.mock(PasscodeToolsAbs::class.java)
    private val mockRealmManager = Mockito.mock(RealmManagerAbs::class.java)

    @Before
    fun setUp() {
        viewModel = LauncherViewModel(
            appContext = TestAppTools.applicationContext,
            preferenceRepository = mockPreferenceRepository,
            passcodeTools = mockPasscodeTools,
            realmManager = mockRealmManager
        )
    }

    @Test
    @Throws(Exception::class)
    fun getNextActivityClassTest() {
        Mockito.doReturn(false).`when`(mockPreferenceRepository).passcodeExist()

        assertEquals(viewModel.getNextActivityClass(), OnboardingSetupActivity::class.java)

        Mockito.doReturn(true).`when`(mockPreferenceRepository).passcodeExist()

        assertEquals(viewModel.getNextActivityClass(), MainActivity::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun onLifeCycleResumeTestCase1() {
        Mockito.doReturn(true).`when`(mockRealmManager).errorOccurred

        viewModel.onLifeCycleResume()

        Mockito.verify(mockRealmManager).initRealm(TestAppTools.applicationContext)
        assertTrue(mockRealmManager.errorOccurred)
        assertNotNull(viewModel.errorOccurred.value)
        assertNull(viewModel.noErrorOccurred.value)
    }

    @Test
    @Throws(Exception::class)
    fun onLifeCycleResumeTestCase2() {
        Mockito.doReturn(false).`when`(mockPreferenceRepository).passcodeExist()
        viewModel.onLifeCycleResume()

        Mockito.verify(mockPasscodeTools).replacePasscodeKey(TestAppTools.applicationContext)

        Mockito.doReturn(true).`when`(mockPreferenceRepository).passcodeExist()
        viewModel.onLifeCycleResume()

        Mockito.verifyNoMoreInteractions(mockPasscodeTools)
    }

    @Test
    @Throws(Exception::class)
    fun onLifeCycleResumeTestCase3() {
        viewModel.onLifeCycleResume()

        Mockito.verify(mockRealmManager).initRealm(TestAppTools.applicationContext)
        assertFalse(mockRealmManager.errorOccurred)
        assertNull(viewModel.errorOccurred.value)
        assertNotNull(viewModel.noErrorOccurred.value)
    }
}
