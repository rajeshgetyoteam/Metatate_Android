package com.getyoteam.budamind.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.getyoteam.budamind.Model.CommanResponseModel
import com.getyoteam.budamind.Model.ProfileImage
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ClarityAPI
import com.getyoteam.budamind.utils.ManagePermissions
import kotlinx.android.synthetic.main.activity_update_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.regex.Pattern


class UpdateProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var isFirstTime: Boolean = false
    private var wallateStatus: String = ""
    private var tokenCredited: String = ""
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
    var profileImageModel: ProfileImage? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private lateinit var list: List<String>
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        userId = MyApplication.prefs!!.userId
        list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        isFirstTime = intent.getBooleanExtra("isFirstTime", false)
        wallateStatus = intent.getStringExtra("wallateStatus")
        tokenCredited = intent.getStringExtra("credited")

        etEmail.text = MyApplication.prefs!!.email


        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        tvSignUp.setOnClickListener {

            val lastName = etLastName.text.toString()
            val firstName = etFirstName.text.toString()
            if (TextUtils.isEmpty(firstName)) {
                etFirstName.setError(getString(R.string.str_please_enter_first_name))
            } else if (TextUtils.isEmpty(lastName)) {
                etLastName.setError(getString(R.string.str_please_enter_last_name))
            } else {
                updateUserDetail()
            }
        }

        ivEditCamera.setOnClickListener {
            openCameraWithPermission()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun updateUserDetail(
    ) {
        progressBarSignIn.visibility = View.VISIBLE
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mindFulNessAPI = retrofit.create(ClarityAPI::class.java)
        val userMap: HashMap<String, String> = HashMap<String, String>()
        userMap.put("userId", userId)
        userMap.put("firstName", etFirstName.text.toString())
        userMap.put("lastName", etLastName.text.toString())
        if (profileImageModel != null)
            userMap.put("profilePic", profileImageModel!!.getFileUrl().toString())

        val call = mindFulNessAPI.updateProfile(userMap)

        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {

                progressBarSignIn.visibility = View.GONE
                Toast.makeText(
                    this@UpdateProfileActivity,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {
                if (response.code() == 200) {

                    val commanResponseModel = response.body()!!
                    if (commanResponseModel.getStatus().equals(getString(R.string.str_success))) {

                        progressBarSignIn.visibility = View.GONE
                        if (MyApplication.prefs!!.isFirstApp!!) {
                            MyApplication.prefs!!.first_name = etFirstName.text.toString()
                            MyApplication.prefs!!.last_name = etLastName.text.toString()
                            val intent = Intent(applicationContext, ChooseYourGoalActivity::class.java)
                            intent.putExtra("isFirstTime", true)
                            intent.putExtra("wallateStatus", wallateStatus)
                            intent.putExtra("credited", tokenCredited)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent)
                        } else {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent)
                        }

                        finish()


                    } else {
                        progressBarSignIn.visibility = View.GONE
                        Toast.makeText(
                            this@UpdateProfileActivity,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun openCameraWithPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    list.toTypedArray(),
                    PermissionsRequestCode
                )
            } else {
                showPictureDialog()
            }

        } else {
            showPictureDialog()
        }
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    showPictureDialog()
                } else {
                }
                return
            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this!!.contentResolver,
                            contentURI
                        )
                        saveImage(bitmap)
                        Glide.with(this)
                            .load(bitmap)
                            .into(ivUserPic)
//                    ivProfilePic!!.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            } else if (requestCode == CAMERA) {

                if (data != null) {
                    try {

                        val thumbnail = data!!.extras!!.get("data") as Bitmap
                        Glide.with(this)
                            .load(thumbnail)
                            .into(ivUserPic)
//            ivProfilePic!!.setImageBitmap(thumbnail)

                        saveImage(thumbnail)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

            }
        }

    }


    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)


        val wallpaperDirectory =
            File((Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY))

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

//        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var file: File? = null
//        try {
//            file = File.createTempFile(timeStamp, ".jpg", storageDir)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        var mCurrentPhotoPath = java.lang.String.valueOf(Uri.fromFile(file))
//        uploadProfilePic(file!!)
//
//        return file.toString()

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val imgFile = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg")
            )
            imgFile.createNewFile()
            val fo = FileOutputStream(imgFile)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(imgFile.getPath()),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + imgFile.getAbsolutePath())
            uploadProfilePic(imgFile)

            return imgFile.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }

    private fun uploadProfilePic(imgFile: File) {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            imgFile.getName(),
            RequestBody.create(MediaType.parse("image/*"), imgFile)
        );
        val imageRequestBody = RequestBody.create(MediaType.parse("text/plain"), "customer_image");
        val mindFulNessAPI = retrofit.create(ClarityAPI::class.java)

        val call = mindFulNessAPI.updateUserProfilePhoto(
            multipartBody,
            imageRequestBody
        )

        call.enqueue(object : Callback<ProfileImage> {
            override fun onFailure(call: Call<ProfileImage>, t: Throwable) {
                Toast.makeText(
                    this@UpdateProfileActivity,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<ProfileImage>, response: Response<ProfileImage>) {
                if (response.code() == 200) {
                    profileImageModel = response.body()!!
                    Toast.makeText(
                        this@UpdateProfileActivity,
                        "Photo uploaded successfully!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    MyApplication.prefs!!.profilePic = profileImageModel!!.getFileUrl()!!
//                    if (profileImageModel != null)
//                        Glide.with(requireActivity())
//                            .load(profileImageModel!!.getFileUrl())
//                            .into(ivUserPic)
                } else {
                    Toast.makeText(
                        this@UpdateProfileActivity,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        })
    }

    private fun hideKeyBoard() {
        val imm =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    fun isValidEmailAddress(email: String): Boolean {
        val emailPattern =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }


}
