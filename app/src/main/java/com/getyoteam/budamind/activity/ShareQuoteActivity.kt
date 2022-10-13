package com.getyoteam.budamind.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getyoteam.budamind.R
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.activity_share_quote.*
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.content.Intent
import androidx.core.content.FileProvider


class ShareQuoteActivity : AppCompatActivity() {

    private var strQuote: String? = ""
    private var imgUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_quote)
        if (intent.extras!!.containsKey("img")) {
            imgUrl = intent.extras!!.getString("img")
        }
        if (intent.extras!!.containsKey("quote")) {
            strQuote = intent.extras!!.getString("quote")
        }

        Glide.with(this)
            .load(imgUrl)
            .placeholder(ColorDrawable(ContextCompat.getColor(this, R.color.color_blue)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(ivBanner)

        tvQuote.setText(strQuote)

        tvShareNow.setOnClickListener{
            val bitmap=screenShot(rlBitmap)
            val imgUri = saveImage(bitmap)
            shareImageUri(imgUri!!)
        }

        ivHeaderLeft.setOnClickListener{
            finish()
        }
    }

    fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.getWidth(),
            view.getHeight(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveImage(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, getString(R.string.app_name) + "_" + System.currentTimeMillis() + ".png")

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(this, "com.getyoteam.budamind.fileprovider", file)

        } catch (e: IOException) {
            Log.d(
                ShareQuoteActivity::class.java.simpleName,
                "IOException while trying to write file for sharing: " + e.message
            )
        }
        return uri
    }

    private fun shareImageUri(uri: Uri) {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }
}
