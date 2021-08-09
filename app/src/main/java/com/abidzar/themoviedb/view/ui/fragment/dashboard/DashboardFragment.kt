package com.abidzar.themoviedb.view.ui.fragment.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.databinding.FragmentDashboardBinding
import com.abidzar.themoviedb.model.network.Instance
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.repository.DiscoverPagedListRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.view.adapter.PopularMoviePagedListAdapter
import com.abidzar.themoviedb.view.ui.fragment.genres.GenresFragment
import com.abidzar.themoviedb.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    lateinit var movieDiscoverRepository: DiscoverPagedListRepository

    lateinit var viewModelFactory: ViewModelProvider.Factory

    var genreId: Int = 28

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiService: Service = Instance.getInstance()

        movieDiscoverRepository = DiscoverPagedListRepository(apiService)

        viewModelFactory = DiscoverViewModelFactory(movieDiscoverRepository, genreId)
        dashboardViewModel = getViewModel()

        val movieAdapter = PopularMoviePagedListAdapter(requireContext())

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType : Int = movieAdapter.getItemViewType(position)

                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return 1
                else return 3
            }
        }

        val recyclerView: RecyclerView = binding.rvDiscover
        val progressBarDiscover: ProgressBar = binding.progressBarDiscover
        val txvErrorDiscover: TextView = binding.txvErrorDiscover
        val genre: LinearLayout = binding.genreSpinner

        genre.setOnClickListener(View.OnClickListener {
            val genreFragment = GenresFragment()

            val transaction: FragmentTransaction= activity?.supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, genreFragment)
            transaction.commit()
        })

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = movieAdapter

        dashboardViewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
            movieAdapter.submitList(it)
        })

        dashboardViewModel.networkState.observe(viewLifecycleOwner, Observer {
            progressBarDiscover.visibility = if (dashboardViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txvErrorDiscover.visibility = if (dashboardViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (!dashboardViewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getViewModel(): DashboardViewModel {
        return  ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
    }
}

class DiscoverViewModelFactory(private var movieDiscoverRepository: DiscoverPagedListRepository, private var genreId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DashboardViewModel(movieDiscoverRepository, genreId) as T
    }
}