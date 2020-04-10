package com.appdemo

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    var driveServiceHelper: DriveServiceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        goToAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        uppity.setOnClickListener {
            uploadPdfFile(it)
        }

        requestSignIn()
        /*
        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
        val savedName = preferences.getString("savedProductName", "String not found in database")

        lastSavedProduct.text = savedName
         */
        //Website is: https://finepointmobile.com/api/inventory/v1/message

        lifecycleScope.launch(Dispatchers.Default) {
            var specialMessage = URL("https://finepointmobile.com/api/inventory/v1/message").readText()
            specialMessage = specialMessage.substring(0, specialMessage.indexOf("from"))
            d("test", "The message is: $specialMessage")

            runOnUiThread {
                lastSavedProduct.text = specialMessage
            }
        }




    }

    fun uploadPdfFile(v: View?) {
        d("Test", "The message is: Ruck")

        val progressDialog: ProgressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setTitle("Uploading to Google Drive")
        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        //Takes the file from here
        val filePath:String = "/storage/emulated/0/Download/mypdf.pdf"
        driveServiceHelper!!.createFilePDF(filePath).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "Uploaded successfully", Toast.LENGTH_LONG).show()
            d("Test", "The message is: Pluck")
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "Check your google drive api key", Toast.LENGTH_LONG).show()
        }
    }

    fun requestSignIn() {
        val signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(
            Scope(DriveScopes.DRIVE_FILE)
        ).build()

        val client: GoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)

        startActivityForResult(client.signInIntent, 400)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            400 -> {
                if(resultCode == RESULT_OK) {
                    handleSignInIntent(data)
                }
            }
        }
    }

    private fun handleSignInIntent(data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener {
            val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(this@MainActivity, Collections.singleton(DriveScopes.DRIVE_FILE))
            //GoogleAccountCredential.

            credential.selectedAccount = it.account

            val googleDriveService: Drive = Drive.Builder (
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential).setApplicationName("My Drive Tutorial").build()

            driveServiceHelper = DriveServiceHelper(googleDriveService)

        }.addOnFailureListener {

        }
    }
}
