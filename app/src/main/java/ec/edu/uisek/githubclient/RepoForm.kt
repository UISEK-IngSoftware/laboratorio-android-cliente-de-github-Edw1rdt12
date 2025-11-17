package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.NewRepo
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Actividad para crear o editar un repositorio.
class RepoForm : AppCompatActivity() {
    private lateinit var binding: ActivityRepoFormBinding
    private var repoOwner: String? = null          // Propietario del repositorio (para editar)
    private var originalRepoName: String? = null // Nombre original del repo (para editar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibe los datos si se está editando un repositorio.
        repoOwner = intent.getStringExtra("REPO_OWNER")
        originalRepoName = intent.getStringExtra("REPO_NAME")

        // Si hay un nombre de repo original, es una edición. Rellena los campos.
        if (repoOwner != null && originalRepoName != null) {
            binding.repoNameInput.setText(originalRepoName)
            binding.repoDescriptionInput.setText(intent.getStringExtra("REPO_DESCRIPTION"))
        }

        // Listener para el botón de guardar.
        binding.saveRepoButton.setOnClickListener {
            val repoName = binding.repoNameInput.text.toString()
            val repoDescription = binding.repoDescriptionInput.text.toString()

            if (repoName.isNotEmpty()) {
                val repoData = NewRepo(repoName, repoDescription)
                // Si existe un nombre de repo original, actualiza. Si no, crea.
                if (repoOwner != null && originalRepoName != null) {
                    updateRepository(repoOwner!!, originalRepoName!!, repoData)
                } else {
                    createRepository(repoData)
                }
            } else {
                showMessage("El nombre del repositorio no puede estar vacío")
            }
        }
    }

    // Llama a la API para crear un nuevo repositorio.
    private fun createRepository(newRepo: NewRepo) {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.createRepo(newRepo)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    finish() // Cierra la actividad.
                } else {
                    showMessage("Error al crear el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo al crear el repositorio")
            }
        })
    }

    // Llama a la API para actualizar un repositorio existente.
    private fun updateRepository(owner: String, repoName: String, repoData: NewRepo) {
        val apiService: GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.updateRepo(owner, repoName, repoData)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado exitosamente")
                    finish() // Cierra la actividad.
                } else {
                    showMessage("Error al actualizar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo al actualizar el repositorio")
            }
        })
    }

    // Muestra un mensaje Toast.
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
