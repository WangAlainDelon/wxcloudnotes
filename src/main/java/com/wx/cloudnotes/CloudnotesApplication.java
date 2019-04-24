package com.wx.cloudnotes;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudnotesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudnotesApplication.class, args);
    }

    //初始化bean
    /*@Bean
    public HbaseTemplate hbaseTemplate(@Value("${hbase.zookeeper.quorum}") String quorum,
                                       @Value("${hbase.zookeeper.port}") String port) {
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", quorum);
        conf.set("hbase.zookeeper.port", port);
        hbaseTemplate.setConfiguration(conf);
        hbaseTemplate.setAutoFlush(true);
        return hbaseTemplate;
    }*/

}
