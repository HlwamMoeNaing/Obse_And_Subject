package com.hmn.obse_and_subject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SharedViewModel : ViewModel() {


    private val selectedPhoto = MutableLiveData<List<Photo>>()
    private val disposable = CompositeDisposable()
    private val imageBehaviorSubject: BehaviorSubject<MutableList<Photo>> =
        BehaviorSubject.createDefault(mutableListOf())


    init {

        imageBehaviorSubject.subscribe { photos ->
            selectedPhoto.value = photos
        }.addTo(disposable)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
    fun getSelectedPhotos(): LiveData<List<Photo>> {
        return selectedPhoto
    }

    fun subscribegetSelectedPhotos(selectedPhotos: Observable<Photo>) {
        selectedPhotos.doOnComplete {
            Log.v("SharedViewModel", "Completed selecting photos")
        }.subscribe { photo ->
            imageBehaviorSubject.value.add(photo)
            imageBehaviorSubject.onNext(imageBehaviorSubject.value ?: mutableListOf())
        }.addTo(disposable)
    }

    fun clearPhoto() {
        imageBehaviorSubject.value.clear()
        imageBehaviorSubject.onNext(imageBehaviorSubject.value)
    }

    fun saveBitmapFromImageView(imageView: ImageView, context: Context): Single<String> {
        return Single.create { observer ->
            val tmpImg = "${System.currentTimeMillis()}.png"

            val os: OutputStream
            val collagesDirectory = File(context.getExternalFilesDir(null), "collages")
            if (!collagesDirectory.exists()) {
                collagesDirectory.mkdir()
            }

            val file = File(collagesDirectory, tmpImg)
            try {
                os = FileOutputStream(file)
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                os.flush()
                os.close()
                observer.onSuccess(tmpImg)
            } catch (e: IOException) {
                observer.onError(e)
            }

        }
    }

}