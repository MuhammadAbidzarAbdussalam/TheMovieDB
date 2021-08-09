package com.abidzar.themoviedb.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.model.data.reviews.AuthorDetails
import com.abidzar.themoviedb.model.data.reviews.ReviewResult
import com.abidzar.themoviedb.model.network.posterBaseURL
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.view.ui.MovieDetailsActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.network_state_item.view.*
import kotlinx.android.synthetic.main.popular_item.view.*
import kotlinx.android.synthetic.main.reviews_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewsAdapter(public val context: Context) :
    PagedListAdapter<ReviewResult, RecyclerView.ViewHolder>(ReviewsDiffCallback()) {

    val REVIEW_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == REVIEW_VIEW_TYPE) {
            (holder as ReviewsItemViewHolder).bind(getItem(position), context)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == REVIEW_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.reviews_item, parent, false)
            return ReviewsItemViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            REVIEW_VIEW_TYPE
        }
    }

    class ReviewsDiffCallback : DiffUtil.ItemCallback<ReviewResult>() {
        override fun areItemsTheSame(oldItem: ReviewResult, newItem: ReviewResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewResult, newItem: ReviewResult): Boolean {
            return oldItem == newItem
        }

    }

    class ReviewsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(review: ReviewResult?, context: Context) {

            itemView.txvAuthorName.text = review?.author

            val date = review?.created_at

            val format1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dt1: Date = format1.parse(date)

            val format2: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            val incepDate: String = format2.format(dt1)

            itemView.txvCreatedDate.text = incepDate

            val authorDetails: AuthorDetails = review?.author_details!!

            var authorPosterURL: String = authorDetails.avatar_path

            authorPosterURL?.let {
                authorPosterURL = if (authorPosterURL.contains("secure.gravatar.com")) {
                    val n = 1
                    authorPosterURL.drop(n)
                } else {
                    posterBaseURL + authorPosterURL
                }
            }

            Glide.with(itemView.context)
                .load(authorPosterURL)
                .error(R.color.black)
                .into(itemView.imvAuthor)

            if (authorDetails.rating != null) {
                itemView.txvRate.text = authorDetails.rating.toString()

                val rating: Double = authorDetails.rating as Double

                itemView.ratingBar.rating = rating.toFloat()
            } else {
                itemView.txvRate.text = "0.0"
            }

            itemView.txvReview.text = review?.content

//            itemView.imvPopular.setOnClickListener(View.OnClickListener {
//                val intent = Intent(context, MovieDetailsActivity::class.java)
//                intent.putExtra("id", review?.id)
//                context.startActivity(intent)
//            })

        }
    }

    class NetworkStateItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.progressBarItem.visibility = View.VISIBLE
            } else {
                itemView.progressBarItem.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.txvErrorMsgItem.visibility = View.VISIBLE
                itemView.txvErrorMsgItem.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                itemView.txvErrorMsgItem.visibility = View.VISIBLE
                itemView.txvErrorMsgItem.text = networkState.msg
            } else {
                itemView.txvErrorMsgItem.visibility = View.GONE
            }

        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState: NetworkState? = this.networkState
        val hadExtraRow: Boolean = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow: Boolean = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }

    }

}