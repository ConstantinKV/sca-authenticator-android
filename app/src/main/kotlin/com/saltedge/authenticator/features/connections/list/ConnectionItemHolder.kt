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
package com.saltedge.authenticator.features.connections.list

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.saltedge.authenticator.R
import com.saltedge.authenticator.features.connections.common.ConnectionItemViewModel
import com.saltedge.authenticator.interfaces.ListItemClickListener
import com.saltedge.authenticator.tools.inflateListItemView
import com.saltedge.authenticator.tools.loadRoundedImage

class ConnectionItemHolder(parent: ViewGroup, private val listener: ListItemClickListener?) :
    RecyclerView.ViewHolder(parent.inflateListItemView(R.layout.view_item_connection)) {

    private val logoImageView = itemView.findViewById<ImageView>(R.id.logoImageView)
    private val titleView = itemView.findViewById<TextView>(R.id.titleView)
    private val subTitleView = itemView.findViewById<TextView>(R.id.subTitleView)
    private val listItemView = itemView.findViewById<RelativeLayout>(R.id.listItemView)
    private val bgColor = ContextCompat.getColor(listItemView.context, R.color.white_and_blue_black)
    private val robotoRegular = ResourcesCompat.getFont(listItemView.context, R.font.roboto_regular)
    private val robotoMedium = ResourcesCompat.getFont(listItemView.context, R.font.roboto_medium)

    init {
        itemView.setOnClickListener {
            if (adapterPosition > RecyclerView.NO_POSITION)
                listener?.onListItemClick(itemIndex = adapterPosition)
        }
    }

    fun bind(item: ConnectionItemViewModel) {
        logoImageView.loadRoundedImage(
            imageUrl = item.logoUrl,
            placeholderId = R.drawable.shape_bg_app_logo,
            cornerRadius = itemView.resources.getDimension(R.dimen.connections_list_logo_radius)
        )
        if (item.isChecked) listItemView.setBackgroundResource(R.drawable.stroke_background)
        else listItemView.setBackgroundColor(bgColor)

        titleView.text = item.name

        val description = if (item.consentsCount.isEmpty()) {
            SpannableStringBuilder(item.statusDescription)
        } else {
            SpannableStringBuilder("${item.consentsCount} \u00B7 ${item.statusDescription}")
        }

        description.setSpan(
            robotoMedium?.style?.let { StyleSpan(it) } ?: return,
            0,
            item.consentsCount.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        description.setSpan(
            robotoRegular?.style?.let { StyleSpan(it) } ?: return,
            description.indexOf(item.statusDescription, 0),
            description.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        subTitleView.setText(description, TextView.BufferType.SPANNABLE)
    }
}
