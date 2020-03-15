package com.service.stream.starter;

import com.service.context.StreamContext;
import org.springframework.transaction.annotation.Transactional;

public interface StreamContentInjector {

    void injectStreamContent(String streamName);

}
