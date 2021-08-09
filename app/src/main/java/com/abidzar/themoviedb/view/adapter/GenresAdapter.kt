package com.abidzar.themoviedb.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.model.data.genre.Genre
import kotlinx.android.synthetic.main.genre_item.view.*

class GenresAdapter(
    public val context: Context,
    private val genreList: List<Genre>,
    private val listener: OnItemClickListener
) :

    RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenresAdapter.GenreViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.genre_item, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenresAdapter.GenreViewHolder, position: Int) {
        (holder as GenreViewHolder).bind(genreList[position], context)
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        fun bind(genre: Genre?, context: Context) {

            itemView.txvGenre.text = genre?.name

            itemView.txvGenre.setOnClickListener(this)

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