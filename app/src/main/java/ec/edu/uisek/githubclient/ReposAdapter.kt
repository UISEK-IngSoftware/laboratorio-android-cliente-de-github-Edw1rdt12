package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoitemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposViewHolder(
    private val binding: FragmentRepoitemBinding,
    // Recibe la acción a ejecutar al pulsar "editar".
    private val onEditClick: (Repo) -> Unit,
    // Recibe la acción a ejecutar al pulsar "eliminar".
    private val onDeleteClick: (Repo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description
        binding.repoLang.text = repo.language

        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .circleCrop()
            .into(binding.repoOwnerImage)

        // Asigna la acción de edición al botón correspondiente.
        binding.editButton.setOnClickListener { onEditClick(repo) }
        // Asigna la acción de eliminación al botón correspondiente.
        binding.deleteButton.setOnClickListener { onDeleteClick(repo) }
    }
}

class ReposAdapter(
    // Define el callback para la acción de editar.
    private val onEditClick: (Repo) -> Unit,
    // Define el callback para la acción de eliminar.
    private val onDeleteClick: (Repo) -> Unit
) : RecyclerView.Adapter<ReposViewHolder>() {

    private var repositories: List<Repo> = emptyList()

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        // Pasa las acciones (callbacks) al ViewHolder.
        return ReposViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}
