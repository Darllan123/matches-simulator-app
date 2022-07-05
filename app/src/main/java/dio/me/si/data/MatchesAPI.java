package dio.me.si.data;

import java.util.List;

import dio.me.si.domain.Match;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MatchesAPI {
    @GET("matches.json")
    Call<List<Match>> getMatches();
}
