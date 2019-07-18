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
package com.saltedge.authenticator.unitTests.tool.secure.fingerprint

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.saltedge.authenticator.tool.secure.fingerprint.FingerprintState
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FingerprintStateTest {

    @Test
    @Throws(Exception::class)
    fun valuesTest() {
        assertThat(FingerprintState.values(), equalTo(arrayOf(FingerprintState.NOT_SUPPORTED,
                FingerprintState.NOT_BLOCKED_DEVICE, FingerprintState.NO_FINGERPRINTS,
                FingerprintState.READY)))
    }

    @Test
    @Throws(Exception::class)
    fun valueOfTest() {
        assertThat(FingerprintState.valueOf("NOT_SUPPORTED"), equalTo(FingerprintState.NOT_SUPPORTED))
        assertThat(FingerprintState.valueOf("NOT_BLOCKED_DEVICE"), equalTo(FingerprintState.NOT_BLOCKED_DEVICE))
        assertThat(FingerprintState.valueOf("NO_FINGERPRINTS"), equalTo(FingerprintState.NO_FINGERPRINTS))
        assertThat(FingerprintState.valueOf("READY"), equalTo(FingerprintState.READY))
    }
}
