package com.albertsons.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.albertsons.myapplication.api.models.Lf
import com.albertsons.myapplication.databinding.ItemFullformBinding

class FullFormAdapter(
    private val fullFormsList: List<Lf>
) : RecyclerView.Adapter<LfViewHolder>() {

    private lateinit var binding: ItemFullformBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LfViewHolder {
        binding = ItemFullformBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LfViewHolder, position: Int) {
        val largeNews = fullFormsList[position]
        holder.bind(largeNews)
    }

    override fun getItemCount(): Int = fullFormsList.size

}

class LfViewHolder(
    private val binding: ItemFullformBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(fullForms: Lf) {
        binding.lf = fullForms
    }
}