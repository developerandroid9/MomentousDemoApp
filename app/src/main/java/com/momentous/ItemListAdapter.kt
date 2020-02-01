package com.momentous

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.momentous.model.DataModels
import kotlinx.android.synthetic.main.list_item.view.*

/*List Item Adapter class */
class ItemListAdapter(
    private var dataList: MutableList<DataModels>,
    var loadMore: (() -> Unit)? = null,
    var onItemClickedListener: OnItemClickedListener
) : RecyclerView.Adapter<ItemListAdapter.DataItemsViewHolder>() {
    override fun getItemCount(): Int = dataList.size
    private var isAscending: Boolean? = true
    private var isSearching = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataItemsViewHolder {
        return DataItemsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: DataItemsViewHolder, position: Int) {
        Log.e("list size", "" + dataList.size)
        holder.bindPost(dataList[position])

        if (position == dataList.lastIndex && !isSearching) {
            loadMore?.invoke()
        }

    }


    ///*search items in local loaded list items*/
    fun searchItems(text: String?) {
        if (!text.isNullOrEmpty()) {
            isSearching = true
            dataList = dataList.filter {
                text.let { it1 ->
                    it.name?.contains(
                        it1,
                        true
                    )
                } == true
            } as MutableList<DataModels>
        } else {
            isSearching = false
        }
        notifyDataSetChanged()

    }

    fun updateListItems(
        dataItems: MutableList<DataModels>,
        isSearching: Boolean,
        isAscending: Boolean?
    ) {
        dataList = dataItems
        this.isAscending = isAscending
        this.isSearching = isSearching
        notifyDataSetChanged()
    }

    /*sort the list items either in ascending or descending order*/
    fun sortItems() {
        isAscending = if (isAscending == true) {
            dataList.sortByDescending { it.name }
            false
        } else {
            dataList.sortBy { it.name }
            true
        }
        notifyDataSetChanged()
    }


    /*View holder to contain list  items*/
    inner class DataItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView? = itemView.imageView
        private var nameTV: TextView? = itemView.nameTV
        private var descriptionTV: TextView? = itemView.descriptionTV
        fun bindPost(dataModel: DataModels) {
            with(dataModel) {
                nameTV?.text = name
                descriptionTV?.text = description
                loadWithGlide(imageUrl, imageView)
                itemView.setOnClickListener {
                    onItemClickedListener.onItemClicked(this)
                }
            }
        }
    }
}