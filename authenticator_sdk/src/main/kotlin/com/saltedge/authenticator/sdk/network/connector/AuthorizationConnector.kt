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
package com.saltedge.authenticator.sdk.network.connector

import com.saltedge.authenticator.sdk.constants.API_AUTHORIZATIONS
import com.saltedge.authenticator.sdk.constants.REQUEST_METHOD_GET
import com.saltedge.authenticator.sdk.contract.FetchAuthorizationListener
import com.saltedge.authenticator.sdk.model.error.ApiErrorData
import com.saltedge.authenticator.sdk.model.connection.ConnectionAndKey
import com.saltedge.authenticator.sdk.model.response.AuthorizationShowResponse
import com.saltedge.authenticator.sdk.network.ApiInterface
import com.saltedge.authenticator.sdk.network.ApiResponseInterceptor
import retrofit2.Call

internal class AuthorizationConnector(
    private val apiInterface: ApiInterface,
    var resultCallback: FetchAuthorizationListener?
) : ApiResponseInterceptor<AuthorizationShowResponse>() {

    fun getAuthorization(
        connectionAndKey: ConnectionAndKey,
        authorizationId: String
    ) {
        val requestData = createSignedRequestData<Nothing>(
            requestMethod = REQUEST_METHOD_GET,
            baseUrl = connectionAndKey.connection.connectUrl,
            apiRoutePath = "$API_AUTHORIZATIONS/$authorizationId",
            accessToken = connectionAndKey.connection.accessToken,
            signPrivateKey = connectionAndKey.key
        )
        apiInterface.getAuthorization(
            requestUrl = requestData.requestUrl,
            headersMap = requestData.headersMap
        ).enqueue(this)
    }

    override fun onSuccessResponse(
        call: Call<AuthorizationShowResponse>,
        response: AuthorizationShowResponse
    ) {
        resultCallback?.onFetchAuthorizationResult(result = response.data, error = null)
    }

    override fun onFailureResponse(
        call: Call<AuthorizationShowResponse>,
        error: ApiErrorData
    ) {
        resultCallback?.onFetchAuthorizationResult(result = null, error = error)
    }
}
