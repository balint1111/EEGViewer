package root.async;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import root.main.DataController;

import java.io.IOException;

@Component
public class AsyncExecutor {

    private final DataController dataController;

    public AsyncExecutor(@Lazy DataController dataController) {
        this.dataController = dataController;
    }


    @Async
    public void preLoadAroundPage(int numberOfPages) throws Exception {
        System.out.println("asyncExecution");
        dataController.preLoadAroundPage(numberOfPages);
        System.out.println("end");
    }

}
