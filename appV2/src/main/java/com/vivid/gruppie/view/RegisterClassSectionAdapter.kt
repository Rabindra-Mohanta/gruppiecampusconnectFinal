package com.vivid.gruppie.view

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivid.gruppie.databinding.RegListItemClassesBinding
import com.vivid.gruppie.databinding.RegListItemUniversityBinding
import com.vivid.gruppie.interfaces.RegisterCallback
import com.vivid.gruppie.model.ClassItem
import com.vivid.gruppie.model.UniversityItem
import java.util.ArrayList

class RegisterClassSectionAdapter(
    val items: ArrayList<ClassItem>, val callback: RegisterCallback
    ) : RecyclerView.Adapter<RegisterClassSectionViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisterClassSectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RegListItemClassesBinding.inflate(layoutInflater, parent, false)
        return RegisterClassSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegisterClassSectionViewHolder, position: Int) {
        val item = items[position]
        holder.bindTo(item, callback)
    }
}

class RegisterClassSectionViewHolder(private val binding: RegListItemClassesBinding) : RecyclerView.ViewHolder(binding.root) {

    var et1 : Int = 0
    var et2 : Int = 0
    var et3 : Int = 0
    var et4 : Int = 0
    var et5 : Int = 0


    fun bindTo(item: ClassItem, callback: RegisterCallback) {
        binding.cbSectionName.text = item.type

        binding.cbSectionName.setOnCheckedChangeListener { button, isChecked ->
            item.classTypeId?.let {
                if (!isChecked) {
                    binding.etSections1Data.setText("0")
                    binding.etSections2Data.setText("0")
                    binding.etSections3Data.setText("0")
                    binding.etSections4Data.setText("0")
                    binding.etSections4Data.setText("0")

               //     binding.etSections3.setText("")
                /*    binding.etSections4.setText("")
                    binding.etSections5.setText("")*/
                }
                callback.onCheckBoxChanged(it, isChecked)
            }

            if (isChecked) {
                item._class?.let { classes ->

                    binding.tvTitleClassName.visibility = View.GONE
                    binding.tvTitleNoSections.visibility = View.GONE

                    binding.tvClassName1.visibility = View.GONE
                    binding.tvClassName2.visibility = View.GONE
                    binding.tvClassName3.visibility = View.GONE
                    binding.tvClassName4.visibility = View.GONE
                    binding.tvClassName5.visibility = View.GONE

                    binding.etSections1.visibility = View.GONE
                    binding.etSections2.visibility = View.GONE
                    binding.etSections3.visibility = View.GONE
                    binding.etSections4.visibility = View.GONE
                    binding.etSections5.visibility = View.GONE

                    if (classes.size > 1) {
                        binding.tvTitleClassName.visibility = View.VISIBLE
                        binding.tvTitleNoSections.visibility = View.VISIBLE
                    }
                    if (classes.size > 0) {
                        binding.tvClassName1.text = item._class[0]
                        binding.tvClassName1.visibility = View.VISIBLE
                        binding.etSections1.visibility = View.VISIBLE
                    }
                    if (classes.size > 1) {
                        binding.tvClassName2.text = item._class[1]
                        binding.tvClassName2.visibility = View.VISIBLE
                        binding.etSections2.visibility = View.VISIBLE
                    }
                    if (classes.size > 2) {
                        binding.tvClassName3.text = item._class[2]
                        binding.tvClassName3.visibility = View.VISIBLE
                        binding.etSections3.visibility = View.VISIBLE
                    }
                    if (classes.size > 3) {
                        binding.tvClassName4.text = item._class[3]
                        binding.tvClassName4.visibility = View.VISIBLE
                        binding.etSections4.visibility = View.VISIBLE
                    }
                    if (classes.size > 4) {
                        binding.tvClassName5.text = item._class[4]
                        binding.tvClassName5.visibility = View.VISIBLE
                        binding.etSections5.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.tvTitleClassName.visibility = View.GONE
                binding.tvTitleNoSections.visibility = View.GONE

                binding.tvClassName1.visibility = View.GONE
                binding.tvClassName2.visibility = View.GONE
                binding.tvClassName3.visibility = View.GONE
                binding.tvClassName4.visibility = View.GONE
                binding.tvClassName5.visibility = View.GONE

                binding.etSections1.visibility = View.GONE
                binding.etSections2.visibility = View.GONE
                binding.etSections3.visibility = View.GONE
                binding.etSections4.visibility = View.GONE
                binding.etSections5.visibility = View.GONE
            }
        }

        /*
        binding.etSections1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val text = binding.tvClassName1.text.toString()
                val inputCount = binding.etSections1.text.toString()
                if (!inputCount.isNullOrEmpty()) {
                    callback.onCountChanged(text , inputCount.toInt())
                } else {
                    callback.onCountChanged(text, 0)
                }
            }
        })

        binding.etSections2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val text = binding.tvClassName2.text.toString()
                val inputCount = binding.etSections2.text.toString()
                if (!inputCount.isNullOrEmpty()) {
                    callback.onCountChanged(text , inputCount.toInt())
                } else {
                    callback.onCountChanged(text, 0)
                }
            }
        })

        binding.etSections3Data.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {


                val text = binding.tvClassName3.text.toString()
                val inputCount = binding.etSections3Data.text.toString()

                Log.e("RegisterClass","MSg $inputCount")
                if (!inputCount.isNullOrEmpty()) {
                    callback.onCountChanged(text , inputCount.toInt())
                } else {
                    callback.onCountChanged(text, 0)
                }
            }
        })

        binding.etSections4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val text = binding.tvClassName4.text.toString()
                val inputCount = binding.etSections4.text.toString()
                if (!inputCount.isNullOrEmpty()) {
                    callback.onCountChanged(text , inputCount.toInt())
                } else {
                    callback.onCountChanged(text, 0)
                }
            }
        })

        binding.etSections5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val text = binding.tvClassName5.text.toString()
                val inputCount = binding.etSections5.text.toString()
                if (!inputCount.isNullOrEmpty()) {
                    callback.onCountChanged(text , inputCount.toInt())
                } else {
                    callback.onCountChanged(text, 0)
                }
            }
        })*/


        binding.imgMin1.visibility = View.GONE
        binding.imgMin2.visibility = View.GONE
        binding.imgMin3.visibility = View.GONE
        binding.imgMin4.visibility = View.GONE
        binding.imgMin5.visibility = View.GONE


        binding.imgMin1.setOnClickListener{

            et1 -= 1;
            binding.etSections1Data.setText(et1.toString())
            if (et1 == 0)
            {
                binding.imgMin1.visibility = View.GONE
            }

            val text = binding.tvClassName1.text.toString()
            callback.onCountChanged(text, et1)
        }

        binding.imgMax1.setOnClickListener{

            binding.imgMin1.visibility = View.VISIBLE
            et1 += 1;
            binding.etSections1Data.setText(et1.toString())

            val text = binding.tvClassName1.text.toString()
            callback.onCountChanged(text, et1)
        }



        binding.imgMin2.setOnClickListener{

            et2 -= 1;
            binding.etSections2Data.setText(et2.toString())
            if (et2 == 0)
            {
                binding.imgMin2.visibility = View.GONE
            }

            val text = binding.tvClassName2.text.toString()
            callback.onCountChanged(text, et2)

        }

        binding.imgMax2.setOnClickListener{

            binding.imgMin2.visibility = View.VISIBLE
            et2 += 1;
            binding.etSections2Data.setText(et2.toString())

            val text = binding.tvClassName2.text.toString()
            callback.onCountChanged(text, et2)

        }



        binding.imgMin3.setOnClickListener{

            et3 -= 1;
            binding.etSections3Data.setText(et3.toString())
            if (et3 == 0)
            {
                binding.imgMin3.visibility = View.GONE
            }

            val text = binding.tvClassName3.text.toString()
            callback.onCountChanged(text, et3)
        }

        binding.imgMax3.setOnClickListener{

            binding.imgMin3.visibility = View.VISIBLE
            et3 += 1;
            binding.etSections3Data.setText(et3.toString())

            val text = binding.tvClassName3.text.toString()
            callback.onCountChanged(text, et3)
        }


        binding.imgMin4.setOnClickListener{

            et4 -= 1;
            binding.etSections4Data.setText(et4.toString())
            if (et4 == 0)
            {
                binding.imgMin4.visibility = View.GONE
            }

            val text = binding.tvClassName4.text.toString()
            callback.onCountChanged(text, et4)
        }

        binding.imgMax4.setOnClickListener{

            binding.imgMin4.visibility = View.VISIBLE
            et4 += 1;
            binding.etSections4Data.setText(et4.toString())

            val text = binding.tvClassName4.text.toString()
            callback.onCountChanged(text, et4)
        }

        binding.imgMin5.setOnClickListener{

            et5 -= 1;
            binding.etSections5Data.setText(et5.toString())
            if (et5 == 0)
            {
                binding.imgMin5.visibility = View.GONE
            }

            val text = binding.tvClassName5.text.toString()
            callback.onCountChanged(text, et5)
        }

        binding.imgMax5.setOnClickListener{

            binding.imgMin5.visibility = View.VISIBLE
            et5 += 1;
            binding.etSections5Data.setText(et5.toString())

            val text = binding.tvClassName5.text.toString()
            callback.onCountChanged(text, et5)
        }




        itemView.setOnClickListener {

        }
    }

}