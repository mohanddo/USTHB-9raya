import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.R
import com.example.usthb9raya.adapters.AdapterModuleFavorites
import com.example.usthb9raya.dataClass.FavoriteModule
import com.example.usthb9raya.dataClass.Module
import com.example.usthb9raya.databinding.FragmentFavoritesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterModuleFavorites
    private val moduleList: MutableList<Module> = mutableListOf() // Initialize with your data

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = AdapterModuleFavorites(moduleList, requireContext())
        binding.recyclerViewModulesFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewModulesFavorites.adapter = adapter
    }

    private fun loadData() {
        val file = File(requireContext().filesDir, "favorites.json")
        if (file.exists()) {
            val json = file.readText()
            val type: Type = object : TypeToken<List<FavoriteModule>>() {}.type
            val favoriteModules: List<FavoriteModule> = Gson().fromJson(json, type)
            moduleList.clear()
            moduleList.addAll(favoriteModules.map { it.module }) // Assuming FavoriteModule contains a Module
            adapter.notifyDataSetChanged()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




