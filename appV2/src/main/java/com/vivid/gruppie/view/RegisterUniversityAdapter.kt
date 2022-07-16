package com.vivid.gruppie.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
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
        holder.bindTo(item,position, callback)



    }

}

class RegisterUniversityViewHolder(private val binding: RegListItemUniversityBinding)
    : RecyclerView.ViewHolder(binding.root) {

    var selectedPosition = -1

    fun bindTo(item: UniversityItem?, position: Int, callback: RegisterCallback) {
        binding.radioButton.text = item?.name


        /*binding.radioButton.setChecked(position
                == selectedPosition)*/
        if ((selectedPosition == -1 && position == 0))
            binding.radioButton.setChecked(true)
        else
            if (selectedPosition == position)
                binding.radioButton.setChecked(true)
            else
                binding.radioButton.setChecked(false)


        binding.radioButton.setOnClickListener {
            selectedPosition=getAdapterPosition()
            callback.onUniversityClicked(binding.radioButton.text.toString())

        }


//        item?.image?.let {
//            Glide.with(binding.root).load(it).centerCrop().into(binding.ivBoardImage)
//        }

//        itemView.setOnClickListener {
//            callback.onUniversityClicked(binding.radioButton.text.toString())
//        }
    }

}