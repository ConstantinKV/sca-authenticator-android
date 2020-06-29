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
package com.saltedge.authenticator.features.connections.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.saltedge.authenticator.R
import com.saltedge.authenticator.app.DELETE_REQUEST_CODE
import com.saltedge.authenticator.app.KEY_GUID
import com.saltedge.authenticator.app.RENAME_REQUEST_CODE
import com.saltedge.authenticator.features.authorizations.common.collectConnectionsAndKeys
import com.saltedge.authenticator.features.connections.common.ConnectionViewModel
import com.saltedge.authenticator.models.Connection
import com.saltedge.authenticator.models.ViewModelEvent
import com.saltedge.authenticator.models.repository.ConnectionsRepositoryAbs
import com.saltedge.authenticator.sdk.AuthenticatorApiManagerAbs
import com.saltedge.authenticator.sdk.constants.KEY_NAME
import com.saltedge.authenticator.sdk.contract.ConnectionsRevokeListener
import com.saltedge.authenticator.sdk.contract.FetchEncryptedDataListener
import com.saltedge.authenticator.sdk.model.*
import com.saltedge.authenticator.sdk.model.connection.ConnectionAndKey
import com.saltedge.authenticator.sdk.model.connection.isActive
import com.saltedge.authenticator.sdk.model.error.ApiErrorData
import com.saltedge.authenticator.sdk.tools.crypt.CryptoToolsAbs
import com.saltedge.authenticator.sdk.tools.keystore.KeyStoreManagerAbs
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ConnectionsListViewModel(
    private val appContext: Context,
    private val connectionsRepository: ConnectionsRepositoryAbs,
    private val keyStoreManager: KeyStoreManagerAbs,
    private val apiManager: AuthenticatorApiManagerAbs,
    private val cryptoTools: CryptoToolsAbs
) : ViewModel(), LifecycleObserver, ConnectionsRevokeListener, FetchEncryptedDataListener,
    CoroutineScope {

    private var consents: Map<String, List<ConsentData>> = emptyMap()
    private val decryptJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = decryptJob + Dispatchers.IO
    private var connectionsAndKeys: Map<ConnectionID, ConnectionAndKey> =
        collectConnectionsAndKeys(
            connectionsRepository,
            keyStoreManager
        )

    var onQrScanClickEvent = MutableLiveData<ViewModelEvent<Unit>>()
        private set
    var onListItemClickEvent = MutableLiveData<ViewModelEvent<Int>>()
        private set
    var onSupportClickEvent = MutableLiveData<ViewModelEvent<String?>>()
        private set
    var onReconnectClickEvent = MutableLiveData<ViewModelEvent<String>>()
        private set
    var onRenameClickEvent = MutableLiveData<ViewModelEvent<Bundle>>()
        private set
    var onDeleteClickEvent = MutableLiveData<ViewModelEvent<Bundle>>()
        private set

    val listVisibility = MutableLiveData<Int>()
    val emptyViewVisibility = MutableLiveData<Int>()

    val listItems = MutableLiveData<List<ConnectionViewModel>>()
    val listItemsValues: List<ConnectionViewModel>
        get() = listItems.value ?: emptyList()
    val updateListItemEvent = MutableLiveData<ConnectionViewModel>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        updateViewsContent()
        getConsents()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onDestroy() {
        decryptJob.cancel()
    }

    override fun onConnectionsRevokeResult(revokedTokens: List<Token>, apiError: ApiErrorData?) {}

    override fun onFetchEncryptedDataResult(
        result: List<EncryptedData>,
        errors: List<ApiErrorData>
    ) {
        processOfEncryptedConsentsResult(encryptedList = result)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null || resultCode != Activity.RESULT_OK) return
        val listItem = listItemsValues.find { it.guid == data.getStringExtra(KEY_GUID) } ?: return
        when (requestCode) {
            RENAME_REQUEST_CODE -> {
                onUserRenamedConnection(
                    listItem = listItem,
                    newConnectionName = data.getStringExtra(KEY_NAME) ?: return
                )
            }
            DELETE_REQUEST_CODE -> onUserConfirmedDeleteConnection(listItem = listItem)
        }
    }

    fun onViewClick(viewId: Int) {
        if (viewId == R.id.actionView) onQrScanClickEvent.postValue(ViewModelEvent(Unit))
    }

    fun onListItemClick(itemIndex: Int) {
        onListItemClickEvent.postValue(ViewModelEvent(itemIndex))
    }

    fun onReconnectOptionSelected() {
        val index = onListItemClickEvent.value?.peekContent() ?: return
        val connectionGuid = listItemsValues.getOrNull(index)?.guid ?: return

        onReconnectClickEvent.postValue(ViewModelEvent(connectionGuid))
    }

    fun onRenameOptionSelected() {
        val index = onListItemClickEvent.value?.peekContent() ?: return
        val connectionGuid = listItemsValues.getOrNull(index)?.guid ?: return

        connectionsRepository.getByGuid(connectionGuid)?.let { connection ->
            onRenameClickEvent.postValue(ViewModelEvent(Bundle()
                .apply { putString(KEY_GUID, connectionGuid) }
                .apply { putString(KEY_NAME, connection.name) })
            )
        }
    }

    fun onContactSupportOptionSelected() {
        val index = onListItemClickEvent.value?.peekContent() ?: return
        val connectionGuid = listItemsValues.getOrNull(index)?.guid ?: return

        onSupportClickEvent.postValue(
            ViewModelEvent(
                connectionsRepository.getByGuid(
                    connectionGuid
                )?.supportEmail
            )
        )
    }

    fun onDeleteOptionsSelected() {
        val item = listItemsValues.getOrNull(onListItemClickEvent.value?.peekContent() ?: return) ?: return
        connectionsRepository.getByGuid(item.guid)?.let { connection ->
            if (connection.isActive()) {
                onDeleteClickEvent.postValue(ViewModelEvent(Bundle()
                    .apply { putString(KEY_GUID, connection.guid) }
                ))
            } else {
                deleteConnectionsAndKeys(connection.guid)
                updateViewsContent()
            }
        }
    }

    //TODO: Display list of active Consents
    fun onViewConsentsOptionSelected() {
        Log.d("some", "click view consents")
    }

    fun refreshConsents() {
        getConsents()
    }

    //TODO SET AS PRIVATE AFTER CREATING TEST FOR COROUTINE
    fun processDecryptedConsentsResult(result: List<ConsentData>) {
        this.consents = result.groupBy { it.connectionId ?: "" }
        val newListItems = listItemsValues.apply {
            forEach {
                val consentsSize = consents[it.connectionId]?.size ?: 0
                it.consentDescription = if (consentsSize > 0)  {
                    it.hasConsents = true
                    appContext.resources.getQuantityString(
                        R.plurals.ui_consents,
                        consentsSize,
                        consentsSize
                    ) + " ·"
                } else ""
            }
        }
        listItems.postValue(newListItems)
    }

    private fun getConsents() {
        collectConsentRequestData()?.let {
            apiManager.getConsents(
                connectionsAndKeys = it,
                resultCallback = this
            )
        }
    }

    private fun collectConsentRequestData(): List<ConnectionAndKey>? {
        return if (connectionsAndKeys.isEmpty()) null else connectionsAndKeys.values.toList()
    }

    private fun processOfEncryptedConsentsResult(encryptedList: List<EncryptedData>) {
        launch {
            val data = decryptConsents(encryptedList = encryptedList)
            withContext(Dispatchers.Main) { processDecryptedConsentsResult(result = data) }
        }
    }

    private fun decryptConsents(encryptedList: List<EncryptedData>): List<ConsentData> {
        return encryptedList.mapNotNull {
            cryptoTools.decryptConsentData(
                encryptedData = it,
                rsaPrivateKey = connectionsAndKeys[it.connectionId]?.key
            )
        }
    }

    private fun onUserRenamedConnection(listItem: ConnectionViewModel, newConnectionName: String) {
        val itemIndex = listItemsValues.indexOf(listItem)
        if (listItem.name != newConnectionName && newConnectionName.isNotEmpty()) {
            connectionsRepository.getByGuid(listItem.guid)?.let { connection ->
                connectionsRepository.updateNameAndSave(connection, newConnectionName)
                listItems.value?.get(itemIndex)?.name = newConnectionName
                updateListItemEvent.postValue(listItem)
            }
        }
    }

    private fun onUserConfirmedDeleteConnection(listItem: ConnectionViewModel) {
        connectionsRepository.getByGuid(listItem.guid)?.let { connection ->
            sendRevokeRequestForConnections(listOf(connection))
        }
        deleteConnectionsAndKeys(listItem.guid)
        updateViewsContent()
    }

    private fun updateViewsContent() {
        val newListItems = collectAllConnectionsViewModels(connectionsRepository, appContext)
        listItems.postValue(newListItems)
        if (newListItems.isEmpty()) {
            emptyViewVisibility.postValue(View.VISIBLE)
            listVisibility.postValue(View.GONE)
        } else {
            listVisibility.postValue(View.VISIBLE)
            emptyViewVisibility.postValue(View.GONE)
        }
    }

    private fun sendRevokeRequestForConnections(connections: List<Connection>) {
        val connectionsAndKeys: List<ConnectionAndKey> = connections.filter { it.isActive() }
            .mapNotNull { keyStoreManager.createConnectionAndKeyModel(it) }

        apiManager.revokeConnections(
            connectionsAndKeys = connectionsAndKeys,
            resultCallback = this
        )
    }

    private fun deleteConnectionsAndKeys(connectionGuid: GUID) {
        keyStoreManager.deleteKeyPair(connectionGuid)
        connectionsRepository.deleteConnection(connectionGuid)
    }
}
