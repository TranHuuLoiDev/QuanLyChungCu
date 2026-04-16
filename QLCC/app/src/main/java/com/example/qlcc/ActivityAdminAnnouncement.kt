package com.example.qlcc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class activity_admin_announcement : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnPostAnnouncement: Button
    private lateinit var btnRemoveImage: Button
    private lateinit var ivPreview: ImageView
    private lateinit var layoutPreview: LinearLayout

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                showImagePreview(uri)
            }
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, " Đã chụp ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_announcement)

        etTitle = findViewById(R.id.et_title)
        etContent = findViewById(R.id.et_content)
        btnSelectImage = findViewById(R.id.btn_select_image)
        btnPostAnnouncement = findViewById(R.id.btn_post_announcement)
        btnRemoveImage = findViewById(R.id.btn_remove_image)
        ivPreview = findViewById(R.id.iv_preview)
        layoutPreview = findViewById(R.id.layout_preview)

        setupListeners()
    }

    private fun setupListeners() {
        btnSelectImage.setOnClickListener { showImageDialog() }
        btnPostAnnouncement.setOnClickListener { postAnnouncement() }
        btnRemoveImage.setOnClickListener { removeImage() }
    }

    private fun showImageDialog() {
        val options = arrayOf(" Chụp ảnh mới", " Chọn từ thư viện")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Hình ảnh minh họa ")
            .setItems(options) { _, which ->
                if (which == 0) openCamera()
                else pickImageFromGallery()
            }
            .show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            takePhotoLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            Toast.makeText(this, "Cần quyền Camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePreview(uri: Uri) {
        ivPreview.setImageURI(uri)
        layoutPreview.visibility = android.view.View.VISIBLE
    }

    private fun removeImage() {
        selectedImageUri = null
        ivPreview.setImageDrawable(null)
        layoutPreview.visibility = android.view.View.GONE
    }

    private fun postAnnouncement() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề thông báo", Toast.LENGTH_SHORT).show()
            return
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung thông báo", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri != null) {
            Toast.makeText(this, "Đang đăng thông báo kèm hình ảnh...", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Đang đăng thông báo...", Toast.LENGTH_LONG).show()
        }

    }
}
