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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        // Inicializa el adaptador y le pasa las acciones para los botones.
        reposAdapter = ReposAdapter(
            onEditClick = { repo -> handleEditRepo(repo) },
            onDeleteClick = { repo -> handleDeleteRepo(repo) }
        )
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                if (response.isSuccessful) {
                    response.body()?.let { reposAdapter.updateRepositories(it) }
                } else {
                    showMessage("Error al cargar repositorios: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
                showMessage("Fallo en la conexión")
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }

    // Abre el formulario para editar un repositorio, pasando sus datos.
    private fun handleEditRepo(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra("REPO_OWNER", repo.owner.login)
            putExtra("REPO_NAME", repo.name)
            putExtra("REPO_DESCRIPTION", repo.description)
        }
        startActivity(intent)
    }

    // Muestra un diálogo de confirmación antes de eliminar.
    private fun handleDeleteRepo(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Eliminar el repositorio '${repo.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepository(repo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Llama a la API para eliminar el repositorio y actualiza la lista.
    private fun deleteRepository(repo: Repo) {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.deleteRepo(repo.owner.login, repo.name)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado")
                    fetchRepositories() // Refresca la lista.
                } else {
                    showMessage("Error al eliminar: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fallo en la conexión al eliminar")
            }
        })
    }
}
