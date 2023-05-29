package di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.omh.android.auth.api.OmhAuthClient;
import com.omh.android.auth.api.OmhAuthProvider;
import com.omh.android.auth.sample.BuildConfig;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SingletonModule {

    @NonNull
    @Provides
    static OmhAuthClient providesOmhAuthClient(@ApplicationContext Context context) {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");
        scopes.add("profile");
        OmhAuthProvider omhAuthProvider = new OmhAuthProvider.Builder()
                .addNonGmsPath()
                .build();
        return omhAuthProvider.provideAuthClient(context, scopes, BuildConfig.CLIENT_ID);
    }
}
