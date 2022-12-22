package Telegram.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val context: Context, private val REitems: List<MessageDTO>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val user = view.findViewById<TextView>(R.id.messageUser)!!
        val date = view.findViewById<TextView>(R.id.messageTime)!!
        val text = view.findViewById<TextView>(R.id.messageText)!!
    }

    class ViewHolderImg(view: View) : RecyclerView.ViewHolder(view) {
        val fieldImg: LinearLayout = view.findViewById(R.id.messageImageField)
        val imgMessage: ImageView = view.findViewById(R.id.ImageUserMessege)
        val imgUser = view.findViewById<TextView>(R.id.ImageUser)!!
        val imgTime = view.findViewById<TextView>(R.id.ImageTime)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) ViewHolderImg(
            LayoutInflater.from(context)
                .inflate(R.layout.imageitem, parent, false)
        )
        else ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.messegeitem, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataaa = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        if (getItemViewType(position) != 1) {
            holder as ViewHolderImg
            holder.imgUser.text = REitems[position].user
            holder.imgMessage.setImageBitmap(REitems[position].message.image!!.image)
            holder.imgTime.text = REitems[position].time?.let { Date(it.toLong()) }
                ?.let { dataaa.format(it) }
            val back = AppCompatResources.getDrawable(context, R.drawable.sendback)
            val new = back?.let { DrawableCompat.wrap(it) }
            holder.fieldImg.background = new
            holder.imgMessage.setOnClickListener {
                val activity = Intent(context, ImageContributor::class.java)
                activity.putExtra("id", REitems[position].num!!)
                context.startActivity(activity)
            }
        } else {
            holder as ViewHolder
            holder.user.text = REitems[position].user
            holder.text.text = REitems[position].message.text!!.text
            REitems[position].time?.let { Date(it.toLong()) }?.let { dataaa.format(it) }
                .apply { holder.date.text = this }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (REitems[position].message.image != null) {
            return 0
        }
        return 1
    }

    override fun getItemCount(): Int {
        return REitems.size
    }
}