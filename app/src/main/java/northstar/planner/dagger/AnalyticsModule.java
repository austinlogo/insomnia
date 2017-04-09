package northstar.planner.dagger;

import android.app.Application;

import javax.inject.Singleton;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import dagger.Module;
import dagger.Provides;


@Module
public class AnalyticsModule {

    Application ctx;

    public AnalyticsModule(Application ctx) {
        this.ctx = ctx;
    }

    @Provides
    @Singleton
    MobileAnalyticsManager providesMobileAnalyticsManager() {
        return MobileAnalyticsManager.getOrCreateInstance(
                ctx,
                "4f1f46523e924c1fba80e9ce0a75b85b", //Amazon Mobile Analytics App ID
                "us-east-1:2ffbcb00-99bc-4e03-982f-c8d24ef87013" //Amazon Cognito Identity Pool ID
        );
    }
}
