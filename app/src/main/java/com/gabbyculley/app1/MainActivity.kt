package com.gabbyculley.app1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

const val FIRST_NAME = "FIRST_NAME"
const val MIDDLE_NAME = "MIDDLE_NAME"
const val LAST_NAME = "LAST_NAME"
const val PROFILE_PIC = "PROFILE_PIC"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var buttonSubmit: Button? = null
    private var buttonPFP: Button? = null
    private var etFirstName: EditText? = null
    private var etMiddleName: EditText? = null
    private var etLastName: EditText? = null
    private var ivPFP: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSubmit = findViewById<View>(R.id.button_submit) as Button
        buttonPFP = findViewById<View>(R.id.button_pfp) as Button
        ivPFP = findViewById<View>(R.id.iv_pfp) as ImageView
        etFirstName = findViewById<View>(R.id.et_first_name) as EditText
        etMiddleName = findViewById<View>(R.id.et_middle_name) as EditText
        etLastName = findViewById<View>(R.id.et_last_name) as EditText

        buttonSubmit!!.setOnClickListener(this)
        buttonPFP!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_submit -> {
                if (etFirstName!!.text.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity,"Type in your first name.", Toast.LENGTH_SHORT).show()
                    // should be "this@MainActivity"
                    return
                } else if (etLastName!!.text.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, "Type in your last name.", Toast.LENGTH_SHORT).show()
                    return
                } else if (ivPFP!!.drawable == null) {
                    Toast.makeText(this@MainActivity, "Take a profile picture first.", Toast.LENGTH_SHORT).show()
                    return
                }

                // open second activity, passing in first & last name
                val intent = Intent(this@MainActivity, DisplayActivity::class.java)
                intent.putExtra(FIRST_NAME, etFirstName!!.text.trim().toString())
                intent.putExtra(LAST_NAME, etLastName!!.text.trim().toString())
                this.startActivity(intent)
            }
            R.id.button_pfp -> {
                // open camera intent
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraActivity.launch(cameraIntent)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Could not open camera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 33) {
                    val pfpThumbnail = result.data!!.getParcelableExtra("data", Bitmap::class.java)
                    ivPFP!!.setImageBitmap(pfpThumbnail)
                } else {
                    val pfpThumbnail = result.data!!.getParcelableExtra<Bitmap>("data")
                    ivPFP!!.setImageBitmap(pfpThumbnail)
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FIRST_NAME, etFirstName!!.text.toString())
        outState.putString(MIDDLE_NAME, etMiddleName!!.text.toString())
        outState.putString(LAST_NAME, etLastName!!.text.toString())

        // add pfp thumbnail to outstate
        val root = externalCacheDir
        val dir = File("$root/pfp_cache")
        dir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
        val filename = "pfp_$timeStamp.jpg"
        val file = File(dir, filename)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            ivPFP!!.drawToBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out)
            outState.putString(PROFILE_PIC, "$externalCacheDir/pfp_cache/$filename")
            out.flush()
            out.close()
        } catch (e: Exception) {}
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        etFirstName!!.setText(outState.getString(FIRST_NAME))
        etMiddleName!!.setText(outState.getString(MIDDLE_NAME))
        etLastName!!.setText(outState.getString(LAST_NAME))

        // set pfp thumbnail from outstate
        val pfpFilepath = outState.getString(PROFILE_PIC)
        if (pfpFilepath != null) {
            val bitmap: Bitmap? = BitmapFactory.decodeFile(pfpFilepath)
            if (bitmap != null) {
                ivPFP!!.setImageBitmap(bitmap)
            }
        }
    }


}