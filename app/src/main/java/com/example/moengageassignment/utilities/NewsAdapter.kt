package com.example.moengageassignment.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moengageassignment.R
import com.example.moengageassignment.models.Article
import java.util.Locale

class NewsAdapter(private val newsList: List<Article>?) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(), Filterable {
    private var filteredNewsList: List<Article>? = newsList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item_layout, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList?.get(position)

        // Bind data to views
        Glide.with(holder.imageView.context)
            .load(newsItem!!.urlToImage)
            .centerCrop()
            .into(holder.imageView)

        holder.titleTextView.text = newsItem.title
        holder.descriptionTextView.text = newsItem.description

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url))
            holder.itemView.context.startActivity(intent)
        }

        holder.sharetext.setOnClickListener {
            onShareClick(newsItem, holder.imageView.context)
        }


        holder.shareImage.setOnClickListener {
            onShareClick(newsItem, holder.imageView.context)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val queryString = constraint?.toString()?.toLowerCase(Locale.getDefault())

                filteredNewsList = if (queryString.isNullOrBlank()) {
                    newsList
                } else {
                    newsList!!.filter {
                        it.title!!.toLowerCase(Locale.getDefault()).contains(queryString) ||
                                it.description!!.toLowerCase(Locale.getDefault()).contains(queryString)
                    }
                }

                filterResults.values = filteredNewsList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return newsList!!.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val sharetext: TextView = itemView.findViewById(R.id.share_text)
        val shareImage: ImageView = itemView.findViewById(R.id.share_button)
    }

    private fun onShareClick(newsItem: Article, context: Context) {
        // Handle share button click
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, newsItem.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, "${newsItem.title}\n${newsItem.url}")

        context.startActivity(Intent.createChooser(shareIntent, "Share news"))
    }
}