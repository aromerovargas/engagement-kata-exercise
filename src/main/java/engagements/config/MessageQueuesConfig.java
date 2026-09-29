package engagements.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MessageQueuesConfig {

    private static final int QUEUE_CAPACITY = 100;

    @Bean(name = "updated_product_template_queue")
    public QueueChannel updatedProductTemplateQueue() {
        return new QueueChannel(QUEUE_CAPACITY);
    }

    @Bean(name = "update_engagement_files_summary_queue")
    public QueueChannel updateEngagementFilesSummaryQueue() {
        return new QueueChannel(QUEUE_CAPACITY);
    }

    @Bean(name = "summaryTaskExecutor")
    public TaskExecutor summaryTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setThreadNamePrefix("summary-");
        executor.initialize();
        return executor;
    }
}
