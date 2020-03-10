package com.example.skye_stagram.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skye_stagram.R;
import com.example.skye_stagram.models.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>
{
    Context _context;
    List<Post> _postList;

    public PostsAdapter(Context context, List<Post> posts)
    {
        this._context = context;
        this._postList = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.bind(_postList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return _postList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivPostImage;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
        }

        public void bind(Post post)
        {
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());

            if (post.getImage() != null)
            {
                Glide.with(_context)
                        .load(post.getImage().getUrl())
                        .into(ivPostImage);
            }
        }
    }

    public void clear()
    {
        _postList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts)
    {
        _postList.addAll(posts);
        notifyDataSetChanged();
    }
}
