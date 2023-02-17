package com.gabbyculley.app1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class DisplayActivity : AppCompatActivity() {
    private var tvFirstName: TextView? = null
    private var tvLastName: TextView? = null

    private var firstName: String? = null
    private var lastName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        tvFirstName = findViewById<View>(R.id.tv_first_name) as TextView
        tvLastName = findViewById<View>(R.id.tv_last_name) as TextView

        val receivedIntent = intent

        firstName = receivedIntent.getStringExtra(FIRST_NAME)
        lastName = receivedIntent.getStringExtra(LAST_NAME)

        tvFirstName!!.text = firstName
        tvLastName!!.text = lastName
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FIRST_NAME, tvFirstName!!.text.toString())
        outState.putString(LAST_NAME, tvLastName!!.text.toString())
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        tvFirstName!!.text = outState.getString(FIRST_NAME)
        tvLastName!!.text = outState.getString(LAST_NAME)
    }
}