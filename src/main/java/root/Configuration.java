package root;

import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.context.annotation.Bean;
import root.main.DataController;
import root.main.DataModel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@org.springframework.context.annotation.Configuration
public class Configuration {


    @Bean
    DataModel dataModel() {
        return new DataModel(1000);
    }

    @Bean
    ThreadPoolExecutor backgroundExecutor() {
        return new ThreadPoolExecutor( 1, 1, 0L, TimeUnit.MILLISECONDS, new TaskQueue() );
    }
}
