package com.example.coursemanagement;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursemanagement.databinding.ActivityTrailerPlayerBinding;

public class TrailerPlayerActivity extends AppCompatActivity {

    private ActivityTrailerPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrailerPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        String trailerUrl = getIntent().getStringExtra("TRAILER_URL");
        String title = getIntent().getStringExtra("TITLE");

        if (title != null && !title.isEmpty()) {
            binding.toolbar.setTitle("Trailer: " + title);
        }

        if (trailerUrl == null || trailerUrl.trim().isEmpty()) {
            showError("Địa chỉ URL video không hợp lệ.");
            return;
        }

        setupVideoPlayback(trailerUrl.trim());
    }

    private void setupVideoPlayback(String url) {
        if (isYouTubeUrl(url) || !isDirectMediaUrl(url)) {
            playWithWebView(url);
        } else {
            playWithVideoView(url);
        }
    }

    private boolean isYouTubeUrl(String url) {
        String lower = url.toLowerCase();
        return lower.contains("youtube.com") || lower.contains("youtu.be");
    }

    private boolean isDirectMediaUrl(String url) {
        String lower = url.toLowerCase();
        return lower.endsWith(".mp4") || lower.endsWith(".3gp") || lower.endsWith(".mkv") || lower.endsWith(".webm");
    }

    private void playWithVideoView(String url) {
        binding.videoView.setVisibility(View.VISIBLE);
        binding.webView.setVisibility(View.GONE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);

        Uri uri = Uri.parse(url);
        binding.videoView.setVideoURI(uri);

        binding.videoView.setOnPreparedListener(mp -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.videoView.start();
        });

        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            binding.progressBar.setVisibility(View.GONE);
            playWithWebView(url);
            return true;
        });
    }

    private void playWithWebView(String url) {
        binding.webView.setVisibility(View.VISIBLE);
        binding.videoView.setVisibility(View.GONE);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String pageUrl) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 80) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        if (url.contains("youtube.com/watch?v=")) {
            String videoId = url.substring(url.indexOf("v=") + 2);
            int ampersandIndex = videoId.indexOf("&");
            if (ampersandIndex != -1) {
                videoId = videoId.substring(0, ampersandIndex);
            }
            url = "https://www.youtube.com/embed/" + videoId + "?autoplay=1";
        } else if (url.contains("youtu.be/")) {
            String videoId = url.substring(url.lastIndexOf("/") + 1);
            url = "https://www.youtube.com/embed/" + videoId + "?autoplay=1";
        }

        binding.webView.loadUrl(url);
    }

    private void showError(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (binding.webView != null) {
            binding.webView.destroy();
        }
        super.onDestroy();
    }
}
