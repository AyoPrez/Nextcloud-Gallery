package com.ayoprez.nextcloudgallery.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayoprez.nextcloudgallery.R
import com.google.android.material.button.MaterialButton
import com.nextcloud.android.sso.AccountImporter
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException
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
}