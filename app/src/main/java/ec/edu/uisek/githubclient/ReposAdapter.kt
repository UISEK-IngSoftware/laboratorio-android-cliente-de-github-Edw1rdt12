package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoitemBinding
import ec.edu.uisek.githubclient.models.Repo

// ViewHolder para un solo repositorio, maneja la vinculación de datos y los clics. parcial 1
class ReposViewHolder(
    private val binding: FragmentRepoitemBinding,
    private val onEditClick: (Repo) -> Unit,      // Clic de editar. parcial 1
    private val onDeleteClick: (Repo) -> Unit   // Clic de eliminar. parcial 1
) : RecyclerView.ViewHolder(binding.root) {

    // Vincula los datos de un repositorio a las vistas. parcial 1
    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description
        binding.repoLang.text = repo.language

        // Carga la imagen del propietario con Glide. parcial 1
        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)

        // Asigna las acciones de clic a los botones. parcial 1
        binding.editButton.setOnClickListener { onEditClick(repo) }
        binding.deleteButton.setOnClickListener { onDeleteClick(repo) }
    }
}

// Adaptador para el RecyclerView de repositorios. parcial 1
class ReposAdapter(
    private val onEditClick: (Repo) -> Unit,    // Callback para editar. parcial 1
    private val onDeleteClick: (Repo) -> Unit // Callback para eliminar. parcial 1
) : RecyclerView.Adapter<ReposViewHolder>() {

    // Lista de repositorios que muestra el adaptador. parcial 1
    private var repositories: List<Repo> = emptyList()

    // Devuelve la cantidad de elementos en la lista. parcial 1
    override fun getItemCount(): Int = repositories.size

    // Crea un nuevo ViewHolder cuando el RecyclerView lo necesita. parcial 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        // Pasa las lambdas de clic al ViewHolder. parcial 1
        return ReposViewHolder(binding, onEditClick, onDeleteClick)
    }

    // Vincula los datos de un repositorio específico con un ViewHolder. parcial 1
    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    // Actualiza la lista de repositorios y notifica al RecyclerView para que se redibuje. parcial 1
    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}
