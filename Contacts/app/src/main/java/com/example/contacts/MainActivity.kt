package com.example.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val READ_CONTACTS_REQUEST_ID = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showContactsOrRequestPermissions()

        contacts_main.setOnRefreshListener {
            contacts_main.isRefreshing = true
            showContactsOrRequestPermissions()
            contacts_main.isRefreshing = false
        }

    }

    private fun showContactsOrRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermissions()
        } else {
            showContacts()
        }
    }

    private fun requestContactsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Snackbar
                .make(contacts_main, R.string.read_contacts_reason, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_ok) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        READ_CONTACTS_REQUEST_ID
                    )
                }.show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_REQUEST_ID
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            READ_CONTACTS_REQUEST_ID -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    Snackbar
                        .make(
                            contacts_main,
                            getString(R.string.read_contacts_granted),
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(getString(R.string.action_update)) {
                            showContacts()
                        }.show()
                } else {
                    Snackbar.make(
                        contacts_main,
                        getString(R.string.read_contacts_not_granted),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showContacts() {
        val contacts: List<Contact> = fetchAllContacts()
        contacts_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ContactAdapter(contacts) {
                startActivity(Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${it.phoneNumber}")
                })
            }
        }

        Toast.makeText(
            this@MainActivity,
            resources.getQuantityString(R.plurals.contacts_count_plurals, contacts.size, contacts.size),
            Toast.LENGTH_SHORT
        ).show()
    }


}