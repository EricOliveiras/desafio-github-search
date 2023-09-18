package br.com.igorbag.githubsearch.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.openBrowser
import br.com.igorbag.githubsearch.ui.shareRepositoryLink

class RepositoryAdapter(
    private val context: Context,
    private val repositories: List<Repository>,
    private val openBrowser: (Repository) -> Unit,
    private val btnShareLister: (Repository) -> Unit
) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]

        holder.repositoryName.text = repository.name

        holder.cardItem.setOnClickListener {
            openBrowser(context, repository.htmlUrl)
        }

        holder.sharedButton.setOnClickListener {
            shareRepositoryLink(context, repository.htmlUrl)
        }
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repositoryName: TextView
        val sharedButton: ImageView
        val cardItem: CardView

        init {
            view.apply {
                repositoryName = findViewById(R.id.tv_repository_name)
                sharedButton = findViewById(R.id.iv_share)
                cardItem = findViewById(R.id.cv_repositories)
            }
        }
    }
}


