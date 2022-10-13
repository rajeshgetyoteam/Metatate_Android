package com.getyoteam.budamind.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.activity_receive.*
import java.io.File
import java.io.FileOutputStream


class ReceiveActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)
        var myaddress = MyApplication.prefs!!.myWallateAddress
        tvAddress.text = myaddress
        ivHeaderLeft.setOnClickListener { finish() }

//        val myBitmap: Bitmap = QRCode.from(myaddress).bitmap()
        val myImage: ImageView = findViewById<View>(R.id.ivQr) as ImageView

        try {
             bitmap = encodeAsBitmap(myaddress)!!
            myImage.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
//        myImage.setImageBitmap(myBitmap)

        tvShare.setOnClickListener {


            shareBitmap(bitmap!!, myaddress)
//            var addres = tvAddress.text
//            val shareIntent = Intent()
//            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.type="text/plain"
//            shareIntent.putExtra(Intent.EXTRA_TEXT, addres);
//            startActivity(Intent.createChooser(shareIntent, "Send To"))

//            val pathofBmp = Images.Media.insertImage(contentResolver, bitmap, "title", null)
//            val bmpUri: Uri = Uri.parse(pathofBmp)
//            val emailIntent1 = Intent(Intent.ACTION_SEND)
//            emailIntent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri)
//            emailIntent1.type = "image/png"
        }

        tvCopyAddress.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", tvAddress.getText().toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show()
        }

        tvExport.setOnClickListener {

            val intent = Intent(this, ExportPrivateActivity::class.java)
            startActivity(intent)
        }
    }
    private fun shareBitmap(bitmap: Bitmap, fileName: String) {
        try {
            val file = File(this.getCacheDir(), "$fileName.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags =Intent.FLAG_GRANT_READ_URI_PERMISSION

            val dirUri = FileProvider.getUriForFile(this, "com.getyoteam.budamind.fileprovider", file)
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            intent.putExtra(Intent.EXTRA_STREAM, dirUri)
            intent.putExtra(Intent.EXTRA_TEXT, "My Receive Address :  \n\n"+fileName);
            intent.type = "*/*"
            startActivity(Intent.createChooser(intent, "Send To"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @Throws(WriterException::class)
    fun encodeAsBitmap(str: String?): Bitmap? {
        val result: BitMatrix
        result = try {
            MultiFormatWriter().encode(
                str,
                BarcodeFormat.QR_CODE, 400, 400, null
            )
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, 400, 0, 0, w, h)
        return bitmap
    }


}
