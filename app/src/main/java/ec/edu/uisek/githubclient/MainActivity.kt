package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Actividad principal: muestra la lista de repositorios del usuario.
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView() // Configura el RecyclerView y su adaptador.

        // Lanza el formulario para crear un nuevo repositorio.
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    // Refresca la lista de repositorios cuando la actividad vuelve a estar visible.
    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    // Inicializa el ReposAdapter con las acciones de clic
    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onEditClick = { repo -> handleEditRepo(repo) },      // Acción para editar
            onDeleteClick = { repo -> handleDeleteRepo(repo) }    // Acción para eliminar
        )
        binding.reposRecyclerView.adapter = reposAdapter
    }

    // Obtiene los repositorios del usuario desde la API de GitHub.
    private fun fetchRepositories() {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos) // Actualiza el adaptador.
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
                showMessage("No se pudieron cargar los repositorio")
            }
        })
    }

    // Muestra un mensaje Toast.
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Inicia la actividad del formulario para un nuevo repositorio.
    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }

    // Inicia el formulario para editar un repositorio existente.
    private fun handleEditRepo(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra("REPO_OWNER", repo.owner.login)
            putExtra("REPO_NAME", repo.name)
            putExtra("REPO_DESCRIPTION", repo.description)
        }
        startActivity(intent)
    }

    // Muestra un diálogo de confirmación para eliminar un repositorio.
    private fun handleDeleteRepo(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el repositorio '${repo.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepository(repo) // Llama a la función de eliminación.
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Llama a la API para eliminar un repositorio.
    private fun deleteRepository(repo: Repo) {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.deleteRepo(repo.owner.login, repo.name)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado exitosamente")
                    fetchRepositories() // Refresca la lista.
                } else {
                    showMessage("Error al eliminar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fallo al eliminar el repositorio")
            }
        })
    }
}
