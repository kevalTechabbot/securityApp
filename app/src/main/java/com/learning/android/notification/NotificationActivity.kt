package com.learning.android.notification

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.learning.android.R
import com.learning.android.utils.PrefUtils
import org.json.JSONArray

class NotificationActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_POST_NOTIFICATION_PERMISSION = 200
        val TAG: String = Companion::class.java.name
    }

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var sharedPreferences: PrefUtils
    private var tokenList: JSONArray = JSONArray()
    private lateinit var msg: TextView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        msg = findViewById(R.id.msg)
        progressBar = findViewById(R.id.progressBar)

        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        sharedPreferences = PrefUtils(this@NotificationActivity)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(ContentValues.TAG, "FCM token: $token")

            Log.i(TAG, "sharedPreferences: ${sharedPreferences.getString("token")}")

            if (sharedPreferences.getString("token").isEmpty()) readDataFromFirestore(
                token
            ) else {
                progressBar.isVisible = false
                msg.text = getString(R.string.token_has_been_uploaded_please_wait_for_notification)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If not, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_POST_NOTIFICATION_PERMISSION
            )
        }
    }

    private fun writeDataOnFirestore(firebaseTokenModel: String) {
        val arrayList = arrayListOf<String>()
        for (i in 0 until tokenList.length()) {
            val value = tokenList.getString(i)
            arrayList.add(value)
        }
        val student = HashMap<String, Any>()
        student["token"] = arrayList
        mFirestore.collection("token_info").document("token_details").set(student)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                sharedPreferences.putString("token", firebaseTokenModel)
                progressBar.isVisible = false
                msg.text = getString(R.string.token_has_been_uploaded_please_wait_for_notification)
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                progressBar.isVisible = false
                msg.text = e.localizedMessage
            }
    }

    private fun readDataFromFirestore(firebaseTokenModel: String) {
        progressBar.isVisible = true
        mFirestore.collection("token_info").document("token_details").get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        val data = document.data
                        tokenList = JSONArray()

                        if (data?.get("token") == null) {
                            tokenList.put(firebaseTokenModel)
                            writeDataOnFirestore(firebaseTokenModel)
                            return@addOnSuccessListener
                        }

                        tokenList = JSONArray(Gson().toJson(data["token"]))
                        tokenList.put(firebaseTokenModel)

                        writeDataOnFirestore(firebaseTokenModel)

                    } else {
                        progressBar.isVisible = false
                        Toast.makeText(this, "No such document!", Toast.LENGTH_LONG).show()
                        msg.text = getString(R.string.no_such_document)
                    }
                } catch (ex: Exception) {
                    progressBar.isVisible = false
                    ex.printStackTrace()
                    ex.message?.let { Log.e(TAG, it) }
                    ex.localizedMessage?.let { Log.e(TAG, it) }
                    msg.text = ex.localizedMessage
                }
            }.addOnFailureListener { e ->
                progressBar.isVisible = false
                Log.e(TAG, "Error writing document", e)
                msg.text = e.localizedMessage
            }
    }
}