package northstar.planner.metrics;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;


public class MetricsLogger {

    private MobileAnalyticsManager analytics;

    public MetricsLogger(MobileAnalyticsManager analytics) {
        this.analytics = analytics;
    }

    public void recordStart() {
        AnalyticsEvent event =  analytics.getEventClient().createEvent(AnalyticsEventType.STARTED.toString());
        submitEvent(event);
    }

    public void recordEvent(AnalyticsEventType eventType, AnalyticsEventAttribute attribute, String attributeValue) {
        AnalyticsEvent event =  analytics.getEventClient()
                .createEvent(eventType.toString())
                .withAttribute(attribute.toString(), attributeValue);
        submitEvent(event);
    }

    private void submitEvent(AnalyticsEvent event)  {
        analytics.getEventClient().recordEvent(event);
        analytics.getEventClient().submitEvents();
    }
}
