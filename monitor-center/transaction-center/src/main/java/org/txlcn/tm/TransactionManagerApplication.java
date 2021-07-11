package org.txlcn.tm;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author codingapi
 * https://www.txlcn.org/zh-cn/docs/demo/env.html
 */
@SpringBootApplication
@EnableTransactionManagerServer
public class TransactionManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionManagerApplication.class, args);
    }
}
