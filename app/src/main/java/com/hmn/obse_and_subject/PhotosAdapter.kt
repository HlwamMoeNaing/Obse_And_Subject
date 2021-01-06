package com.hmn.obse_and_subject

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotosAdapter(private val photos: List<Photo>, private val listener: PhotoListener) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        lateinit var photo: Photo
        private val photoImageView = v.findViewById<ImageView>(R.id.photo)

        init {
            v.setOnClickListener(this)
        }

        fun bind(photo: Photo) {
            this.photo = photo
            val bitmap =
                BitmapFactory.decodeResource(photoImageView.context.resources, photo.drawable)
            photoImageView.setImageDrawable(
                BitmapDrawable(
                    photoImageView.context.resources,
                    bitmap
                )
            )
        }

        override fun onClick(v: View?) {
            listener.photoClicked(this.photo)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosAdapter.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.list_item_photo))
    }

    override fun onBindViewHolder(holder: PhotosAdapter.ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int {
        return photos.size
    }


    interface PhotoListener {
        fun photoClicked(photo: Photo)
    }
}