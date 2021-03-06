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
package com.saltedge.authenticator.features.main

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.saltedge.authenticator.R
import com.saltedge.authenticator.tools.ResId
import com.saltedge.authenticator.tools.setFont

fun FragmentActivity.showWarningSnack(
    textResId: Int,
    snackBarDuration: Int = Snackbar.LENGTH_INDEFINITE,
    actionResId: Int? = null
) = showWarningSnack(this.getString(textResId), snackBarDuration, actionResId)

fun FragmentActivity.showWarningSnack(
    message: String,
    snackBarDuration: Int = Snackbar.LENGTH_INDEFINITE,
    actionResId: Int? = null
) = getSnackbarAnchorView()?.buildSnackbar(message, snackBarDuration, actionResId)?.apply { show() }

fun FragmentActivity.buildWarningSnack(
    messageRes: ResId,
    snackBarDuration: Int = Snackbar.LENGTH_INDEFINITE,
    actionResId: Int? = null
) = getSnackbarAnchorView()?.buildSnackbar(this.getString(messageRes), snackBarDuration, actionResId)

private fun Activity.getSnackbarAnchorView(): View? {
    return if (this is SnackbarAnchorContainer) getSnackbarAnchorView() else null
}

private fun View.buildSnackbar(
    message: String,
    snackBarDuration: Int,
    actionResId: Int? = null
): Snackbar {
    val snackbar = Snackbar
        .make(this, message, snackBarDuration)
        .setActionTextColor(ContextCompat.getColor(context, R.color.primary_light))
    if (actionResId != null) snackbar.setAction(actionResId) { snackbar.dismiss() }
    val textView = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
    textView.minimumHeight = context.resources.getDimension(R.dimen.action_bar_size).toInt()
    textView.gravity = Gravity.CENTER_VERTICAL
    textView.setFont(R.font.roboto_regular)
    textView.setTextColor(ContextCompat.getColor(context, R.color.grey_40))
    textView.maxLines = 7
    snackbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_black_and_dark_100))
    snackbar.view.setOnTouchListener { _, _ ->
        snackbar.dismiss()
        true
    }
    return snackbar
}

interface SnackbarAnchorContainer {
    fun getSnackbarAnchorView(): View?
}
