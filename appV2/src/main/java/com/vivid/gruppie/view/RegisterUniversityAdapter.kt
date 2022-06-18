package com.vivid.gruppie.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivid.gruppie.databinding.RegListItemUniversityBinding
import com.vivid.gruppie.interfaces.RegisterCallback
import com.vivid.gruppie.model.UniversityItem

class RegisterUniversityAdapter(
    val items: ArrayList<UniversityItem>, val callback: RegisterCallback
    ) : RecyclerView.Adapter<RegisterUniversityViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisterUniversityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RegListItemUniversityBinding.inflate(layoutInflater, parent, false)
        return RegisterUniversityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegisterUniversityViewHolder, position: Int) {
        val item = items[position]
        holder.bindTo(item, callback)
    }

}

class RegisterUniversityViewHolder(private val binding: RegListItemUniversityBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(item: UniversityItem?, callback: RegisterCallback) {
        binding.tvBoardName.text = item?.name
        item?.image?.let {
            Glide.with(binding.root).load(it).centerCrop().into(binding.ivBoardImage)
        }
        itemView.setOnClickListener {
            callback.onUniversityClicked(binding.tvBoardName.text.toString())
        }
    }

}