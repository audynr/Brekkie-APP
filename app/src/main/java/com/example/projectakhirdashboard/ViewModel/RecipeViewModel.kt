import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.projectakhirdashboard.Data.RecipeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val _recipeData = MutableStateFlow(RecipeData(recipes = emptyMap()))
    val recipeData: StateFlow<RecipeData> = _recipeData.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        val context = getApplication<Application>().applicationContext
        try {
            val inputStream = context.assets.open("recipes.json")
            val reader = InputStreamReader(inputStream)

            val gson = Gson()
            val type = object : TypeToken<RecipeData>() {}.type
            val data: RecipeData = gson.fromJson(reader, type)

            _recipeData.value = data
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
