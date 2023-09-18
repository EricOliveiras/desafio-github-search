package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import br.com.igorbag.githubsearch.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var nomeUsuario: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var listaRepositories: RecyclerView
    private lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        githubApi = NetworkUtils.getRetrofitInstance("https://api.github.com/").create(GitHubService::class.java)
        setupListeners()
    }

    private fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            val userName = nomeUsuario.text.toString()
            getAllReposByUserName(userName)
            saveUserLocal()
        }
    }

    private fun saveUserLocal() {
        val userInput = nomeUsuario.text.toString()
        val sharedPref = getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.saved_name), userInput)
            apply()
        }
        Log.d("sharedPref", "saveUserLocal: $sharedPref")
    }

    private fun showUserName() {
        val sharedPref = getPreferences(MODE_PRIVATE) ?: return
        val savedUser = sharedPref.getString(getString(R.string.saved_name), null)

        if (!savedUser.isNullOrEmpty()) {
            nomeUsuario.setText(savedUser)
        }
    }

    private fun getAllReposByUserName(userName: String) {
        githubApi.getAllRepositoriesByUser(userName).enqueue(object: Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositories = response.body()
                    repositories?.let {
                        setupAdapter(repositories)
                    }
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                println("ERROR::BAD_REQUEST")
            }
        })
    }

    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(
            this,
            list,
            { repository -> openBrowser(this, repository.htmlUrl) },
            { repository -> shareRepositoryLink(this, repository.htmlUrl) }
        )
        listaRepositories.adapter = adapter
    }
}
fun shareRepositoryLink(context: Context, urlRepository: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, urlRepository)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun openBrowser(context: Context, urlRepository: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlRepository))
    context.startActivity(browserIntent)
}
