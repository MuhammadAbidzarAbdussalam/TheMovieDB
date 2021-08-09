package com.abidzar.themoviedb.view.ui.fragment.genres

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abidzar.themoviedb.databinding.FragmentGenresBinding
import com.abidzar.themoviedb.model.data.genre.Genre
import com.abidzar.themoviedb.model.network.Instance
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.repository.GenresRepository
import com.abidzar.themoviedb.view.adapter.GenresAdapter
import com.abidzar.themoviedb.viewmodel.GenreViewModel

class GenresFragment : Fragment(), GenresAdapter.OnItemClickListener {

    private lateinit var genreViewModel: GenreViewModel

    lateinit var genresRepository: GenresRepository

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var genreList: List<Genre>

    private var _binding: FragmentGenresBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGenresBinding.inflate(inflater, container, false)

        val apiService: Service = Instance.getInstance()

        genresRepository = GenresRepository(apiService)

        viewModelFactory = GenreViewModelFactory(genresRepository)
        genreViewModel = getViewModel()

        genreViewModel.genres.observe(viewLifecycleOwner, Observer {
            val genresAdapter = GenresAdapter(requireContext(), it.genres, this)

            genreList = it.genres

            val linearLayoutManager = LinearLayoutManager(requireContext())

            val recyclerView: RecyclerView = binding.rvGenres

            recyclerView.layoutManager = linearLayoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = genresAdapter

        })

        genreViewModel.networkState.observe(viewLifecycleOwner, Observer {

        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getViewModel(): GenreViewModel {
        return  ViewModelProvider(this, viewModelFactory).get(GenreViewModel::class.java)
    }

    override fun onItemClick(position: Int) {
//        Toast.makeText(context, "Item ${genreList[position].name} clicked", Toast.LENGTH_SHORT).show()
        // Use the Kotlin extension in the fragment-ktx artifact
        activity?.supportFragmentManager!!.setFragmentResult("requestKey", bundleOf("genreName" to genreList[position].name, "genreId" to genreList[position].id))
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

}

class GenreViewModelFactory(private var genresRepository: GenresRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GenreViewModel(genresRepository) as T
    }
}