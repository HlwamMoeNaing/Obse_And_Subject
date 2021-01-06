package com.hmn.obse_and_subject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class PhotosBottomDialogFragment : BottomSheetDialogFragment(), PhotosAdapter.PhotoListener {
    private lateinit var viewModel: SharedViewModel
    private val selectedPhotoSubject = PublishSubject.create<Photo>()

    val selectedPhoto: Observable<Photo> = selectedPhotoSubject.hide()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_photo_button_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val ctx = activity
        ctx?.let {
            viewModel = ViewModelProvider(ctx).get(SharedViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoRecy = view.findViewById<RecyclerView>(R.id.photosRecyclerView)
        photoRecy.layoutManager = GridLayoutManager(context, 3)
        photoRecy.adapter = PhotosAdapter(PhotoStore.photo, this)

    }

    override fun onDestroyView() {
        selectedPhotoSubject.onComplete()
        super.onDestroyView()
    }


    companion object {
        const val TAG = "PhotosBottomDialogFragment"
        fun newInstance(): PhotosBottomDialogFragment {
            return PhotosBottomDialogFragment()
        }
    }

    override fun photoClicked(photo: Photo) {
        selectedPhotoSubject.onNext(photo)
    }
}