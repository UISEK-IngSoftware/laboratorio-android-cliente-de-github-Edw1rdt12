package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.NewRepo
import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    // 1. LEER/LISTAR Repositorios (GET)
    @GET("user/repos")
    fun getRepos(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ) : Call<List<Repo>>

    // 2. CREAR Repositorio (POST)
    @POST("user/repos")
    fun createRepo(
        @Body repo: NewRepo
    ) : Call<Repo>

    // 3. ACTUALIZAR Repositorio (PATCH)
    // Se usa la ruta completa del repositorio: repos/NOMBRE_USUARIO/NOMBRE_REPO
    @PATCH("repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String, // El nombre de usuario (dueño del repo)
        @Path("repo") repoName: String, // El nombre actual del repositorio
        @Body repo: NewRepo // El body con los nuevos datos (nombre, descripción, etc.)
    ): Call<Repo>

    // 4. ELIMINAR Repositorio (DELETE)
    // Se usa la ruta completa del repositorio. Retorna Call<Void> o Call<Unit> porque el cuerpo
    // de la respuesta HTTP 204 (éxito de DELETE) está vacío.
    @DELETE("repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String, // El nombre de usuario (dueño del repo)
        @Path("repo") repoName: String // El nombre del repositorio a eliminar
    ): Call<Void>
}