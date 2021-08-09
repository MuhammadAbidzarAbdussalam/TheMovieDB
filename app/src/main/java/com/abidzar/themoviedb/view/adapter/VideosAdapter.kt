package com.abidzar.themoviedb.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.model.data.genre.Genre
import com.abidzar.themoviedb.model.data.videos.ResultVideos
import com.abidzar.themoviedb.viewmodel.VideosViewModel
import kotlinx.android.synthetic.main.genre_item.view.*
import kotlinx.android.synthetic.main.videos_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class VideosAdapter(
    public val context: Context,
    private val videoList: List<ResultVideos>,
    private val listener: VideosAdapter.OnItemClickListener
) :

    RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.videos_item, parent, false)
        return VideosViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        (holder as VideosViewHolder).bind(videoList[position], context)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    inner class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        fun bind(resultVideos: ResultVideos?, context: Context) {

            println("resultVideos?.name " + resultVideos?.name)

            itemView.txvName.text = resultVideos?.name
            itemView.txvType.text = resultVideos?.type
            itemView.txvSize.text = resultVideos?.size.toString()
            itemView.txvSite.text = resultVideos?.site

            val date = resultVideos?.published_at

            val format1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dt1: Date = format1.parse(date)

            val format2: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            val incepDate: String = format2.format(dt1)

            itemView.txvPublished.text = incepDate

            itemView.videoItem.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position: Int = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }


    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}