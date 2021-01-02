package com.ayoprez.nextcloudgallery.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayoprez.nextcloudgallery.R
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import com.nextcloud.android.sso.AccountImporter
import com.nextcloud.android.sso.AccountImporter.IAccountAccessGranted
import com.nextcloud.android.sso.AccountImporter.accountsToImportAvailable
import com.nextcloud.android.sso.api.NextcloudAPI
import com.nextcloud.android.sso.api.NextcloudAPI.ApiConnectedListener
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException
import com.nextcloud.android.sso.helper.SingleAccountHelper
import com.nextcloud.android.sso.model.SingleSignOnAccount
import com.nextcloud.android.sso.ui.UiExceptionManager


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val loginButton: MaterialButton = root.findViewById(R.id.login_button)


        if(SingleAccountHelper.getCurrentSingleSignOnAccount(context).token.isNullOrBlank()) {
            loginButton.visibility = View.VISIBLE
        } else {
            loginButton.visibility = View.GONE
        }

        loginButton.setOnClickListener { openAccountChooser() }

        return root
    }

    private fun openAccountChooser() {
        try {
            AccountImporter.pickNewAccount(this)
        } catch (e: NextcloudFilesAppNotInstalledException) {
            UiExceptionManager.showDialogForException(context, e)
        } catch (e: AndroidGetAccountsPermissionNotGranted) {
            UiExceptionManager.showDialogForException(context, e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AccountImporter.onActivityResult(requestCode, resultCode, data, this, object : IAccountAccessGranted {
            var callback: ApiConnectedListener = object : ApiConnectedListener {
                override fun onConnected() {
                    // ignore this one… see 5)
                    print("-----Connected with Nextcloud")
                }

                override fun onError(ex: Exception) {
                    // TODO handle errors
                    print("-------Error: ${ex.message}")
                }
            }

            override fun accountAccessGranted(account: SingleSignOnAccount) {
                // As this library supports multiple accounts we created some helper methods if you only want to use one.
                // The following line stores the selected account as the "default" account which can be queried by using
                // the SingleAccountHelper.getCurrentSingleSignOnAccount(context) method
                SingleAccountHelper.setCurrentAccount(activity, account.name)

                // Get the "default" account
                val ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context)
                val nextcloudAPI = NextcloudAPI(context!!, ssoAccount, GsonBuilder().create(), callback)
                print("-------- Access granted: ${ssoAccount.name}")
            }
        })
    }
}