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
package com.saltedge.authenticator.features.settings.mvvm.about

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saltedge.authenticator.R
import com.saltedge.authenticator.tool.AppTools

class AboutViewModel(val appContext: Context) : ViewModel() {

    private val observableEvents = MutableLiveData<ViewModelEvent>()

    fun observeViewModelEvents(): LiveData<ViewModelEvent> = observableEvents

    fun getListItems(): List<AboutListItemViewModel> {
        return listOf(
            AboutListItemViewModel(
                titleId = R.string.about_app_version,
                value = AppTools.getAppVersionName(appContext)
            ),
            AboutListItemViewModel(
                titleId = R.string.about_copyright,
                value = appContext.getString(R.string.about_copyright_description)
            ),
            AboutListItemViewModel(
                titleId = R.string.about_terms_service
            ),
            AboutListItemViewModel(
                titleId = R.string.about_open_source_licenses
            )
        )
    }

    fun onTitleClick(titleName: Int) {
        when (titleName) {
            R.string.about_terms_service -> postViewModelEvent(WebViewFragmentEvent())
            R.string.about_open_source_licenses -> postViewModelEvent(LicensesFragmentEvent())
        }
    }

    private fun postViewModelEvent(event: ViewModelEvent) {
        observableEvents.postValue(event)
    }
}
