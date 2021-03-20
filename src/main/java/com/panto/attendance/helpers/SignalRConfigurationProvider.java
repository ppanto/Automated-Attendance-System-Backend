package com.panto.attendance.helpers;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:signalr.properties")
@Data
public class SignalRConfigurationProvider {

        private String signalRApiKey;
        private String signalRServiceBaseEndpoint;

        @Autowired
        public SignalRConfigurationProvider(@Value("${signalRApiKey}") String signalRApiKey,
                                            @Value("${signalRServiceBaseEndpoint}") String signalRServiceBaseEndpoint) {
            this.signalRApiKey = signalRApiKey;
            this.signalRServiceBaseEndpoint = signalRServiceBaseEndpoint;
        }

}
