package com.c4.hero.common.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class GoogleCalendarConfig {

    @Value("${google.calendar.credentials-path}")
    private Resource credentialsResource;

    @Bean
    public Calendar googleCalendarClient() throws Exception {
        InputStream in = credentialsResource.getInputStream();

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of(CalendarScopes.CALENDAR));

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        return new Calendar.Builder(
                transport,
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        )
                .setApplicationName("HERO")
                .build();
    }
}
