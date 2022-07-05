package dio.me.si.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

import dio.me.si.R;
import dio.me.si.data.MatchesAPI;
import dio.me.si.databinding.ActivityMainBinding;
import dio.me.si.domain.Match;
import dio.me.si.ui.adapter.MatchesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MatchesAPI matchesApi;
    private MatchesAdapter matchesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
       binding= ActivityMainBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());

        setupHttpClient();
        setupMatchesList();
        setupMatchesRefresh();
        setupFloatingActionButton();

    }

    private void setupHttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://github.com/Darllan123/matches-simulator-api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
       matchesApi= retrofit.create(MatchesAPI.class);
        
    }

    private void setupMatchesList() {
        binding.rvMatches.setHasFixedSize(true);
      binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));
        findMatchesFromAPI();
    }

    private void findMatchesFromAPI() {
        binding.srlMatches.setRefreshing(true);
        matchesApi.getMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful()){
                    List<Match>matches = response.body();
                    matchesAdapter = new MatchesAdapter(matches);
                    binding.rvMatches.setAdapter(matchesAdapter);
                }else {
                    showErrorMessage();
                }
                binding.srlMatches.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                showErrorMessage();
                binding.srlMatches.setRefreshing(false);
            }

        });
    }

    private void showErrorMessage() {
        Snackbar.make(binding.fabMatches, R.string.error_api,Snackbar.LENGTH_LONG).show();
    }

    private void setupMatchesRefresh() {
        binding.srlMatches.setOnRefreshListener(this::findMatchesFromAPI);


    }

    private void setupFloatingActionButton() {
        binding.fabMatches.setOnClickListener(view ->
      view.animate().rotationBy(360).setDuration(500).setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              Random random = new Random();
              for (int i = 0; i <matchesAdapter.getItemCount() ; i++) {
                  Match match = matchesAdapter.getMatches().get(i);
                  match.getHomeTeam().setScore(random.nextInt(match.getHomeTeam().getStars()+1));
                  match.getAwayTeam().setScore(random.nextInt(match.getAwayTeam().getStars()+1));
                  matchesAdapter.notifyItemChanged(i);
              }
          }
      })
                );
    }
}
