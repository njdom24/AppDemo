package com.appdemo.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appdemo.MainActivity
import com.appdemo.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = "This is my home fragment"
        })

        var button: Button = root.findViewById(R.id.goToAddProduct2)
        button.setOnClickListener(this)
        button = root.findViewById(R.id.driveUpload)
        button.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        //Match per button
        when(v?.id) {
            R.id.goToAddProduct2 -> {
                val specialMessage = "fu"
                d("Test", "The message is: $specialMessage")

                val it = Intent(activity, MainActivity::class.java)
                startActivity(it)
            }
            R.id.driveUpload -> uploadPdfFile(v)
        }

    }

    fun requestSignIn() {
        val signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(
            Scope(DriveScopes.DRIVE_FILE)).build()

        val client: GoogleSignInClient = GoogleSignIn.getClient(this.activity!!, signInOptions)

        startActivityForResult(client.signInIntent, 400)
    }

    fun uploadPdfFile(v: View?) {
        d("Test", "The message is: Duck")
        requestSignIn()
    }
}
