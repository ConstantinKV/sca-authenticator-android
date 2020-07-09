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
package com.saltedge.authenticator.tools

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.saltedge.authenticator.R

typealias ResId = Int

/**
 * Set alpha to color
 *
 * @receiver Int object
 * @param factor - option to change the primary color
 * @return Int object - resource color
 */
fun Int.applyAlphaToColor(factor: Float): Int {
    if (factor >= 1f || factor < 0) return this
    val alpha = Math.round(Color.alpha(this) * factor)
    return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}

/**
 * Get enabled state. Depending of the state, returns the color.
 *
 * @param isEnabled - color status parameter
 * @return ResId (Int) object - color resource
 */
fun getEnabledStateColorResId(isEnabled: Boolean): ResId {
    return if (isEnabled) R.color.button_blue_default else R.color.button_blue_disabled
}

/**
 * Convert dp to px
 *
 * @param dp - number of density pixels
 * @return Int object - number of pixels
 */
fun convertDpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp,
        Resources.getSystem().displayMetrics
    ).toInt()
}

fun SwipeRefreshLayout.stopRefresh() {
    isRefreshing = false
    destroyDrawingCache()
    clearAnimation()
}

fun SpannableStringBuilder.appendColoredText(
    text: String,
    colorRes: ResId,
    context: Context
): SpannableStringBuilder {
    val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context, colorRes))
    return this.append(text, colorSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun SpannableStringBuilder.appendFacedText(
    text: String,
    fontRes: ResId,
    context: Context
): SpannableStringBuilder {
    val span = CustomTypefaceSpan(typeface = ResourcesCompat.getFont(context, fontRes) ?: return this)
    return this.append(text, span, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}
