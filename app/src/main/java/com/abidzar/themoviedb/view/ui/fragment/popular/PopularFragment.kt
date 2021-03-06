package com.abidzar.themoviedb.view.ui.fragment.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abidzar.themoviedb.databinding.FragmentHomeBinding
import com.abidzar.themoviedb.model.network.Instance
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.model.repository.PopularPagedListRepository
import com.abidzar.themoviedb.view.adapter.PopularMoviePagedListAdapter
import com.abidzar.themoviedb.viewmodel.PopularViewModel

class PopularFragment : Fragment() {

    private lateinit var popularViewModel: PopularViewModel

    lateinit var moviePopularRepository: PopularPagedListRepository

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiService: Service = Instance.getInstance()

        moviePopularRepository = PopularPagedListRepository(apiService)

        viewModelFactory = HomeViewModelFactory(moviePopularRepository)
        popularViewModel = getViewModel()

        val movieAdapter = PopularMoviePagedListAdapter(requireContext())

        val gridLayoutManager =GridLayoutManager(requireContext(), 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType : Int = movieAdapter.getItemViewType(position)

                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return 1
                else return 3
            }
        }

        val recyclerView: RecyclerView = binding.rvPopular
        val progressBarPopular: ProgressBar = binding.progressBarPopular
        val txvErrorPopular: TextView = binding.txvErrorPopular

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = movieAdapter

        popularViewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
            movieAdapter.submitList(it)
        })

        popularViewModel.networkState.observe(viewLifecycleOwner, Observer {
            progressBarPopular.visibility = if (popularViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txvErrorPopular.visibility = if (popularViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (!popularViewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })

        return root
    }

    private fun getViewModel(): PopularViewModel {
        return  ViewModelProvider(this, viewModelFactory).get(PopularViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class HomeViewModelFactory(private var moviePopularRepository: PopularPagedListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PopularViewModel(moviePopularRepository) as T
    }
}