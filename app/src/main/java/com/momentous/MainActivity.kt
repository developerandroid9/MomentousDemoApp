package com.momentous

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.momentous.model.DataModels
import com.momentous.viewmodel.MainViewModel
import com.momentous.webservice.itemsData
import kotlinx.android.synthetic.main.activity_main.*


const val PAGE_SIZE = 10

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    View.OnClickListener, SearchView.OnCloseListener, OnItemClickedListener {

    private var mainViewModel: MainViewModel? = null


    override fun onItemClicked(dataModel: DataModels) {
        startActivity(
            Intent(
                this,
                DescriptionActivity::class.java
            ).apply { putExtras(Bundle().apply { putSerializable("dataModel", dataModel) }) })
    }

    override fun onClose(): Boolean {
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sortIB -> {
                (itemRV?.adapter as ItemListAdapter).sortItems()
            }
        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            (itemRV?.adapter as ItemListAdapter).updateListItems(
                mainViewModel?.responseData?.value?.dataItems ?: mutableListOf(), false
                , mainViewModel?.isAscending
            )
        } else
            (itemRV?.adapter as ItemListAdapter).searchItems(newText)
        return false
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.local -> {
                if (mainViewModel?.fromHost == true) {
                    mainViewModel?.isReferesh = true
                    mainViewModel?.fromHost = false
                    mainViewModel?.currentPageNumber = 0
                    if (itemsData?.dataItems?.isEmpty() == true)
                        mainViewModel?.getListItems(this)
                    else {

                        loadMore()
                    }
                    item.isChecked = true

                }
                return false
            }
            R.id.host -> {
                if (mainViewModel?.fromHost == false) {
                    mainViewModel?.isReferesh = true
                    mainViewModel?.currentPageNumber = 0
                    mainViewModel?.fromHost = true
                    loadMore()
                    item.isChecked = true
                }
                return false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider.NewInstanceFactory().create(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        searchView?.setOnQueryTextListener(this)
        sortIB?.setOnClickListener(this)
        searchView?.setOnCloseListener(this)
        setSupportActionBar(toolbar)
        setItems()

    }


    private fun setItems() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val itemAdapter = ItemListAdapter(mutableListOf(), ::loadMore, this)
        itemRV?.apply {
            layoutManager = linearLayoutManager
            adapter = itemAdapter
        }
        mainViewModel = ViewModelProvider.NewInstanceFactory().create(MainViewModel::class.java)

        mainViewModel?.getListItems(this)
        updateUI()

    }

    private fun updateUI() {
        mainViewModel?.isLoadMore?.observe(this, Observer {
            if (!it)
                (itemRV.adapter as ItemListAdapter).loadMore = null
        })
        mainViewModel?.getLiveItems()?.observe(this, Observer {
            (itemRV.adapter as ItemListAdapter).updateListItems(
                it.dataItems,
                false,
                mainViewModel?.isAscending
            )
            if (it.totalElements <= it.dataItems.size)
                (itemRV.adapter as ItemListAdapter).loadMore = null

        })
        mainViewModel?.isLoading?.observe(this, Observer {
            if (it) progressPB.visibility = View.VISIBLE else progressPB.visibility = View.GONE
        })
        mainViewModel?.message?.observe(this, Observer {
            if (it != null)
                showToast(it, this)
        })


    }

    private fun loadMore() {
        if (mainViewModel?.fromHost == true)
            mainViewModel?.getListFromServer()
        else
            mainViewModel?.getListItems(this)
    }


}


interface OnItemClickedListener {
    fun onItemClicked(dataModel: DataModels)
}

