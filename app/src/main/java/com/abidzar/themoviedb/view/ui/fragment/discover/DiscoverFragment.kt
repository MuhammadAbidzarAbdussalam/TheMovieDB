package com.abidzar.themoviedb.view.ui.fragment.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
import com.abidzar.themoviedb.viewmodel.DiscoverViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DiscoverFragment : Fragment() {

    private lateinit var discoverViewModel: DiscoverViewModel

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
        discoverViewModel = getViewModel()

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
            transaction.addToBackStack(null)
            transaction.commit()
        })

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = movieAdapter

        discoverViewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
            println("WHEN AM I CALLED")
            movieAdapter.submitList(it)
        })

        discoverViewModel.networkState.observe(viewLifecycleOwner, Observer {
            progressBarDiscover.visibility = if (discoverViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txvErrorDiscover.visibility = if (discoverViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (!discoverViewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })

        activity?.supportFragmentManager!!.setFragmentResultListener("requestKey", this) { key, bundle ->
            val genreName = bundle.getString("genreName")
            val genreIdBundle = bundle.getInt("genreId")
            // Do something with the result
            println("genreName $genreName")
            println("genreId $genreIdBundle")
            txvGenre.text = genreName
            genreId = genreIdBundle

            movieAdapter.submitList(null)

            discoverViewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
                println("WHEN AM I CALLED 22")
                movieAdapter.submitList(it)
            })

            Toast.makeText(context, genreName, Toast.LENGTH_LONG).show()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getViewModel(): DiscoverViewModel {
        return  ViewModelProvider(this, viewModelFactory).get(DiscoverViewModel::class.java)
    }
}

class DiscoverViewModelFactory(private var movieDiscoverRepository: DiscoverPagedListRepository, private var genreId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DiscoverViewModel(movieDiscoverRepository, genreId) as T
    }
}