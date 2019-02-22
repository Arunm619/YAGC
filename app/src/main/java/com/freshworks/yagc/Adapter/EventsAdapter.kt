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
import com.freshworks.yagc.Model.EventsPublic.EventModel
import com.freshworks.yagc.R

class EventsAdapter(private var list: MutableList<EventModel>, private val mContext: Context) :
    RecyclerView.Adapter<EventsViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, p0: Int): EventsViewHolder {

        val itemView = inflater.inflate(R.layout.single_item_event, parent, false)
        return EventsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {


        Glide.with(mContext).load(list[position].actor.avatar_url).into(holder.iv_profile)
        holder.tv_username.text = list[position].actor.display_login

        holder.tvContent.text = list[position].repo.toString()



        holder.setItemClickListener(object : ItemClickListener {
            override fun onClick(view: View, position: Int, isLongClick: Boolean) {

                if (!isLongClick) {

                    val url = list[position].repo.url
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


    }
    fun deleteEvents() {
        list.clear()

    }

    fun addEvent(e: EventModel) {
        list.add(e)
    }



}

class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
    View.OnLongClickListener {

    override fun onClick(v: View?) {
        itemClickListener!!.onClick(v!!, adapterPosition, false)

    }

    override fun onLongClick(v: View?): Boolean {
        itemClickListener!!.onClick(v!!, adapterPosition, true)
        return true
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }


    var tv_username: TextView

    var tvContent: TextView

    var iv_profile: ImageView

    private var itemClickListener: ItemClickListener? = null


    init {
        tv_username = itemView.findViewById(R.id.tv_username)
        tvContent = itemView.findViewById(R.id.tv_content)
        iv_profile = itemView.findViewById(R.id.iv_profile)

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

}
