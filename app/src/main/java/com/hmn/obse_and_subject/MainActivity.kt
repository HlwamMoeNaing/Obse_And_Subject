package com.hmn.obse_and_subject

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel
    lateinit var progressBar: ProgressBar
    lateinit var collageImage: ImageView
    lateinit var clearButton: Button
    lateinit var addButton: Button
    lateinit var saveButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        addButton = findViewById<Button>(R.id.addButton)
        clearButton = findViewById<Button>(R.id.clearButton)
        saveButton = findViewById<Button>(R.id.saveButton)
        collageImage = findViewById<ImageView>(R.id.collageImage)

        title = getString(R.string.collage)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        addButton.setOnClickListener {
            actionAdd()
        }
        clearButton.setOnClickListener {
            actionClear()
        }
        saveButton.setOnClickListener {
            actionSave()
        }

        viewModel.getSelectedPhotos().observe(this, Observer { photos ->
            photos.let {
                if (photos.isNotEmpty()) {
                    val bitmaps =
                        photos.map { BitmapFactory.decodeResource(resources, it.drawable) }
                    val newBitmap = combineImages(bitmaps)
                    collageImage.setImageDrawable(BitmapDrawable(resources, newBitmap))

                } else {
                    collageImage.setImageResource(android.R.color.transparent)
                }
                updateUi(photos)
            }

        })

    }

    private fun actionAdd() {
        val addPhotoBottomDialogFragment = PhotosBottomDialogFragment.newInstance()
        addPhotoBottomDialogFragment.show(supportFragmentManager, PhotosBottomDialogFragment.TAG)
        viewModel.subscribegetSelectedPhotos(
            addPhotoBottomDialogFragment.selectedPhoto
        )
    }

    private fun actionClear() {
        viewModel.clearPhoto()
    }

    private fun actionSave() {
        progressBar.visibility = View.VISIBLE
        viewModel.saveBitmapFromImageView(collageImage, applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { file ->
                    Toast.makeText(this, "$file saved", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                },
                onError = { error ->
                    Toast.makeText(
                        this,
                        "Error saving file :${error.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                }
            )
    }

    private fun updateUi(photo: List<Photo>) {
        saveButton.isEnabled = photo.isNotEmpty() && (photo.size % 2 == 0)
        clearButton.isEnabled = photo.isNotEmpty()
        addButton.isEnabled = photo.size < 6
        title = if (photo.isNotEmpty()) {
            resources.getQuantityString(R.plurals.photos_format, photo.size, photo.size)
        } else {
            getString(R.string.collage)
        }

    }
}