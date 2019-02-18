package com.freshworks.yagc.Adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.freshworks.yagc.Interface.ItemClickListener
import com.freshworks.yagc.Model.RSSFeed.AtomFeedModel
import com.freshworks.yagc.R


class FeedAdapter(private val AtomFeedObject: AtomFeedModel, private val mContext: Context) :
    RecyclerView.Adapter<FeedViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): FeedViewHolder {
        val itemView = inflater.inflate(R.layout.list_row, parent, false)
        return FeedViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return AtomFeedObject.items.size
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.tvTitle.text = AtomFeedObject.items[position].title

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvContent.text =
                (Html.fromHtml(
                    AtomFeedObject.items[position].content_html
                    , Html.FROM_HTML_MODE_COMPACT
                ))
        } else {
            holder.tvContent.text = (Html.fromHtml(
                AtomFeedObject.items[position].content_html
            ))
        }


        holder.setItemClickListener(object : ItemClickListener {
            override fun onClick(view: View, position: Int, isLongClick: Boolean) {

                if (!isLongClick) {

                    val url = AtomFeedObject.items[position].url
                    try {
                        val builder = CustomTabsIntent.Builder()
                        builder.setToolbarColor(
                            ContextCompat.getColor(
                                mContext,
                                com.freshworks.yagc.R.color.colorPrimaryDark
                            )
                        )
                        val customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(mContext, Uri.parse(url))
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        Toast.makeText(mContext, "You don't have a browser", Toast.LENGTH_LONG).show()
                    }

                }
            }
        })

        if (AtomFeedObject.items[position].thumbnail != null && AtomFeedObject.items[position].thumbnail.isNotEmpty())
            Glide.with(mContext).load(AtomFeedObject.items[position].thumbnail).into(holder.iv_profile);


    }

}

class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
    View.OnLongClickListener {


    var tvTitle: TextView

    var tvContent: TextView

    var iv_profile: ImageView

    private var itemClickListener: ItemClickListener? = null


    init {
        tvTitle = itemView.findViewById(R.id.tv_title)
        tvContent = itemView.findViewById(R.id.tv_content)
        iv_profile = itemView.findViewById(R.id.iv_profile)

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }


    override fun onClick(v: View?) {
        itemClickListener!!.onClick(v!!, adapterPosition, false)

    }

    override fun onLongClick(v: View?): Boolean {
        itemClickListener!!.onClick(v!!, adapterPosition, true)
        return true
    }


}